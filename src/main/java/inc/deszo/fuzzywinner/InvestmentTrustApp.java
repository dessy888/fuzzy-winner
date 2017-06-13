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

