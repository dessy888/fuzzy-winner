package inc.deszo.fuzzywinner.repository;

import inc.deszo.fuzzywinner.model.FundHistoryPrices;

import java.util.List;

public interface FundHistoryPricesRepositoryCustom {

    List<FundHistoryPrices> lastUpdated(String sedol, String isin, String ftSymbol);
}
