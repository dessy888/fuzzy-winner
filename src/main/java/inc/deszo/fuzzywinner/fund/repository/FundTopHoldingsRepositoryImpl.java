package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundTopHolding;
import inc.deszo.fuzzywinner.shared.model.Type;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.ParseException;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

//http://stackoverflow.com/questions/11880924/how-to-add-custom-method-to-spring-data-jpa
//http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behaviour
//Impl postfix of the name on it compared to the core repository interface
public class FundTopHoldingsRepositoryImpl implements FundTopHoldingsRepositoryCustom {

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
}
