package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrustHistoryPrice;

import java.text.ParseException;
import java.util.List;

public interface InvestmentTrustHistoryPricesRepositoryCustom {

  List<InvestmentTrustHistoryPrice> getDistinctSedol();

  List<InvestmentTrustHistoryPrice> getLastUpdated(String sedol, String isin, String ftSymbol);

  List<InvestmentTrustHistoryPrice> getOldestPrice(String sedol, String isin, String ftSymbol);

  List<InvestmentTrustHistoryPrice> getFundPriceByDate(String sedol, String isin, String ftSymbol, String cobDate) throws ParseException;
}
