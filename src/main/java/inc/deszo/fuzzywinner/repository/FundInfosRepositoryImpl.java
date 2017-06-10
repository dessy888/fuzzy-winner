package inc.deszo.fuzzywinner.repository;

import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.model.Domain;
import inc.deszo.fuzzywinner.model.FundInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

//http://stackoverflow.com/questions/11880924/how-to-add-custom-method-to-spring-data-jpa
//http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behaviour
//Impl postfix of the name on it compared to the core repository interface
public class FundInfosRepositoryImpl implements FundInfosRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public int updateInceptionDate(String sedol, String inceptionDate, String updated) {

        Query query = new Query(Criteria.where("sedol").is(sedol));
        Update update = new Update();
        update.set("inceptionDate", inceptionDate);
        update.set("updated", updated);

        WriteResult result = mongoTemplate.updateFirst(query, update, FundInfos.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
    }

    @Override
    public int updateFtSymbol(String sedol, String ftSymbol, String updated) {

        Query query = new Query(Criteria.where("sedol").is(sedol));
        Update update = new Update();
        update.set("ftSymbol", ftSymbol);
        update.set("updated", updated);

        WriteResult result = mongoTemplate.updateFirst(query, update, FundInfos.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
    }

    @Override
    public int updatePlusFund(String sedol, String plusFund, String updated) {

        Query query = new Query(Criteria.where("sedol").is(sedol));
        Update update = new Update();
        update.set("plusFund", plusFund);
        update.set("updated", updated);

        WriteResult result = mongoTemplate.updateFirst(query, update, FundInfos.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
    }
}
