package inc.deszo.fuzzywinner.investmenttrust;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientOptions;
import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.fund.model.FundMapping;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrustHistoryPrice;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrustMapping;
import inc.deszo.fuzzywinner.investmenttrust.repository.InvestmentTrustHistoryPricesRepository;
import inc.deszo.fuzzywinner.investmenttrust.repository.InvestmentTrustMappingsRepository;
import inc.deszo.fuzzywinner.investmenttrust.repository.InvestmentTrustsRepository;
import inc.deszo.fuzzywinner.shared.model.Type;
import inc.deszo.fuzzywinner.utils.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@SpringBootApplication
public class InvestmentTrustApp implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(InvestmentTrustApp.class);

  private RestTemplate restTemplate;

  @Autowired
  private InvestmentTrustsRepository investmentTrustsRepository;

  @Autowired
  private InvestmentTrustMappingsRepository investmentTrustMappingsRepository;

  @Autowired
  private InvestmentTrustHistoryPricesRepository investmentTrustHistoryPricesRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  private static int numOfInvestmentTrustUpdated = 0;
  private static int numOfInvestmentTrustCompany = 0;
  private static int investmentTrustCount = 0;

  private Semaphore semaphore = new Semaphore(1);

  @PostConstruct
  public void init() {
    restTemplate = new RestTemplate();
  }

    public static void main(String[] args) {
    SpringApplication.run(InvestmentTrustApp.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

    loadInvestmentTrusts(true);

    updateInvestmentTrustsHistoryPrices();

    investmentTrustsRepository.genCsvInvestmentReport();

    SoundUtils.playSound();
  }

  private void loadInvestmentTrusts(boolean reloadCobData) throws IOException {

    //Get list of companyIds
    Document doc = Jsoup.connect("http://www.hl.co.uk/shares/investment-trusts/search-for-investment-trusts?it_search_input=&companyid=150&tab=prices&sectorid=&tab=prices").timeout(0).get();
    Element content = doc.getElementById("companyid");
    Elements options = content.getElementsByTag("option");

    ExecutorService executor = Executors.newFixedThreadPool(10);

    for (Element option : options) {

      if (option.val().equalsIgnoreCase("")) {
        continue;
      }

      String companyId = option.val();
      String companyName = option.text();

      logger.info("Loading Investment Trusts for companyIds: {}, {}", companyId, companyName);

      Runnable longRunningTask = () -> {
        try {
          loadCompanyInvestmentTrust(reloadCobData, companyId, companyName);
        } catch (IOException | ParseException e) {
          logger.error("BOOM!", e);
        }
      };

      executor.submit(longRunningTask);

      synchronized (this) {
        numOfInvestmentTrustCompany++;
      }
    }

    executor.shutdown();
    //noinspection StatementWithEmptyBody
    while (!executor.isTerminated()) {
    }

    logger.info("*****Number of Investment Trust Companies Loaded: {}, Number of Funds Updated: {}",
        numOfInvestmentTrustCompany, numOfInvestmentTrustUpdated);
  }

  private void loadCompanyInvestmentTrust(boolean reloadCobData, String companyId,
                                          String companyName) throws IOException, ParseException {

    Document doc;
    InvestmentTrust investmentTrust;
    int currentInvestmentTrustCount;

    logger.info(Thread.currentThread().getName() + "started.");

    doc = Jsoup.connect("http://www.hl.co.uk/shares/investment-trusts/search-for-investment-trusts?it_search_input=&companyid=" +
        companyId + "&tab=prices&sectorid=&tab=prices").timeout(0).get();
    logger.debug(doc.html());

    //get identifier, title & description
    Elements searchResults = doc.select("table[summary='Investment trust search results']").first().child(1).getElementsByTag("tr");
    logger.debug(searchResults.html());

    for (Element searchResult : searchResults) {

      synchronized (this) {
        investmentTrustCount++;
        currentInvestmentTrustCount = investmentTrustCount;
      }

      investmentTrust = new InvestmentTrust();
      Elements rowData = searchResult.select("td");
      String href = rowData.select("a[href]").first().attr("href");

      investmentTrust.setIdentifier(rowData.get(0).text());
      investmentTrust.setTitle(rowData.get(1).text());
      investmentTrust.setDescription(rowData.get(2).text());
      investmentTrust.setCompany(companyName);
      investmentTrust.setSedol(href.substring(href.lastIndexOf("/") + 1));

      //check if it has already been saved for this CobDate
      List<InvestmentTrust> savedInvestmentTrust = investmentTrustsRepository.getLastUpdated(investmentTrust.getSedol());
      if (savedInvestmentTrust.size() > 0) {
        if (savedInvestmentTrust.get(0).getUpdatedLocalDateString().equalsIgnoreCase(DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT))) {
          if (!reloadCobData) {
            logger.info("Skip Loading {}: {}, {}, {}, {}.", currentInvestmentTrustCount, investmentTrust.getIdentifier(),
                investmentTrust.getTitle(), investmentTrust.getDescription(), investmentTrust.getSedol());
            continue;
          }
        }
      }

      // now get more detail data for each investment trust
      // http://www.hl.co.uk/shares/shares-search-results/XXXXX
      doc = Jsoup.connect("http://www.hl.co.uk/shares/shares-search-results/"
          + investmentTrust.getSedol()).timeout(0).get();

      if (!doc.select("h1:contains(Market data not available)").isEmpty()) {
        logger.info("No Marktet data for {}: {}, {}, {}, {}.", currentInvestmentTrustCount, investmentTrust.getIdentifier(),
            investmentTrust.getTitle(), investmentTrust.getDescription(), investmentTrust.getSedol());
      } else {
        logger.info("Loading {}: {}, {}, {}, {}, {}.", currentInvestmentTrustCount, investmentTrust.getIdentifier(),
            investmentTrust.getTitle(), investmentTrust.getDescription(), investmentTrust.getSedol(),
            investmentTrust.getIsin());

        investmentTrust.setIsin(doc.select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(ISIN)").get(0).parent().child(1).text());

        /*
        String priceChange = "";
        if (doc.select("span[class='change-divide'] span[class='nochange change']").size() > 0) {
          priceChange = "0";
        } else if (doc.select("span[class='change-divide'] span[class='negative change']").size() > 0) {
          priceChange = "-" + doc.select("span[class='change-divide'] span[class='negative change']").get(0).text();
        } else if (doc.select("span[class='change-divide'] span[class='positive change']").size() > 0) {
          priceChange = "-" + doc.select("span[class='change-divide'] span[class='positive change']").get(0).text();
        }*/

        Elements securityDetails = doc.select("div[id='security-detail'] div[class='row'] div[class='columns large-3 medium-4 small-6']");
        logger.debug(securityDetails.html());

        investmentTrust.setCurrency(securityDetails.select("span:contains(Currency)").first().parent().child(2).text());

        investmentTrust.setEstimatedNav(MathUtils.convert(securityDetails.select("span:contains(Estimated Nav)")
            .first().parent().child(2).text()));

        investmentTrust.setPremiumDiscount(MathUtils.convert(securityDetails.select("span:contains(Premium/Discount)")
            .first().parent().child(2).text()));

        investmentTrust.setPriceBuy(CurrencyUtils.getAmount(doc.select("span[class='ask price-divide']").get(0).text(),
            investmentTrust.getCurrency()));

        investmentTrust.setPriceSell(CurrencyUtils.getAmount(doc.select("span[class='bid price-divide']").get(0).text(),
            investmentTrust.getCurrency()));

        String priceChange;
        if (securityDetails.select("span:contains(Previous)").last().parent().child(2).attr("class")
            .equalsIgnoreCase("negative change")) {
          priceChange = "-" + securityDetails.select("span:contains(Previous)").last().parent().child(2).text();
        } else {
          priceChange = "-" + securityDetails.select("span:contains(Previous)").last().parent().child(2).text();
        }
        investmentTrust.setPriceChange(CurrencyUtils.getAmount(priceChange, investmentTrust.getCurrency()));

        investmentTrust.setDividendYield(MathUtils.convert(securityDetails
            .select("span:contains(Dividend yield)").first().parent().child(2).text()));

        investmentTrust.setLatestActualNav(MathUtils.convert(doc
            .select("span:contains(Latest actual NAV)").get(0).parent().parent()
            .siblingElements().text()));

        investmentTrust.setLatestActualNavDate(doc
            .select("span:contains(Latest actual NAV date)").get(0).parent().parent()
            .siblingElements().text());

        investmentTrust.set_12mAvgPremiumDiscount(MathUtils.convert(doc
            .select("span:contains(12m average Premium/Discount)").get(0).parent().parent()
            .siblingElements().text()));

        investmentTrust.setNavFrequency(doc
            .select("span:contains(NAV frequency)").get(0).parent().parent()
            .siblingElements().text());

        investmentTrust.setPerformanceFee(doc
            .select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(Performance fee)")
            .get(0).parent().child(1).text());

        investmentTrust.setOngoingCharge(MathUtils.convert(doc
            .select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(Ongoing charge)")
            .get(0).parent().child(1).text()));

        investmentTrust.setDividendFrequency(doc
            .select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(Dividend frequency)")
            .get(0).parent().child(1).text());

        investmentTrust.setTotalAssets(MathUtils.convert(doc
            .select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(Total assets)")
            .get(0).parent().child(1).text()));

        investmentTrust.setGrossGearing(MathUtils.convert(doc
            .select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(Gross gearing)")
            .get(0).parent().child(1).text()));

        investmentTrust.setMarketCap(doc
            .select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(Market capitalisation)")
            .get(0).parent().child(1).text());

        investmentTrust.setSharesInIssue(doc
            .select("table[class='factsheet-table table-no-border spacer-bottom'] th:contains(Shares in issue)")
            .get(0).parent().child(1).text());

        investmentTrust.setLaunchDate(doc
            .select("th:contains(Launch date:)").get(0).parent().child(1).text());

        if (doc.select("span[class='stream_msg_closed dNone']").hasText()) {
          String msgClosedText = doc.select("span[class='stream_msg_closed dNone']").first().text();
          investmentTrust.setUpdated(msgClosedText.substring(msgClosedText.indexOf("on") + 3));
        } else {
          investmentTrust.setUpdated(DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT));
        }

        investmentTrust.setUrl();
        investmentTrust.setKey();

        if (investmentTrustsRepository.findInvestmentTrustByKey(investmentTrust.getKey()) == null) {
          investmentTrustsRepository.save(investmentTrust);
          logger.info("Saved Investment Trust {}: {}", currentInvestmentTrustCount, investmentTrust.toString());
        } else {
          if (investmentTrustsRepository.update(investmentTrust) == 1) {
            logger.info("Updated Investment Trust {}: {}", currentInvestmentTrustCount, investmentTrust.toString());
          }
        }

        InvestmentTrustMapping investmentTrustMapping = new InvestmentTrustMapping(investmentTrust.getSedol(),
            investmentTrust.getIsin(), investmentTrust.getLaunchDateLocalDateString(),
            DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT));

        if (investmentTrustMappingsRepository.findInvestmentTrustMappingBySedol(investmentTrust.getSedol()) == null) {
          investmentTrustMappingsRepository.save(investmentTrustMapping);
          logger.info("Saved Investment Trust Mapping: {}", investmentTrustMapping.getSedol());
        } else {
          if (investmentTrustMappingsRepository.update(investmentTrustMapping) == 1) {
            logger.info("Updated Investment Trust Mapping: {}", investmentTrustMapping.getSedol());
          }
        }

        synchronized (this) {
          numOfInvestmentTrustUpdated++;
        }
      }
    }

    logger.info(Thread.currentThread().getName() + "ended.");
  }

  private void updateInvestmentTrustsHistoryPrices() throws IOException, ParseException {

    int numOfInvestmentTrustLoaded = 0;
    int investmentTrustCount = 0;

    List<InvestmentTrustMapping> investmentTrustMappings = investmentTrustMappingsRepository
        .findAllDistinct(new Sort(Sort.Direction.DESC, "sedol"));

    for (InvestmentTrustMapping investmentTrustMapping : investmentTrustMappings) {

      investmentTrustCount++;
      logger.info("Investment Trust {}, Sedol {}.", investmentTrustCount,
          investmentTrustMapping.getSedol());

      if (investmentTrustMapping.getInceptionDate() == null) {
        logger.info("No inception date for Sedol: {} {} {} hence skipped!", investmentTrustMapping.getSedol(),
            investmentTrustMapping.getIsin(), investmentTrustMapping.getFtSymbol());
        continue;
      }

      logger.info("Starting download of historical prices for: {}, {}, {}, {}", investmentTrustMapping.getSedol(),
          investmentTrustMapping.getIsin(), investmentTrustMapping.getInceptionLocalDate(),
          investmentTrustMapping.getFtSymbol());

      numOfInvestmentTrustLoaded += updateInvestmentTrustHistoryPrices(investmentTrustMapping.getSedol(),
          investmentTrustMapping.getIsin(), investmentTrustMapping.getInceptionLocalDate(),
          investmentTrustMapping.getFtSymbol());
      logger.info("Download completed for: {}", investmentTrustMapping.getSedol());
    }

    logger.info("*****Investment Trusts History Prices download for {} investment trusts.", numOfInvestmentTrustLoaded);
  }

  private int updateInvestmentTrustHistoryPrices(String sedol, String isin, String inceptionDate,
                                                 String ftSymbol) throws IOException, ParseException {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    ObjectMapper mapper = JsonUtils.getMAPPER();
    JsonFactory factory = mapper.getFactory();

    boolean updateInceptionDateFormat = false;

    boolean pricesSaved = false;

    if (inceptionDate == null || ftSymbol == null) {

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("query", isin);

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

      ResponseEntity<String> ftCompanyLookupResponse = restTemplate.
          postForEntity("https://markets.ft.com/data/searchapi/fullsitesearch", request,
              String.class);

      JsonParser ftCompanyLookupParser = factory.createParser(ftCompanyLookupResponse.getBody());
      JsonNode ftCompanyLookupJnode = mapper.readTree(ftCompanyLookupParser);

      JsonNode securities = ftCompanyLookupJnode.get("data").get("security");
      String investmentTrustUrl = "";
      String ftStockSymbol = "";
      for (JsonNode security : securities) {
        if (security.get("url").textValue().contains("investment-trust")) {
          investmentTrustUrl = security.get("url").textValue();
          ftStockSymbol = investmentTrustUrl.substring(investmentTrustUrl.indexOf("=")+1);
          logger.info("Found Investment Trust {}: {}", sedol, investmentTrustUrl);
        }
      }

      if (investmentTrustUrl.isEmpty()) {
        logger.info("Investment Trust not found on FT!: {}", sedol);
        return 0;
      }

      //now get the inception date and ftsymbol.
      Document ftHistoricDoc = Jsoup.connect("https://markets.ft.com/data/investment-trust/tearsheet/historical?s=" +
          ftStockSymbol).timeout(0).get();
      String ftHistoricalPricesAppElement = ftHistoricDoc.select("div[data-module-name='HistoricalPricesApp']")
          .attr("data-mod-config");
      JsonParser ftHistoricalPricesAppElementParser = factory.createParser(ftHistoricalPricesAppElement);
      JsonNode ftHistoricalPricesAppJnode = mapper.readTree(ftHistoricalPricesAppElementParser);

      if (ftSymbol == null) {
        try {
          ftSymbol = ftHistoricalPricesAppJnode.get("symbol").textValue();
        } catch (NullPointerException npe) {
          logger.error("ftSymbol NOT FOUND!");
          return 0;
        }

        if (investmentTrustMappingsRepository
            .updateFtSymbol(sedol, ftSymbol, DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT)) == 1) {
          logger.info("FT Symbol for sedol {}: {} updated.", sedol, ftSymbol);
        }
      }

      // only update with FT inceptionDate if it is null. HL ones are more accurate.
      if (inceptionDate == null) {
        String inceptionISODate = null;

        try {
          inceptionISODate = ftHistoricalPricesAppJnode.get("inception").textValue();
        } catch (NullPointerException npe) {
          logger.error("Can't find Inception Date, hence will try Launch Date.");
        }

        if (inceptionISODate != null) {
          inceptionDate = DateUtils.getDateByIsoDate(inceptionISODate, DateUtils.STANDARD_FORMAT);

          if (investmentTrustMappingsRepository
              .updateInceptionDate(sedol, inceptionDate, DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT)) == 1) {
            logger.info("Inception date for sedol {}: {} updated.", sedol, inceptionDate);
          }

          inceptionDate = DateUtils.getDatefromFormat(inceptionDate, DateUtils.STANDARD_FORMAT, DateUtils.FT_FORMAT);
        } else {
          logger.info("No inceptionDate, hence skipped.");
          return 0;
        }
      } else {
        updateInceptionDateFormat = true;
      }
    } else {
      updateInceptionDateFormat = true;
    }

    if (updateInceptionDateFormat) {
      inceptionDate = DateUtils.getDatefromFormat(inceptionDate, DateUtils.STANDARD_FORMAT, DateUtils.FT_FORMAT);
    }

    logger.info("ISIN {}, inception: {}, symbol: {}", isin, inceptionDate, ftSymbol);

    //now get the historical prices year by year

    //check the last date it is in the database
    String startDate;
    List<InvestmentTrustHistoryPrice> fundHistoryPrices = investmentTrustHistoryPricesRepository
        .getLastUpdated(sedol, isin, ftSymbol);
    if (fundHistoryPrices.size() > 0) {
      startDate = DateUtils.getNextWorkingDate(fundHistoryPrices.get(0).getCobDate(),
          DateUtils.FT_FORMAT);

      logger.info("Fund history prices already updated. Will update from: {}", startDate);
    } else {
      startDate = inceptionDate;
      logger.info("Fund prices will updated from: {}", startDate);
    }

    String endDate = DateUtils.getEndDateForHistoricalPrices(startDate, DateUtils.FT_FORMAT);

    while (DateUtils.isLessThanOrEqualToDate(startDate, DateUtils.getTodayDate(DateUtils.FT_FORMAT), DateUtils.FT_FORMAT)) {
      logger.info("Loading Historical Prices from {} to {}", startDate, endDate);

      String historicalPriceAppURL = "https://markets.ft.com/data/equities/ajax/get-historical-prices?startDate=" +
          URLEncoder.encode(startDate, "UTF-8") + "&endDate=" + URLEncoder.encode(endDate, "UTF-8") + "&symbol=" + ftSymbol;
      logger.info("Loading Historical Prices using URL: {}", historicalPriceAppURL);

      MultiValueMap<String, String> historicalPriceAppMap = new LinkedMultiValueMap<>();
      historicalPriceAppMap.add("startDate", startDate);
      historicalPriceAppMap.add("endDate", endDate);
      historicalPriceAppMap.add("symbol", ftSymbol);

      HttpEntity<MultiValueMap<String, String>> historicalPriceAppRequest = new
          HttpEntity<>(historicalPriceAppMap, headers);

      ResponseEntity<String> ftHistoricalPriceAppResponse = restTemplate
          .postForEntity("https://markets.ft.com/data/equities/ajax/get-historical-prices",
              historicalPriceAppRequest, String.class);
      JsonParser ftHistoricalPriceParser = factory.createParser(ftHistoricalPriceAppResponse.getBody());
      JsonNode ftHistoricalPriceJNode = mapper.readTree(ftHistoricalPriceParser);

      Document ftHistoricalPriceDoc = Jsoup.parse("<html><body><table>" + ftHistoricalPriceJNode.get("html").asText() +
          "</table></body></html>");
      Elements ftHistoricalPriceElements = ftHistoricalPriceDoc.select("tr");

      for (Element ftHistoricalPriceElement : ftHistoricalPriceElements) {
        String cobDate = DateUtils.getDatefromFormat(ftHistoricalPriceElement.
                select("span[class='mod-ui-hide-small-below']").first().text(), "EEEE, MMMM d, y",
            DateUtils.STANDARD_FORMAT);

        Double price_open = Double.valueOf(ftHistoricalPriceElement.select("td").get(1).text().replace(",", ""));
        Double price_high = Double.valueOf(ftHistoricalPriceElement.select("td").get(2).text().replace(",", ""));
        Double price_low = Double.valueOf(ftHistoricalPriceElement.select("td").get(3).text().replace(",", ""));
        Double price_close = Double.valueOf(ftHistoricalPriceElement.select("td").get(4).text().replace(",", ""));
        Double volume = Double.valueOf(ftHistoricalPriceElement.select("td").get(5).text().replace(",", ""));

        logger.info("Date: {}, Price= Open: {}, High: {}, Low: {}, Close: {}, Volume: {}", cobDate,
            price_open, price_high, price_low, price_close, volume);

        InvestmentTrustHistoryPrice investmentTrustHistoryPrice = new InvestmentTrustHistoryPrice(sedol,
            isin, ftSymbol, price_open, price_high, price_low, price_close, volume, cobDate);
        investmentTrustHistoryPricesRepository.save(investmentTrustHistoryPrice);
        pricesSaved = true;
      }

      startDate = DateUtils.addDayToDate(endDate, DateUtils.FT_FORMAT, 1);
      endDate = DateUtils.getEndDateForHistoricalPrices(endDate, DateUtils.FT_FORMAT);
    }

    return (pricesSaved) ? 1 : 0;
  }

  @Bean
  public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
                                     MongoMappingContext context) {

    MappingMongoConverter converter =
        new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));

    return new MongoTemplate(mongoDbFactory, converter);
  }

  static class OptionsConfig {

    @Bean
    public MongoClientOptions mongoOptions() {
      return MongoClientOptions.builder()
          .socketTimeout(30000)
          .connectTimeout(30000)
          .serverSelectionTimeout(30000).build();
    }
  }
}

