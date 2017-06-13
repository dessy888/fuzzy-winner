package inc.deszo.fuzzywinner.repository.fund;

import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.model.fund.Fund;
import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;
import inc.deszo.fuzzywinner.model.fund.FundInfos;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class FundRepositoryImpl implements FundRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(FundRepositoryImpl.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<String> getDistinctSedol() {

    return mongoTemplate.getCollection("fund").distinct("sedol");
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

    return mongoTemplate.getCollection("fund").distinct("updated");
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
}

