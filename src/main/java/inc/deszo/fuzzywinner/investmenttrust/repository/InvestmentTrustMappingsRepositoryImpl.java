package inc.deszo.fuzzywinner.investmenttrust.repository;

import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.fund.model.FundMapping;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrustMapping;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.text.ParseException;

public class InvestmentTrustMappingsRepositoryImpl implements InvestmentTrustMappingsRepositoryCustom{

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public int update(InvestmentTrustMapping investmentTrustMapping) throws ParseException {

    Query query = new Query(Criteria.where("sedol").is(investmentTrustMapping.getSedol()));
    Update update = new Update();
    update.set("isin", investmentTrustMapping.getIsin());
    update.set("updated", DateUtils.getDate(investmentTrustMapping.getUpdatedLocalDate(), DateUtils.STANDARD_FORMAT));
    update.set("inceptionDate", DateUtils.getDate(investmentTrustMapping.getInceptionLocalDate(), DateUtils.STANDARD_FORMAT));

    WriteResult result = mongoTemplate.updateFirst(query, update, InvestmentTrustMapping.class);

    if (result != null) {
      return result.getN();
    } else {
      return 0;
    }
  }

  @Override
  public int updateInceptionDate(String sedol, String inceptionDate, String updated) throws ParseException {

    Query query = new Query(Criteria.where("sedol").is(sedol));
    Update update = new Update();
    update.set("inceptionDate", DateUtils.getDate(inceptionDate, DateUtils.STANDARD_FORMAT));
    update.set("updated", DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT));

    WriteResult result = mongoTemplate.updateFirst(query, update, InvestmentTrustMapping.class);

    if (result != null) {
      return result.getN();
    } else {
      return 0;
    }
  }

  @Override
  public int updateFtSymbol(String sedol, String ftSymbol, String updated) throws ParseException {

    Query query = new Query(Criteria.where("sedol").is(sedol));
    Update update = new Update();
    update.set("ftSymbol", ftSymbol);
    update.set("updated", DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT));

    WriteResult result = mongoTemplate.updateFirst(query, update, InvestmentTrustMapping.class);

    if (result != null) {
      return result.getN();
    } else {
      return 0;
    }
  }
}
