package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;

import java.text.ParseException;
import java.util.List;

public interface FundHistoryPricesRepositoryCustom {

    List<FundHistoryPrices> getDistinctSedol();

    List<FundHistoryPrices> getLastUpdated(String sedol, String isin, String ftSymbol);

    List<FundHistoryPrices> getOldestPrice(String sedol, String isin, String ftSymbol);

    List<FundHistoryPrices> getFundPriceByDate(String sedol, String isin, String ftSymbol, String cobDate) throws ParseException;
}
