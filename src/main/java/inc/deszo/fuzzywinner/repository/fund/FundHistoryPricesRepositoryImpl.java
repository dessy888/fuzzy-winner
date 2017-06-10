package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

//http://stackoverflow.com/questions/11880924/how-to-add-custom-method-to-spring-data-jpa
//http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behaviour
//Impl postfix of the name on it compared to the core repository interface
public class FundHistoryPricesRepositoryImpl implements FundHistoryPricesRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<FundHistoryPrices> lastUpdated(String sedol, String isin, String ftSymbol) {

        Query query = new Query();
        query.limit(1);
        query.with(new Sort(Sort.Direction.DESC, "cobDate"));
        query.addCriteria(Criteria.where("sedol").is(sedol));
        query.addCriteria(Criteria.where("isin").is(isin));
        query.addCriteria(Criteria.where("ftSymbol").is(ftSymbol));

        return mongoTemplate.find(query, FundHistoryPrices.class);
    }
}
