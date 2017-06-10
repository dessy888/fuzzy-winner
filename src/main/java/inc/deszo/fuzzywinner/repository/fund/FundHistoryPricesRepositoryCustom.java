package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;

import java.util.List;

public interface FundHistoryPricesRepositoryCustom {

    List<FundHistoryPrices> lastUpdated(String sedol, String isin, String ftSymbol);
}
