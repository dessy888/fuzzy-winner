package inc.deszo.fuzzywinner.fund;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientOptions;
import inc.deszo.fuzzywinner.fund.model.*;
import inc.deszo.fuzzywinner.fund.repository.*;
import inc.deszo.fuzzywinner.shared.model.Type;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.JsonUtils;
import inc.deszo.fuzzywinner.utils.SoundUtils;
import org.jsoup.HttpStatusException;
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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

@SpringBootApplication
public class FundApp implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(FundApp.class);

  private RestTemplate restTemplate;

  @Autowired
  private FundsRepository fundsRepository;

  @Autowired
  private FundMappingsRepository fundMappingsRepository;

  @Autowired
  private FundHistoryPricesRepository fundHistoryPricesRepository;

  @Autowired
  private FundPerformancesRepository fundPerformancesRepository;

  @Autowired
  private FundTopHoldingsRepository fundTopHoldingsRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  public static void main(String[] args) {
    SpringApplication.run(FundApp.class, args);
  }

  @PostConstruct
  public void init() {
    restTemplate = new RestTemplate();
  }

  @Override
  public void run(String... args) throws Exception {

    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

    setup(false);

    // set updateFundInfo to true very first time repo is populated
    loadFunds(true, false, false, true, 10);

    updateFundsHistoryPrices(false, 10);

    runStatistics(DateUtils.getTodayDate(), false, true);

    /*
    rerunStatistics("15/12/2017", "11/02/2018", false, false);
    */

    repairStatistics("_ALL", -95.0);
    repairStatistics("_1D", -99.0);

    genFundReports("2017-06-01");

    SoundUtils.playSound();
  }

  private void setup(boolean deleteAll) {
    if (deleteAll) {
      fundsRepository.deleteAll();
      fundMappingsRepository.deleteAll();
      fundHistoryPricesRepository.deleteAll();
      fundPerformancesRepository.deleteAll();
      fundTopHoldingsRepository.deleteAll();
    }
  }

  private void loadFunds(boolean reloadFund, boolean updateFundMappings, boolean updateFundTopHoldings, boolean updatePlusFund, int numOfThreads) throws IOException, ExecutionException, InterruptedException {

    int numOfFundCompany = 0;
    int numOfFundsUpdated = 0;

    //Update fund keys
    //logger.info("Number of funds key updated: {}.", fundsRepository.updateKey());

    //Get list of companyIds
    Document doc = Jsoup.connect("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?companyid=218&lo=0&page=1&tab=prices").timeout(0).get();
    Element content = doc.getElementById("search-company");
    Elements optgroups = content.getElementsByTag("optgroup");
    Elements options = optgroups.last().getElementsByTag("option");

    // if by sector
    //Element content = doc.getElementById("search-sector");
    //Elements options = content.getElementsByTag("option");

    ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
    Set<Future<Integer>> set = new HashSet<Future<Integer>>();

    for (Element option : options) {

      if (option.val().equalsIgnoreCase(""))
        continue;

      Callable<Integer> longRunningTask = () -> {
        int updated = 0;

        try {

          updated = loadFund(reloadFund, updateFundMappings, updateFundTopHoldings, updatePlusFund, option);

        } catch (IOException | ParseException e) {
          logger.error("BOOM!", e);
        }

        return updated;
      };

      synchronized (this) {
        numOfFundCompany++;
      }

      Future<Integer> future = executor.submit(longRunningTask);
      set.add(future);
    }

    executor.shutdown();
    //noinspection StatementWithEmptyBody
    while (!executor.isTerminated()) {
    }

    for (Future<Integer> future : set) {
      numOfFundsUpdated += future.get();
    }

    logger.info("*****Number of Fund Companies Loaded: {}, Number of Funds Updated: {}", numOfFundCompany, numOfFundsUpdated);
  }

  private int loadFund(boolean reloadFund, boolean updateFundMappings, boolean updateFundTopHoldings, boolean updatePlusFund, Element option) throws IOException, ParseException {

    int numOfFundsUpdated = 0;
    int fundCount = 1;

    logger.info("Loading Funds for companyIds: {}, {}", option.val(), option.text());

    int page = 1;
    int totalPages;
    String companyId = option.val();

    do {

      String result = restTemplate.postForObject("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?companyid=" + companyId + "&lo=0&page=" + page + "&SQ_DESIGN_NAME=json", null, String.class);
      logger.debug(result);

      //Get results from page
      final JsonNode arrNodeResults = JsonUtils.getMAPPER().readTree(result).get("results");
      if (arrNodeResults.isArray()) {
        for (final JsonNode objNode : arrNodeResults) {

          Fund newFund;
          logger.debug("Loading Fund({}): {}", fundCount, objNode.toString());
          try {
            newFund = JsonUtils.getMAPPER().treeToValue(objNode, Fund.class);
            newFund.setKey();
            newFund.setUrl();
          } catch (JsonMappingException jme) {
            logger.error("BOOM! {}", objNode.asText(), jme);
            continue;
          }

          logger.info("Loading Fund({}): {} {}", fundCount, newFund.getSedol(), newFund.getName());

          String sedol = newFund.getSedol();
          String updated = newFund.getUpdatedLocalDateString();

          Fund loadedFund = fundsRepository.findFundBySedolUpdated(sedol, DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT));
          if (loadedFund == null) {
            fundsRepository.save(newFund);
            logger.info("Saved Fund {} {} for {}.", newFund.getSedol(), newFund.getName(), newFund.getUpdatedLocalDateString());
            numOfFundsUpdated++;
          } else {
            if (reloadFund) {
              fundsRepository.updateFund(newFund);
              logger.info("Updated Fund {} {} for {}.", newFund.getSedol(), newFund.getName(), newFund.getUpdatedLocalDateString());
              numOfFundsUpdated++;
            } else {
              logger.info("Fund {} {} already updated on {}.", loadedFund.getSedol(), loadedFund.getName(), loadedFund.getUpdatedLocalDateString());
            }
          }

          if (updatePlusFund) {
            //update plusFund
            if (fundMappingsRepository.updatePlusFund(sedol, newFund.getPlusFund(), DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT)) == 1) {
              logger.info("PlusFund for sedol {}: {} updated.", sedol, newFund.getPlusFund());
            } else {
              logger.info("PlusFund for sedol {}: {} NOT updated.", sedol, newFund.getPlusFund());
            }
          }

          if (updateFundMappings) {
            //update inceptionDate and isin
            updateFundMapping(sedol);
          }

          if (updateFundTopHoldings) {
            //update fund top holdings
            updateFundsTopHoldings(sedol);
          }

          fundCount++;

        }
        logger.info("Funds processed for companyId: {}, {}", option.val(), option.text());
      }

      //Check if there are more pages
      JsonNode arrNodePages = JsonUtils.getMAPPER().readTree(result).get("pages");
      totalPages = arrNodePages.asInt();

      page++;

    } while (page <= totalPages);

    return numOfFundsUpdated;
  }

  private void updateFundsMappings(boolean onlyNewOnes, List<String> sedols) throws ParseException {

    int numOfISINLoaded = 0;
    int fundCount = 0;

    if (onlyNewOnes) {
      for (String sedol : sedols) {
        fundCount++;
        logger.info("Fund {}, Sedol {}.", fundCount, sedol);
        if (fundMappingsRepository.findFirstBySedol(sedol) == null) {
          if (updateFundMapping(sedol)) {
            numOfISINLoaded++;
          }
        } else {
          logger.info("Sedol: {} already added before.", sedol);
        }
      }
    } else {
      for (String sedol : sedols) {
        fundCount++;
        logger.info("Fund {}, Sedol {}.", fundCount, sedol);
        updateFundMapping(sedol);
        numOfISINLoaded++;
      }
    }

    logger.info("*****Number of updated ISIN: {}", numOfISINLoaded);
  }

  private boolean updateFundMapping(String sedol) throws ParseException {

    boolean success = false;

    // now get the isin into fundmapping
    logger.info("Loading Fund ISIN using sedol: {}", sedol);

    try {
      Document searchDoc = Jsoup.connect("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results/" + sedol).timeout(0).get();
      Element launchDateElement = searchDoc.select("th[class='align-left']:contains(Fund launch date:)").first().parent().child(1);
      String launchDate = launchDateElement.text().trim();
      String redirected = searchDoc.location();

      //update isin
      Document keyFeaturesDoc;
      keyFeaturesDoc = Jsoup.connect(redirected + "/key-features").timeout(0).get();

      logger.debug("Key Features: {}", keyFeaturesDoc.html());

      Element isinElement = keyFeaturesDoc
          .select("th[class='align-left']:contains(ISIN code:)").first().parent().child(1);
      String isin = isinElement.text().trim();

      if (isin.isEmpty()) {
        logger.debug("ISIN for SEDOL {} NOT found!", sedol);
      } else {
        logger.debug("ISIN: {} for SEDOL {} found.", isin, sedol);

        FundMapping newFundMapping = new FundMapping(sedol, isin,
            DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT));

        // check if fundInfo exist?
        FundMapping loadedFundMapping = fundMappingsRepository.findISIN(sedol, isin);
        if (loadedFundMapping == null) {
          fundMappingsRepository.save(newFundMapping);
          logger.info("ISIN: {} for SEDOL {} saved.", isin, sedol);
          success = true;
        } else {
          logger.info("ISIN: {} for SEDOL {} already exist.", isin, sedol);
        }
      }

      //update InceptionDate from HL first otherwise use the ones from FT
      if (!launchDate.equalsIgnoreCase("n/a")) {
        String inceptionDate = DateUtils.getDatefromFormat(launchDate, "d MMMM yyyy", DateUtils.STANDARD_FORMAT);
        if (fundMappingsRepository.updateInceptionDate(sedol, inceptionDate, DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT)) == 1) {
          logger.info("Inception date for sedol {}: {} updated.", sedol, inceptionDate);
        } else {
          logger.info("Inception date for sedol {}: {} NOT updated.", sedol, inceptionDate);
        }
      } else {
        logger.info("Inception date for sedol {}: {} NOT updated.", sedol, launchDate);
      }

    } catch (HttpStatusException | NullPointerException e) {
      logger.error("No market data for sedol {}.", sedol);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return success;
  }

  private void updateFundsHistoryPrices(boolean onlyPlusFunds, int numOfThreads) throws ExecutionException, InterruptedException {

    int numOfFundsLoaded = 0;
    int fundCount = 0;

    ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
    Set<Future<Integer>> set = new HashSet<Future<Integer>>();

    List<FundMapping> fundMappings;

    if (onlyPlusFunds) {
      fundMappings = fundMappingsRepository.findPlusFunds(new Sort(Sort.Direction.DESC, "sedol"));
    } else {
      fundMappings = fundMappingsRepository.findAllDistinct(new Sort(Sort.Direction.DESC, "sedol"));
    }

    for (FundMapping fundMapping : fundMappings) {

      fundCount++;
      logger.info("Fund {}, Sedol {}.", fundCount, fundMapping.getSedol());

      if (fundMapping.getInceptionDate() == null) {
        logger.info("No inception date for Sedol: {} {} {} {} hence skipped!", fundMapping.getSedol(),
            fundMapping.getIsin(), fundMapping.getFtSymbol(), fundMapping.getPlusFund());
        continue;
      }

      logger.info("Starting download of historical prices for: {}, {}, {}, {}, {}", fundMapping.getSedol(),
          fundMapping.getIsin(), fundMapping.getInceptionLocalDate(), fundMapping.getFtSymbol(), fundMapping.getPlusFund());


      Callable<Integer> longRunningTask = () -> {
        int updatedCount = 0;

        try {

          updatedCount += updateFundHistoryPrices(fundMapping.getSedol(), fundMapping.getIsin(),
          fundMapping.getInceptionLocalDate(), fundMapping.getFtSymbol());

          logger.info("Download completed for: {}", fundMapping.getSedol());

        } catch (IOException | ParseException e) {
          logger.error("BOOM!", e);
        }

        return updatedCount;
      };

      Future<Integer> future = executor.submit(longRunningTask);
      set.add(future);
    }

    executor.shutdown();
    //noinspection StatementWithEmptyBody
    while (!executor.isTerminated()) {
    }

    for (Future<Integer> future : set) {
      numOfFundsLoaded += future.get();
    }

    logger.info("*****Fund History Prices download for {} funds.", numOfFundsLoaded);
  }

  private int updateFundHistoryPrices(String sedol, String isin, String inceptionDate, String ftSymbol) throws IOException, ParseException {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    ObjectMapper mapper = JsonUtils.getMAPPER();
    JsonFactory factory = mapper.getFactory();

    boolean updateInceptionDateFormat = false;

    boolean pricesSaved = false;

    if (inceptionDate == null || ftSymbol == null) {

      MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
      map.add("searchTerm", isin);

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

      ResponseEntity<String> ftCompanyLookupResponse = restTemplate.
          postForEntity("http://funds.ft.com/Remote/UK/API/CompanyLookup?projectType=GlobalFunds", request,
              String.class);

      JsonParser ftCompanyLookupParser = factory.createParser(ftCompanyLookupResponse.getBody());
      JsonNode ftCompanyLookupJnode = mapper.readTree(ftCompanyLookupParser);

      String ftCompanyLookupResponseHTML = ftCompanyLookupJnode.get("html").toString();
      logger.info("FT Company Lookup Response for {}: {}", isin, ftCompanyLookupResponseHTML);

      Document ftCompanyLookupResponseDoc = Jsoup.parse(ftCompanyLookupResponseHTML);
      Element ftCompanyLookupResponseHref = ftCompanyLookupResponseDoc.select("a[href]").last();

      String ftIsin;
      try {
        ftIsin = ftCompanyLookupResponseHref.html();
        logger.info("FT Company LookUp Response ISIN {}: {}", isin, ftIsin);
      } catch (NullPointerException npe) {
        logger.error("FT Company LookUp Response ISIN {}: NOT FOUND!", isin);
        return 0;
      }

      //now get the inception date and ftsymbol.
      Document ftHistoricDoc = Jsoup.connect("https://markets.ft.com/data/funds/tearsheet/historical?s=" +
          ftIsin).timeout(0).get();
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

        if (fundMappingsRepository.updateFtSymbol(sedol, ftSymbol, DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT)) == 1) {
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

          if (fundMappingsRepository.updateInceptionDate(sedol, inceptionDate, DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT)) == 1) {
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
    List<FundHistoryPrice> fundHistoryPrices = fundHistoryPricesRepository
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

        Double price = Double.valueOf(ftHistoricalPriceElement.select("td").get(4).text().replace(",", ""));

        logger.debug("Date: {}, Price: {}", cobDate, price);

        FundHistoryPrice fundHistoryPrice = new FundHistoryPrice(sedol, isin, ftSymbol,
            price, cobDate);
        fundHistoryPricesRepository.save(fundHistoryPrice);
        pricesSaved = true;
      }

      startDate = DateUtils.addDayToDate(endDate, DateUtils.FT_FORMAT, 1);
      endDate = DateUtils.getEndDateForHistoricalPrices(endDate, DateUtils.FT_FORMAT);

      logger.info("Historical Prices from {} to {} loaded.", startDate, endDate);
    }

    return (pricesSaved) ? 1 : 0;
  }

  private boolean updateFundsTopHoldings(String sedol) throws ParseException {

    boolean success = false;

    logger.info("Loading Fund Top Holding using sedol: {}", sedol);

    try {

      // check if sedol top holdings have been updated today.
      List<FundTopHolding> fundTopHoldings = fundTopHoldingsRepository.getFundTopHoldingsByDate(sedol,
          DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT));

      if (fundTopHoldings.isEmpty()) {

        Document searchDoc = Jsoup.connect("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results/" + sedol).timeout(0).get();
        Element topHoldingsElement = searchDoc.select("table[summary='Top 10 holdings']").first();
        Elements holdings = topHoldingsElement.getElementsByTag("tr");

        for (int i = 1; i < holdings.size(); i++) {

          String security = holdings.get(i).select("td").get(0).text();
          String weight = holdings.get(i).select("td").get(1).text();
          String url = holdings.get(i).select("td").get(0).select("a").attr("href");

          logger.debug("Sedol: {}, Position: {}, Security: {}, Weight: {}, URL: {}", sedol,
              i, security, weight, url);

          FundTopHolding newFundTopHolding = new FundTopHolding(sedol,
              DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT), Type.FUND, String.valueOf(i),
              security, weight, url);
          fundTopHoldingsRepository.save(newFundTopHolding);
          success = true;
        }

      } else {
          logger.info("Fund Top Holding sedol: {} already updated.", sedol);
          success = false;
      }

    } catch (HttpStatusException | NullPointerException e) {
      logger.error("No market data for top holdings {}.", sedol);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return success;
  }

  private void runStatistics(LocalDate cobDate, boolean plusFundOnly, boolean overrideFundPerformance) throws ParseException {

    //calculate fund performance if it is a business day
    if (DateUtils.isWeekDay(cobDate)) {
      fundPerformancesRepository.calculate(cobDate, plusFundOnly, overrideFundPerformance);
    } else {
      logger.info("Run Statistics for {} skipped, not Weekday.", cobDate);
    }

    /*// all funds with yield more than 5% sort by yield and sedol
    AggregationResults<Fund> fundResults = fundsRepository.getFundWithYieldMoreThan(5.0);
    fundResults.forEach((fund) -> logger.info("Funds {} {} {}M {}p {}: {}%", fund.getSedol(), fund.getName(),
        fund.getFundSize(), fund.getPriceSell(), fund.getUpdatedLocalDateString(), fund.getYield()));

    // all plus funds with yield more than 4% sort by yield and sedol
    AggregationResults<Fund> plusFundResults = fundsRepository.getPlusFundWithYieldMoreThan(4.0);
    plusFundResults.forEach((fund) -> logger.info("Plus Funds {} {} {}M {}p {}: {}%", fund.getSedol(), fund.getName(),
        fund.getFundSize(), fund.getPriceSell(), fund.getUpdatedLocalDateString(), fund.getYield()));
*/
    // all updated dates
    List<Date> updatedDates = fundsRepository.getDistinctUpdated();
    updatedDates.forEach((date) -> logger.info("Updated Date {}.", DateUtils.getDate(date, DateUtils.STANDARD_FORMAT)));
  }

  private void rerunStatistics(String startDate, String endDate, boolean plusFundOnly, boolean overrideFundPerformance) throws ParseException {

    long days = DateUtils.diffBetTwoDates(endDate, startDate, DateUtils.STANDARD_FORMAT) + 1;

    logger.info("Rerun Statistics for {} days from {} to {} started.", days, startDate, endDate);

    for (int i=0; i<days; i++) {

      String date = DateUtils.addDayToDate(startDate, DateUtils.STANDARD_FORMAT, i);

      logger.info("Rerun Statistics for {}.", date);

      runStatistics(DateUtils.getLocalDate(date, DateUtils.STANDARD_FORMAT), plusFundOnly, overrideFundPerformance);
    }

    logger.info("Rerun Statistics for {} days from {} to {} ended.", days, startDate, endDate);
  }

  private void repairStatistics(String tenor, double performance) throws IOException, ParseException {

    int deletedCount;
    int updatedCount;
    int totalRepaired = 0;

    logger.info("Repair Statistics by {} tenor where performance is less than {}%.", tenor, performance);

    AggregationResults<FundPerformance> fundPerformances = fundPerformancesRepository.getSedolByTenorPerformance(tenor, performance);

    for (FundPerformance fundPerformance : fundPerformances) {

      logger.info("Fund sedol: {}", fundPerformance.getSedol());

      // remove all history prices
      deletedCount = fundHistoryPricesRepository.deleteFundHistoryPricesBySedol(fundPerformance.getSedol());
      logger.info("Fund sedol: {}, {} History Prices deleted.", fundPerformance.getSedol(), deletedCount);

      // remove all performance cals
      deletedCount = fundPerformancesRepository.deleteFundPerformanceBySedol(fundPerformance.getSedol());
      logger.info("Fund sedol: {}, {} Fund Performances deleted.", fundPerformance.getSedol(), deletedCount);

      // redownload history prices
      FundMapping fundMapping = fundMappingsRepository.findFirstBySedol(fundPerformance.getSedol());
      updatedCount = updateFundHistoryPrices(fundMapping.getSedol(), fundMapping.getIsin(),
          fundMapping.getInceptionLocalDate(), fundMapping.getFtSymbol());
      logger.info("Fund sedol: {}, {} Fund History downloaded.", fundPerformance.getSedol(), updatedCount);

      // calculate perfomance cals again
      String startDate = "01/06/2017";
      String endDate = DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT);
      long days = DateUtils.diffBetTwoDates(endDate, startDate, DateUtils.STANDARD_FORMAT) + 1;
      logger.info("Rerun Statistics for {} days from {} to {} started.", days, startDate, endDate);
      List<FundHistoryPrice> fundHistoryPrices = fundHistoryPricesRepository.getSedol(fundPerformance.getSedol());
      for (int i=0; i<days; i++) {
        String date = DateUtils.addDayToDate(startDate, DateUtils.STANDARD_FORMAT, i);
        logger.info("Rerun Statistics for {}.", date);

        LocalDate cobDate = DateUtils.getLocalDate(date, DateUtils.STANDARD_FORMAT);
        if (DateUtils.isWeekDay(cobDate)) {
          fundPerformancesRepository.calculateFund(fundHistoryPrices.get(0), 0, cobDate, false, false);
        }
      }

      totalRepaired++;
    }

    logger.info("Repair Statistics by {} tenor where performance is less than {}%, total Repaired: {}",
        tenor, performance, totalRepaired);
  }

  private void genFundReports(String startDate) throws IOException {
    fundsRepository.genCsvFundReport(startDate);
    fundTopHoldingsRepository.genCsvTopHoldingsReport("2018-01-27");
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
          .maxWaitTime(0)
          .socketTimeout(0)
          .connectTimeout(0)
          .serverSelectionTimeout(0)
          .build();
    }
  }
}

