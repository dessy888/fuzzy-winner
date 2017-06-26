package inc.deszo.fuzzywinner.investmenttrust;

import com.mongodb.MongoClientOptions;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;
import inc.deszo.fuzzywinner.investmenttrust.repository.InvestmentTrustRepository;
import inc.deszo.fuzzywinner.utils.CurrencyUtils;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.MathUtils;
import inc.deszo.fuzzywinner.utils.SoundUtils;
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
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@SpringBootApplication
public class InvestmentTrustApp implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(InvestmentTrustApp.class);

  @Autowired
  private InvestmentTrustRepository investmentTrustRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  private static int numOfInvestmentTrustUpdated = 0;
  private static int numOfInvestmentTrustCompany = 0;
  private static int investmentTrustCount = 0;

  private Semaphore semaphore = new Semaphore(1);

  public static void main(String[] args) {
    SpringApplication.run(InvestmentTrustApp.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

    loadInvestmentTrusts(true);

    investmentTrustRepository.genCsvInvestmentReport();

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
          e.printStackTrace();
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

  private void loadCompanyInvestmentTrust(boolean reloadCobData, String companyId, String companyName) throws IOException, ParseException {

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
      List<InvestmentTrust> savedInvestmentTrust = investmentTrustRepository.getLastUpdated(investmentTrust.getSedol());
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

        if (investmentTrustRepository.findInvestmentTrustByKey(investmentTrust.getKey()) == null) {
          investmentTrustRepository.save(investmentTrust);
          logger.info("Saving Investment Trust {}: {}", currentInvestmentTrustCount, investmentTrust.toString());
        } else {
          investmentTrustRepository.updateInvestmentTrust(investmentTrust);
          logger.info("Updating Investment Trust {}: {}", currentInvestmentTrustCount, investmentTrust.toString());
        }

        synchronized (this) {
          numOfInvestmentTrustUpdated++;
        }
      }
    }

    logger.info(Thread.currentThread().getName() + "ended.");
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

