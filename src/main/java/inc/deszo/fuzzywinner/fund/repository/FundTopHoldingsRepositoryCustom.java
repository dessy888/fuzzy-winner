package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.fund.model.FundTopHolding;

import java.text.ParseException;
import java.util.List;

public interface FundTopHoldingsRepositoryCustom {

  List<FundTopHolding> getLastUpdated(String sedol);

  List<FundTopHolding> getFundTopHoldingsByDate(String sedol, String updated) throws ParseException;
}
