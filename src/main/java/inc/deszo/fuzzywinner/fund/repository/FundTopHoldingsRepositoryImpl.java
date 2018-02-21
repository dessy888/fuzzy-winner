package inc.deszo.fuzzywinner.fund.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import inc.deszo.fuzzywinner.fund.model.FundTopHolding;
import inc.deszo.fuzzywinner.shared.model.Type;
import inc.deszo.fuzzywinner.utils.CsvUtils;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.JsonUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

//http://stackoverflow.com/questions/11880924/how-to-add-custom-method-to-spring-data-jpa
//http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behaviour
//Impl postfix of the name on it compared to the core repository interface
public class FundTopHoldingsRepositoryImpl implements FundTopHoldingsRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(FundTopHoldingsRepositoryImpl.class);

  @Value("${spring.data.mongodb.host}")
  private String host;

  @Value("${spring.data.mongodb.port}")
  private int port;

  @Value("${spring.data.mongodb.database}")
  private String database;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<FundTopHolding> getLastUpdated(String sedol) {

    Query query = new Query();
    query.limit(1);
    query.with(new Sort(Sort.Direction.DESC, "cobDate"));
    query.addCriteria(Criteria.where("sedol").is(sedol));
    query.addCriteria(Criteria.where("type").is(Type.FUND));

    return mongoTemplate.find(query, FundTopHolding.class);
  }

  @Override
  public List<FundTopHolding> getFundTopHoldingsByDate(String sedol, String updated) throws ParseException {

    Query query = new Query();
    query.limit(1);
    query.with(new Sort(Sort.Direction.DESC, "cobDate"));
    query.addCriteria(Criteria.where("sedol").is(sedol));
    query.addCriteria(Criteria.where("updated").lte(DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT)));
    query.addCriteria(Criteria.where("type").is(Type.FUND));

    return mongoTemplate.find(query, FundTopHolding.class);
  }

  @Override
  public void genCsvTopHoldingsReport(String cobDate) throws IOException {

    // top holdings report
    ObjectMapper mapper = JsonUtils.getMAPPER();

    MongoClient client = new MongoClient(new ServerAddress(host, port));
    MongoDatabase db = client.getDatabase(database);
    MongoCollection<Document> collection = db.getCollection("topholdings");

    Instant instant = Instant.parse(cobDate + "T00:00:00Z");
    Date timestamp = Date.from(instant);

    Document projectStage = Document.parse(
        "{ $dateToString: { format: '%d/%m/%Y', date: '$updated' } }");

    AggregateIterable<Document> result = collection
        .aggregate(Arrays.asList(
            Aggregates.match(Filters.eq("updated", timestamp)),
            Aggregates.match(Filters.eq("type", "FUND")),
            Aggregates.project(
                Projections.fields(
                    Projections.excludeId(),
                    Projections.include("sedol"),
                    Projections.include("position"),
                    Projections.include("security"),
                    Projections.include("weight"),
                    Projections.computed("security-url", "$url"),
                    Projections.computed("sedol-url",
                        "http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results/" + "$sedol"),
                    Projections.computed("cobDate", projectStage)
                ))
        )).allowDiskUse(true).batchSize(1000).useCursor(true);

    MongoCursor<Document> cursor = result.iterator();
    List<LinkedHashMap<String, String>> myArrList = new ArrayList<>();

    int recordCount = 0;
    while (cursor.hasNext()) {
      Document doc = cursor.next();

      final JsonNode objNode = mapper.readTree(JsonUtils.flattenDoc(doc).toJson());
      LinkedHashMap<String, String> map;
      map = mapper.readValue(objNode.toString(), new TypeReference<LinkedHashMap<String, String>>() {
      });
      myArrList.add(map);

      recordCount++;
    }

    String pathname = "C:/Users/deszo/IdeaProjects/fuzzy-winner/reports/csvTopHoldingsReport-" +
        DateUtils.getTodayDate("MM_dd_yyyy") + ".csv";
    File file = new File(pathname);

    // Create a File and append if it already exists.
    Writer writer = new FileWriter(file, false);

    //Copy List of Map Object into CSV format at specified File location.
    CsvUtils.csvWriter(myArrList, writer);

    logger.info("CSV file generated ({} records): {}", recordCount, pathname);
  }
}
