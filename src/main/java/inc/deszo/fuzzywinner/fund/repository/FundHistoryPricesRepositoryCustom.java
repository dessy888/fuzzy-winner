package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrices;

import java.text.ParseException;
import java.util.List;

public interface FundHistoryPricesRepositoryCustom {

  List<FundHistoryPrices> getDistinctSedol();

  List<FundHistoryPrices> getLastUpdated(String sedol, String isin, String ftSymbol);

  List<FundHistoryPrices> getOldestPrice(String sedol, String isin, String ftSymbol);

  List<FundHistoryPrices> getFundPriceByDate(String sedol, String isin, String ftSymbol, String cobDate) throws ParseException;
}
