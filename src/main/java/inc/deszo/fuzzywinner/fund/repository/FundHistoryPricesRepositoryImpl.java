package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.shared.model.Type;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.ParseException;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

//http://stackoverflow.com/questions/11880924/how-to-add-custom-method-to-spring-data-jpa
//http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behaviour
//Impl postfix of the name on it compared to the core repository interface
public class FundHistoryPricesRepositoryImpl implements FundHistoryPricesRepositoryCustom {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<FundHistoryPrice> getDistinctSedol() {

    Aggregation agg = newAggregation(
        match(Criteria.where("type").is(Type.FUND)),
        group("sedol", "isin", "ftSymbol", "type").count().as("total"),
        project("sedol", "isin", "ftSymbol", "type").and("total").previousOperation(),
        sort(Sort.Direction.DESC, "sedol")
    );

    //Convert the aggregation result into a List
    AggregationResults<FundHistoryPrice> groupResults = mongoTemplate.aggregate(agg, FundHistoryPrice.class, FundHistoryPrice.class);

    return groupResults.getMappedResults();

    //return mongoTemplate.getCollection("historyprices").aggregate().distinct("sedol");
  }

  @Override
  public List<FundHistoryPrice> getSedol(String sedol) {

    Aggregation agg = newAggregation(
        match(Criteria.where("type").is(Type.FUND).andOperator(Criteria.where("sedol").is(sedol))),
        group("sedol", "isin", "ftSymbol", "type").count().as("total"),
        project("sedol", "isin", "ftSymbol", "type").and("total").previousOperation(),
        sort(Sort.Direction.DESC, "sedol")
    );

    //Convert the aggregation result into a List
    AggregationResults<FundHistoryPrice> groupResults = mongoTemplate.aggregate(agg, FundHistoryPrice.class, FundHistoryPrice.class);

    return groupResults.getMappedResults();

    //return mongoTemplate.getCollection("historyprices").aggregate().distinct("sedol");
  }

  @Override
  public List<FundHistoryPrice> getLastUpdated(String sedol, String isin, String ftSymbol) {

    Query query = new Query();
    query.limit(1);
    query.with(new Sort(Sort.Direction.DESC, "cobDate"));
    query.addCriteria(Criteria.where("sedol").is(sedol));
    query.addCriteria(Criteria.where("isin").is(isin));
    query.addCriteria(Criteria.where("ftSymbol").is(ftSymbol));
    query.addCriteria(Criteria.where("type").is(Type.FUND));

    return mongoTemplate.find(query, FundHistoryPrice.class);
  }

  @Override
  public List<FundHistoryPrice> getOldestPrice(String sedol, String isin, String ftSymbol) {
    Query query = new Query();
    query.limit(1);
    query.with(new Sort(Sort.Direction.ASC, "cobDate"));
    query.addCriteria(Criteria.where("sedol").is(sedol));
    query.addCriteria(Criteria.where("isin").is(isin));
    query.addCriteria(Criteria.where("ftSymbol").is(ftSymbol));
    query.addCriteria(Criteria.where("type").is(Type.FUND));

    return mongoTemplate.find(query, FundHistoryPrice.class);
  }

  @Override
  public List<FundHistoryPrice> getFundPriceByDate(String sedol, String isin, String ftSymbol, String cobDate) throws ParseException {

    Query query = new Query();
    query.limit(1);
    query.with(new Sort(Sort.Direction.DESC, "cobDate"));
    query.addCriteria(Criteria.where("sedol").is(sedol));
    query.addCriteria(Criteria.where("isin").is(isin));
    query.addCriteria(Criteria.where("ftSymbol").is(ftSymbol));
    query.addCriteria(Criteria.where("cobDate").lte(DateUtils.getDate(cobDate, DateUtils.STANDARD_FORMAT)));
    query.addCriteria(Criteria.where("type").is(Type.FUND));

    return mongoTemplate.find(query, FundHistoryPrice.class);
  }
}
