package inc.deszo.fuzzywinner.repository.fund;

import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.model.fund.FundInfos;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.text.ParseException;

//http://stackoverflow.com/questions/11880924/how-to-add-custom-method-to-spring-data-jpa
//http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behaviour
//Impl postfix of the name on it compared to the core repository interface
public class FundInfosRepositoryImpl implements FundInfosRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public int updateInceptionDate(String sedol, String inceptionDate, String updated) throws ParseException {

        Query query = new Query(Criteria.where("sedol").is(sedol));
        Update update = new Update();
        update.set("inceptionDate", DateUtils.getDate(inceptionDate, DateUtils.STANDARD_FORMAT));
        update.set("updated", DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT));

        WriteResult result = mongoTemplate.updateFirst(query, update, FundInfos.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
    }

    @Override
    public int updateFtSymbol(String sedol, String ftSymbol, String updated) throws ParseException {

        Query query = new Query(Criteria.where("sedol").is(sedol));
        Update update = new Update();
        update.set("ftSymbol", ftSymbol);
        update.set("updated", DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT));

        WriteResult result = mongoTemplate.updateFirst(query, update, FundInfos.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
    }

    @Override
    public int updatePlusFund(String sedol, String plusFund, String updated) throws ParseException {

        Query query = new Query(Criteria.where("sedol").is(sedol));
        Update update = new Update();
        update.set("plusFund", plusFund);
        update.set("updated", DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT));

        WriteResult result = mongoTemplate.updateFirst(query, update, FundInfos.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
    }
}
