package inc.deszo.fuzzywinner.fund.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import inc.deszo.fuzzywinner.fund.model.Fund;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;
import inc.deszo.fuzzywinner.utils.CsvUtils;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.JsonUtils;
import javafx.util.Pair;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class FundRepositoryImpl implements FundRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(FundRepositoryImpl.class);

  @Value("${spring.data.mongodb.host}")
  private String host;

  @Value("${spring.data.mongodb.port}")
  private int port;

  @Value("${spring.data.mongodb.database}")
  private String database;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<String> getDistinctSedol() {

    return (List<String>) mongoTemplate.getCollection("fund").distinct("sedol");
  }

  @Override
  public AggregationResults<Fund> getFundWithYieldMoreThan(double yield) {
    Aggregation aggFund = newAggregation(
        match(Criteria.where("yield").gt(yield)), sort(Sort.Direction.DESC, "yield").and(Sort.Direction.ASC, "sedol")
    );

    return mongoTemplate.aggregate(aggFund, Fund.class, Fund.class);
  }

  @Override
  public AggregationResults<Fund> getPlusFundWithYieldMoreThan(double yield) {
    Aggregation aggPlusFund = newAggregation(
        match(Criteria.where("yield").gt(yield)), sort(Sort.Direction.DESC, "yield").and(Sort.Direction.ASC, "sedol")
    );

    return mongoTemplate.aggregate(aggPlusFund, Fund.class, Fund.class);
  }

  @Override
  public List<Date> getDistinctUpdated() {

    return (List<Date>) mongoTemplate.getCollection("fund").distinct("updated");
  }

  @Override
  public List<Fund> getLastUpdated(String sedol) {

    Query query = new Query();
    query.limit(1);
    query.with(new Sort(Sort.Direction.DESC, "updated"));
    query.addCriteria(Criteria.where("sedol").is(sedol));

    return mongoTemplate.find(query, Fund.class);
  }

  @Override
  public void genCsvFundReport() throws IOException {

    // performance report
    ObjectMapper mapper = JsonUtils.getMAPPER();
    CsvMapper csvMapper = new CsvMapper();

    MongoClient client = new MongoClient(new ServerAddress(host, port));
    MongoDatabase db = client.getDatabase(database);
    MongoCollection<Document> collection = db.getCollection("fund");

    AggregateIterable<Document> result = collection
        .aggregate(Arrays.asList(
            Aggregates.sort(orderBy(ascending("key"))),
            Aggregates.lookup("fundperformance", "key", "key", "perf"),
            Aggregates.unwind("$perf"),
            Aggregates.project(
                Projections.fields(
                    Projections.excludeId(),
                    Projections.include("sedol"),
                    Projections.computed("isin","$perf.isin"),
                    Projections.computed("ftSymbol","$perf.ftSymbol"),
                    Projections.include("name"),
                    Projections.include("url"),
                    Projections.include("unitType"),
                    Projections.include("loaded"),
                    Projections.include("company"),
                    Projections.include("sector"),
                    Projections.include("plusFund"),
                    Projections.include("price_sell"),
                    Projections.include("price_buy"),
                    Projections.include("price_change"),
                    Projections.include("yield"),
                    Projections.include("initialCharge"),
                    Projections.include("annualCharge"),
                    Projections.include("annualSaving"),
                    Projections.include("netAnnualCharge"),
                    Projections.include("fundSize"),
                    Projections.include("incomeFrequency"),
                    Projections.include("paymentType"),
                    Projections.include("numHoldings"),
                    Projections.computed("reportName","$perf.reportName"),
                    Projections.computed("1D","$perf._1D"),
                    Projections.computed("3D","$perf._3D"),
                    Projections.computed("5D","$perf._5D"),
                    Projections.computed("1W","$perf._1W"),
                    Projections.computed("2W","$perf._2W"),
                    Projections.computed("3W","$perf._3W"),
                    Projections.computed("1M","$perf._1M"),
                    Projections.computed("2M","$perf._2M"),
                    Projections.computed("3M","$perf._3M"),
                    Projections.computed("4M","$perf._4M"),
                    Projections.computed("5M","$perf._5M"),
                    Projections.computed("6M","$perf._6M"),
                    Projections.computed("7M","$perf._7M"),
                    Projections.computed("8M","$perf._8M"),
                    Projections.computed("9M","$perf._9M"),
                    Projections.computed("10M","$perf._10M"),
                    Projections.computed("11M","$perf._11M"),
                    Projections.computed("1Y","$perf._1Y"),
                    Projections.computed("2Y","$perf._2Y"),
                    Projections.computed("3Y","$perf._3Y"),
                    Projections.computed("4Y","$perf._4Y"),
                    Projections.computed("5Y","$perf._5Y"),
                    Projections.computed("6Y","$perf._6Y"),
                    Projections.computed("7Y","$perf._7Y"),
                    Projections.computed("8Y","$perf._8Y"),
                    Projections.computed("9Y","$perf._9Y"),
                    Projections.computed("10Y","$perf._10Y"),
                    Projections.computed("11Y","$perf._11Y"),
                    Projections.computed("12Y","$perf._12Y"),
                    Projections.computed("13Y","$perf._13Y"),
                    Projections.computed("14Y","$perf._14Y"),
                    Projections.computed("15Y","$perf._15Y"),
                    Projections.computed("16Y","$perf._16Y"),
                    Projections.computed("17Y","$perf._17Y"),
                    Projections.computed("18Y","$perf._18Y"),
                    Projections.computed("19Y","$perf._19Y"),
                    Projections.computed("20Y","$perf._20Y"),
                    Projections.computed("ALL","$perf._ALL"),
                    Projections.include("perf12m"),
                    Projections.include("perf12t24m"),
                    Projections.include("perf24t36m"),
                    Projections.include("perf36t48m"),
                    Projections.include("perf48t60m"),
                    Projections.include("key")
            ))
        )).allowDiskUse(true).batchSize(1000).useCursor(true);

    MongoCursor<Document> cursor = result.iterator();
    List<LinkedHashMap<String, String>> myArrList = new ArrayList<>();

    int recordCount = 0;
    while (cursor.hasNext()) {
      Document doc = cursor.next();

      final JsonNode objNode = mapper.readTree(flattenDoc(doc).toJson());
      LinkedHashMap<String, String> map;
      map = mapper.readValue(objNode.toString(), new TypeReference<LinkedHashMap<String, String>>() {
      });
      myArrList.add(map);

      recordCount++;
    }

    String pathname = "C:/Users/deszo/IdeaProjects/fuzzy-winner/reports/csvFundReport-" +
        DateUtils.getTodayDate("MM_dd_yyyy") + ".csv";
    File file = new File(pathname);

    // Create a File and append if it already exists.
    Writer writer = new FileWriter(file, false);

    //Copy List of Map Object into CSV format at specified File location.
    CsvUtils.csvWriter(myArrList, writer);

    logger.info("CSV file generated ({} records): {}", recordCount, pathname);
  }

  public static Document flattenDoc( Document document ){

    Document flattened = new Document();
    Queue<Pair<String, Document>> queue = new ArrayDeque<>();
    queue.add( new Pair<>( "", document ) );

    while( !queue.isEmpty() ){
      Pair<String, Document> pair = queue.poll();
      String key = pair.getKey();
      for( Map.Entry<String, Object> entry : pair.getValue().entrySet() ){
        if( entry.getValue() instanceof Document ){
          queue.add( new Pair<>( key + entry.getKey() + ".", ( Document ) entry.getValue() ) );

        }else{
          flattened.put( key + entry.getKey(), entry.getValue() );

        }
      }//end for
    }

    return flattened;
  }

  @Override
  public int updateKey() {

    int numOfRecordsUpdated = 0;

    Query query = new Query();
    query.with(new Sort(Sort.Direction.DESC, "sedol"));
    //query.addCriteria(Criteria.where("key").is(null));

    List<Fund> funds = mongoTemplate.find(query, Fund.class);

    for (Fund fund : funds) {
      fund.setKey();

      Query fundQuery = new Query();
      fundQuery.addCriteria(Criteria.where("sedol").is(fund.getSedol()));
      fundQuery.addCriteria(Criteria.where("updated").is(fund.getUpdated()));

      Update update = new Update();
      update.set("key", fund.getKey());

      WriteResult result = mongoTemplate.updateFirst(fundQuery, update, Fund.class);

      if (result != null) {
        numOfRecordsUpdated += result.getN();
      } else {
        logger.error("Sedol {} key not updated.", fund.getSedol());
      }
    }

    return numOfRecordsUpdated;
  }

  @Override
  public int updateUrl() {

    int numOfRecordsUpdated = 0;

    Query query = new Query();
    query.with(new Sort(Sort.Direction.DESC, "sedol"));
    //query.addCriteria(Criteria.where("key").is(null));

    List<Fund> funds = mongoTemplate.find(query, Fund.class);

    for (Fund fund : funds) {
      fund.setUrl();

      Query fundQuery = new Query();
      fundQuery.addCriteria(Criteria.where("sedol").is(fund.getSedol()));
      fundQuery.addCriteria(Criteria.where("updated").is(fund.getUpdated()));

      Update update = new Update();
      update.set("url", fund.getUrl());

      WriteResult result = mongoTemplate.updateFirst(fundQuery, update, Fund.class);

      if (result != null) {
        numOfRecordsUpdated += result.getN();
      } else {
        logger.error("Sedol {} url not updated.", fund.getSedol());
      }
    }

    return numOfRecordsUpdated;
  }

  @Override
  public int updateFund(Fund fund) {

    Query query = new Query(Criteria.where("key").is(fund.getKey()));
    Update update = new Update();
    update.set("annualCharge", fund.getAnnualCharge());
    update.set("annualSaving", fund.getAnnualSaving());
    update.set("company", fund.getCompany());
    update.set("discountedCode", fund.getDiscountedCode());
    update.set("fundSize", fund.getFundSize());
    update.set("incomeFrequency", fund.getIncomeFrequency());
    update.set("initialCharge", fund.getInitialCharge());
    update.set("loaded", fund.getLoaded());
    update.set("name", fund.getName());
    update.set("netAnnualCharge", fund.getNetAnnualCharge());
    update.set("numHoldings", fund.getNumHoldings());
    update.set("paymentType", fund.getPaymentType());
    update.set("perf12m", fund.getPerf12m());
    update.set("perf12t24m", fund.getPerf12t24m());
    update.set("perf24t36m", fund.getPerf24t36m());
    update.set("perf36t48m", fund.getPerf36t48m());
    update.set("perf48t60m", fund.getPerf48t60m());
    update.set("plusFund", fund.getPlusFund());
    update.set("price_buy", fund.getPriceBuy());
    update.set("price_change", fund.getPriceChange());
    update.set("price_sell", fund.getPriceSell());
    update.set("sector", fund.getSector());
    update.set("unitType", fund.getUnitType());
    update.set("yield", fund.getYield());

    WriteResult result = mongoTemplate.updateFirst(query, update, Fund.class);

    if (result != null) {
      return result.getN();
    } else {
      return 0;
    }
  }
}

