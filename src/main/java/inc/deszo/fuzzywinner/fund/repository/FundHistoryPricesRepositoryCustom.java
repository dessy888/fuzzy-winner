package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;

import java.text.ParseException;
import java.util.List;

public interface FundHistoryPricesRepositoryCustom {

  List<FundHistoryPrice> getDistinctSedol();

  List<FundHistoryPrice> getSedol(String sedol);

  List<FundHistoryPrice> getLastUpdated(String sedol, String isin, String ftSymbol);

  List<FundHistoryPrice> getOldestPrice(String sedol, String isin, String ftSymbol);

  List<FundHistoryPrice> getFundPriceByDate(String sedol, String isin, String ftSymbol, String cobDate) throws ParseException;
}
