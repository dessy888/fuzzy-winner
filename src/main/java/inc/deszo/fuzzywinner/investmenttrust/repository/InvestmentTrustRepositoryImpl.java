package inc.deszo.fuzzywinner.investmenttrust.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static inc.deszo.fuzzywinner.utils.CsvUtils.csvWriter;

public class InvestmentTrustRepositoryImpl implements InvestmentTrustRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(InvestmentTrustRepositoryImpl.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private InvestmentTrustRepository investmentTrustRepository;

  @Override
  public int updateInvestmentTrust(InvestmentTrust investmentTrust) {

    Query query = new Query(Criteria.where("key").is(investmentTrust.getKey()));
    Update update = new Update();
    update.set("identifier", investmentTrust.getIdentifier());
    update.set("title", investmentTrust.getTitle());
    update.set("description", investmentTrust.getDescription());
    update.set("company", investmentTrust.getCompany());
    update.set("isin", investmentTrust.getIsin());
    update.set("currency", investmentTrust.getCurrency());
    update.set("totalAssets", investmentTrust.getTotalAssets());
    update.set("grossGearing", investmentTrust.getGrossGearing());
    update.set("marketCap", investmentTrust.getMarketCap());
    update.set("sharesInIssue", investmentTrust.getSharesInIssue());
    update.set("priceBuy", investmentTrust.getPriceBuy());
    update.set("priceSell", investmentTrust.getPriceSell());
    update.set("priceChange", investmentTrust.getPriceChange());
    update.set("premiumDiscount", investmentTrust.getPremiumDiscount());
    update.set("dividendYield", investmentTrust.getDividendYield());
    update.set("dividendFrequency", investmentTrust.getDividendFrequency());
    update.set("estimatedNav", investmentTrust.getEstimatedNav());
    update.set("latestActualNav", investmentTrust.getLatestActualNav());
    update.set("latestActualNavDate", investmentTrust.getLatestActualNavDate());
    update.set("_12mAvgPremiumDiscount", investmentTrust.get_12mAvgPremiumDiscount());
    update.set("navFrequency", investmentTrust.getNavFrequency());
    update.set("performanceFee", investmentTrust.getPerformanceFee());
    update.set("ongoingCharge", investmentTrust.getOngoingCharge());
    update.set("launchDate", investmentTrust.getLaunchDate());
    update.set("navFrequency", investmentTrust.getNavFrequency());

    WriteResult result = mongoTemplate.updateFirst(query, update, InvestmentTrust.class);

    if (result != null) {
      return result.getN();
    } else {
      return 0;
    }
  }

  @Override
  public List<InvestmentTrust> getLastUpdated(String sedol) {

    Query query = new Query();
    query.limit(1);
    query.with(new Sort(Sort.Direction.DESC, "updated"));
    query.addCriteria(Criteria.where("sedol").is(sedol));

    return mongoTemplate.find(query, InvestmentTrust.class);
  }

  @Override
  public void genCsvInvestmentReport() throws IOException {

    ObjectMapper mapper = JsonUtils.getMAPPER();
    CsvMapper csvMapper = new CsvMapper();

    List<InvestmentTrust> investmentTrusts = investmentTrustRepository.findAll();
    List<LinkedHashMap<String, String>> myArrList = new ArrayList<>();

    for (InvestmentTrust investmentTrust:investmentTrusts) {

      String json = mapper.writeValueAsString(investmentTrust);
      JsonNode jsonNode = mapper.readTree(json);

      LinkedHashMap<String, String> map;
      map = mapper.readValue(jsonNode.toString(), new TypeReference<LinkedHashMap<String, String>>() {
        });
        myArrList.add(map);
    }

    String pathname = "C:/Users/deszo/IdeaProjects/fuzzy-winner/reports/csvInvestmentReport_" +
      DateUtils.getTodayDate("MM_dd_yyyy") + ".csv";
    File file = new File(pathname);

    // Create a File and append if it already exists.
    Writer writer = new FileWriter(file, false);

    //Copy List of Map Object into CSV format at specified File location.
    csvWriter(myArrList, writer);

    logger.info("CSV file generated: {}", pathname);
  }

}

