package inc.deszo.fuzzywinner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DuplicateKeyException;
import inc.deszo.fuzzywinner.model.Domain;
import inc.deszo.fuzzywinner.model.Fund;
import inc.deszo.fuzzywinner.model.FundInfos;
import inc.deszo.fuzzywinner.repository.DomainRepository;
import inc.deszo.fuzzywinner.repository.FundInfosRepository;
import inc.deszo.fuzzywinner.repository.FundRepository;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.jsoup.HttpStatusException;
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
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.client.RestTemplate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private FundInfosRepository fundInfosRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        loadFunds(false, false);

        updateAllISIN(true);

        // all funds with yield more than 5%
        /*Sort sort = new Sort(Sort.Direction.ASC, "yield");
        List<Fund> funds = fundRepository.findFundsByYield(5.0, sort);
        funds.forEach((fund) -> logger.info("Funds {} {} {}M {}p {}: {}%", fund.getSedol(), fund.getName(),
                fund.getFundSize(), fund.getPrice_sell(), fund.getUpdated(), fund.getYield()));
                */

        // all plus funds with yield more than 4%
        /*List<Fund> plusFunds = fundRepository.findPlusFundsByYield(4.0, sort);
        plusFunds.forEach((fund) -> logger.info("Plus Funds {} {} {}M {}p {}: {}%", fund.getSedol(), fund.getName(),
                fund.getFundSize(), fund.getPrice_sell(), fund.getUpdated(), fund.getYield()));
                */

        // all funds with yield more than 5% sort by yield and sedol
        Aggregation aggFund = newAggregation(
                match(Criteria.where("yield").gt(5.0)), sort(Sort.Direction.DESC, "yield").and(Sort.Direction.ASC, "sedol")
        );
        AggregationResults<Fund> fundResults = mongoTemplate.aggregate(aggFund, Fund.class, Fund.class);
        fundResults.forEach((fund) -> logger.info("Funds {} {} {}M {}p {}: {}%", fund.getSedol(), fund.getName(),
                fund.getFundSize(), fund.getPrice_sell(), fund.getUpdated(), fund.getYield()));

        // all plus funds with yield more than 4% sort by yield and sedol
        Aggregation aggPlusFund = newAggregation(
                match(Criteria.where("yield").gt(4.0)), sort(Sort.Direction.DESC, "yield").and(Sort.Direction.ASC, "sedol")
        );
        AggregationResults<Fund> plusFundResults = mongoTemplate.aggregate(aggPlusFund, Fund.class, Fund.class);
        plusFundResults.forEach((fund) -> logger.info("Funds {} {} {}M {}p {}: {}%", fund.getSedol(), fund.getName(),
                fund.getFundSize(), fund.getPrice_sell(), fund.getUpdated(), fund.getYield()));

        // all updated dates
        List<String> updatedDates = mongoTemplate.getCollection("fund").distinct("updated");
        updatedDates.forEach((date) -> logger.info("Updated Date {}.", date));
    }

    public void loadFunds(boolean deleteAll, boolean updateISIN) throws IOException {

        if (deleteAll) {
            fundRepository.deleteAll();
            fundInfosRepository.deleteAll();
        }

        int numOfFundsUpdated = 0;
        int numOfFundCompany = 0;

        //Get list of companyIds
        Document doc = Jsoup.connect("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?companyid=218&lo=0&page=1&tab=prices").timeout(0).get();
        Element content = doc.getElementById("search-company");
        Elements optgroups = content.getElementsByTag("optgroup");
        Elements options = optgroups.last().getElementsByTag("option");

        for (Element option : options) {
            logger.info("Loading Funds for companyIds: {}, {}", option.val(), option.text());

            String companyId = option.val();
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.postForObject("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?companyid=" + companyId + "&lo=0&page=1&SQ_DESIGN_NAME=json", null, String.class);
            logger.debug(result);

            final JsonNode arrNode = new ObjectMapper().readTree(result).get("results");
            if (arrNode.isArray()) {
                for (final JsonNode objNode : arrNode) {
                    logger.debug("Loading Fund: {}", objNode.toString());
                    Fund newFund = new ObjectMapper().treeToValue(objNode, Fund.class);

                    String sedol = newFund.getSedol();
                    String updated = newFund.getUpdated();

                    Fund loadedFund = fundRepository.updatedFund(sedol, updated);
                    if (loadedFund == null) {
                        logger.info("Saving Fund {} {} for {}.", newFund.getSedol(), newFund.getName(), newFund.getUpdated());
                        fundRepository.save(newFund);
                        numOfFundsUpdated++;
                    } else {
                        logger.info("Fund {} {} already updated on {}.", loadedFund.getSedol(), loadedFund.getName(), loadedFund.getUpdated());
                    }

                    if (updateISIN) {
                        updateISIN(sedol);
                    }
                }
                logger.info("Funds processed for companyId: {}, {}", option.val(), option.text());
            }
            numOfFundCompany++;
        }

        logger.info("Number of Fund Companies Loaded: {}, Number of Funds Updated {}", numOfFundCompany, numOfFundsUpdated);
    }

    public void updateAllISIN(boolean onlyNewOnes) {

        int numOfISINLoaded = 0;

        List<String> sedols = mongoTemplate.getCollection("fund").distinct("sedol");

        if (onlyNewOnes) {
            for (String sedol : sedols) {
                if (fundInfosRepository.findFirstBySedol(sedol) == null) {
                    if (updateISIN(sedol)) {
                        numOfISINLoaded++;
                    }
                } else {
                    logger.info("Sedol: {} already added before.", sedol);
                }
            }
        } else {
            for (String sedol : sedols) {
                updateISIN(sedol);
                numOfISINLoaded++;
            }
        }

        logger.info("Number of updated ISIN: {}", numOfISINLoaded);
    }

    public boolean updateISIN(String sedol) {

        boolean success = false;

        // now get the isin into fundinfos
        logger.info("Loading Fund ISIN using sedol: {}", sedol);

        try {
            Document searchDoc = Jsoup.connect("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results/" + sedol).timeout(0).get();
            String redirected = searchDoc.location();

            Document keyFeaturesDoc = null;

            keyFeaturesDoc = Jsoup.connect(redirected + "/key-features").timeout(0).get();

            logger.debug("Key Features: {}", keyFeaturesDoc.html());

            Element isinElement = keyFeaturesDoc.select("th[class='align-left']:contains(ISIN code:)").first().parent().child(1);
            String isin = isinElement.text().trim();

            if (isin.isEmpty()) {
                logger.debug("ISIN for SEDOL {} NOT found!", sedol);
            } else {
                logger.debug("ISIN: {} for SEDOL {} found.", isin, sedol);

                FundInfos newFundInfos = new FundInfos(sedol, isin, DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT));

                // check if fundInfo exist?
                FundInfos loadedFundInfos = fundInfosRepository.findISIN(sedol, isin);
                if (loadedFundInfos == null) {
                    fundInfosRepository.save(newFundInfos);
                    logger.info("ISIN: {} for SEDOL {} saved.", isin, sedol);
                    success = true;
                } else {
                    logger.info("ISIN: {} for SEDOL {} already exist.", isin, sedol);
                }
            }

        } catch (HttpStatusException hse) {
            logger.info("No market data for sedol {}.", sedol);

        } catch (NullPointerException npe) {
            logger.info("No market data for sedol {}.", sedol);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return success;
    }

   /* @Bean
    CommandLineRunner init(FundRepository fundRepository) {

        return args -> {

            fundRepository.deleteAll();

            Fund newFund = new Fund("B0XWNK3",
                    "",
                    "Aberdeen Asia Pacific and Japan Equity (Class I)",
                    "Accumulation",
                    "Unbundled",
                    "Aberdeen",
                    "Asia Pacific Inc Japan",
                    "false",
                    153.41,
                    0,
                    0.16,
                    1.30,
                    0.00,
                    1.20,
                    0.175,
                    1.025,
                    "lND",
                    "35.98%",
                    "-5.00%",
                    "9.86%",
                    "-1.63%",
                    "18.49%",
                    136.7,
                    "Annually",
                    "Dividend",
                    70,
                    new Date("06/06/2017"));

            fundRepository.save(newFund);

        };

    }*/

   /* @Bean
    CommandLineRunner init(DomainRepository domainRepository) {

        return args -> {

            Domain newObj = new Domain(100, "deszo.inc", true);
            domainRepository.save(newObj);

            Domain obj = domainRepository.findOne(100L);
            System.out.println(obj);

            Domain obj2 = domainRepository.findFirstByDomain("deszo.inc");
            System.out.println(obj2);

            //obj2.setDisplayAds(true);
            //domainRepository.save(obj2);

            //int n = domainRepository.updateDomain("mkyong.com", true);
            //System.out.println("Number of records updated : " + n);

            //Domain obj3 = domainRepository.findOne(2000001L);
            //System.out.println(obj3);

            //Domain obj4 = domainRepository.findCustomByDomain("google.com");
            //System.out.println(obj4);

            //List<Domain> obj5 = domainRepository.findCustomByRegExDomain("google");
            //obj5.forEach(x -> System.out.println(x));

        };

    }*/

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
                                       MongoMappingContext context) {

        MappingMongoConverter converter =
                new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

        return mongoTemplate;

    }

}
