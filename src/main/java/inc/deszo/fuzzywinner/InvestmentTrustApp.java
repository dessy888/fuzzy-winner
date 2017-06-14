package inc.deszo.fuzzywinner;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientOptions;
import inc.deszo.fuzzywinner.model.fund.Fund;
import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;
import inc.deszo.fuzzywinner.model.fund.FundInfos;
import inc.deszo.fuzzywinner.repository.fund.FundHistoryPricesRepository;
import inc.deszo.fuzzywinner.repository.fund.FundInfosRepository;
import inc.deszo.fuzzywinner.repository.fund.FundPerformanceRepository;
import inc.deszo.fuzzywinner.repository.fund.FundRepository;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.JsonUtils;
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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class InvestmentTrustApp implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(InvestmentTrustApp.class);

  private RestTemplate restTemplate;

  @Autowired
  private FundRepository fundRepository;

  @Autowired
  private FundInfosRepository fundInfosRepository;

  @Autowired
  private FundHistoryPricesRepository fundHistoryPricesRepository;

  @Autowired
  private FundPerformanceRepository fundPerformanceRepository;

  @Autowired
  private MongoTemplate mongoTemplate;

  public static void main(String[] args) {
    SpringApplication.run(InvestmentTrustApp.class, args);
  }

  @PostConstruct
  public void init() throws Exception {
    restTemplate = new RestTemplate();
  }

  @Override
  public void run(String... args) throws Exception {

    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

    logger.info("Work in Progress.");
  }

  private void setup(boolean deleteAll) {
    if (deleteAll) {
      fundRepository.deleteAll();
      fundInfosRepository.deleteAll();
      fundHistoryPricesRepository.deleteAll();
      fundPerformanceRepository.deleteAll();
    }
  }

  private void loadInvestmentTrusts() throws IOException, ParseException {

    int numOfInvestmentTrustUpdated = 0;
    int numOfInvestmentTrustCompany = 0;
    int investmentTrustCound = 1;

    //Get list of companyIds
    Document doc = Jsoup.connect("http://www.hl.co.uk/shares/investment-trusts/search-for-investment-trusts?it_search_input=&companyid=150&tab=prices&sectorid=&tab=prices").timeout(0).get();
    Element content = doc.getElementById("companyid");
    Elements options = content.getElementsByTag("option");

    for (Element option : options) {

      if (option.val() == "")
        continue;

      logger.info("Loading Investment Trusts for companyIds: {}, {}", option.val(), option.text());

      int page = 1;
      int totalPages;
      String companyId = option.val();

      do {

        //TODO: Continue here!!!
        String result = restTemplate.postForObject("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?companyid=" + companyId + "&lo=0&page=" + page + "&SQ_DESIGN_NAME=json", null, String.class);
        //String result = restTemplate.postForObject("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?sectorid=" + companyId + "&lo=0&page=1&SQ_DESIGN_NAME=json", null, String.class);
        logger.debug(result);

        //Get results from page
        final JsonNode arrNodeResults = JsonUtils.getMAPPER().readTree(result).get("results");
        if (arrNodeResults.isArray()) {
          for (final JsonNode objNode : arrNodeResults) {

            logger.debug("Loading Fund({}): {}", fundCount, objNode.toString());
            Fund newFund = JsonUtils.getMAPPER().treeToValue(objNode, Fund.class);
            newFund.setKey();

            logger.info("Loading Fund({}): {} {}", fundCount, newFund.getSedol(), newFund.getName());

            String sedol = newFund.getSedol();
            String updated = newFund.getUpdatedLocalDateString();

            Fund loadedFund = fundRepository.findFundBySedolUpdated(sedol, DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT));
            if (loadedFund == null) {
              logger.info("Saving Fund {} {} for {}.", newFund.getSedol(), newFund.getName(), newFund.getUpdatedLocalDateString());
              fundRepository.save(newFund);
              numOfFundsUpdated++;
            } else {
              logger.info("Fund {} {} already updated on {}.", loadedFund.getSedol(), loadedFund.getName(), loadedFund.getUpdatedLocalDateString());
            }

            if (updatePlusFund) {
              //update plusFund
              if (fundInfosRepository.updatePlusFund(sedol, newFund.getPlusFund(), DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT)) == 1) {
                logger.info("PlusFund for sedol {}: {} updated.", sedol, newFund.getPlusFund());
              } else {
                logger.info("PlusFund for sedol {}: {} NOT updated.", sedol, newFund.getPlusFund());
              }
            }

            if (updateFundInfos) {
              //update inceptionDate and isin
              updateFundInfos(sedol);
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

      numOfFundCompany++;
    }

    logger.info("*****Number of Fund Companies Loaded: {}, Number of Funds Updated: {}", numOfFundCompany, numOfFundsUpdated);
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

