package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrustMapping;

import java.text.ParseException;

public interface InvestmentTrustMappingsRepositoryCustom {

  int update(InvestmentTrustMapping investmentTrustMapping) throws ParseException;

  int updateInceptionDate(String sedol, String inceptionDate, String updated) throws ParseException;

  int updateFtSymbol(String sedol, String ftSymbol, String updated) throws ParseException;
}
