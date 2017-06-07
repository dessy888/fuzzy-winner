package inc.deszo.fuzzywinner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.deszo.fuzzywinner.model.Domain;
import inc.deszo.fuzzywinner.model.Fund;
import inc.deszo.fuzzywinner.repository.DomainRepository;
import inc.deszo.fuzzywinner.repository.FundRepository;
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
import org.springframework.web.client.RestTemplate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private FundRepository fundRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        loadFunds();

        // all funds with yield more than 5%
        Sort sort = new Sort(Sort.Direction.ASC,"yield");
        List<Fund> funds = fundRepository.findFundsByYield(5.0, sort);
        funds.forEach((fund) -> logger.info("Funds {}: {}%", fund.getName(), fund.getYield()));

        // all plus funds with yield more than 4%
        List<Fund> plusFunds = fundRepository.findPlusFundsByYield(4.0, sort);
        plusFunds.forEach((fund) -> logger.info("Plus Funds {}: {}%", fund.getName(), fund.getYield()));
    }

    public void loadFunds() throws IOException {
        fundRepository.deleteAll();

        int numOfFunds = 0;
        int numOfFundCompany = 0;

        //Get list of companyIds
        Document doc = Jsoup.connect("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?companyid=218&lo=0&page=1&tab=prices").timeout(0).get();
        Element content = doc.getElementById("search-company");
        Elements optgroups = content.getElementsByTag("optgroup");
        Elements options = optgroups.last().getElementsByTag("option");

        for (Element option : options) {
            logger.info("companyIds: {}, {}", option.val(), option.text());

            String companyId = option.val();
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.postForObject("http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results?companyid=" + companyId + "&lo=0&page=1&SQ_DESIGN_NAME=json", null, String.class);
            logger.info(result);

            final JsonNode arrNode = new ObjectMapper().readTree(result).get("results");
            if (arrNode.isArray()) {
                for (final JsonNode objNode : arrNode) {
                    logger.info(objNode.toString());
                    Fund newFund = new ObjectMapper().treeToValue(objNode, Fund.class);

                    fundRepository.save(newFund);
                    numOfFunds++;
                }
            }
            numOfFundCompany++;
        }

        logger.info("Number of Fund Companies Loaded: {}, Number of Funds Loaded: {}", numOfFundCompany, numOfFunds);
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
