package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.fund.model.FundPerformance;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

public interface FundPerformancesRepositoryCustom {

  void calculate(LocalDate cobDate, boolean plusFundOnly, boolean overrideFundPerformance) throws ParseException;

  int calculateFund(FundHistoryPrice fund, int fundCount, LocalDate cobDate, boolean plusFundOnly,
                           boolean overrideFundPerformance) throws ParseException;

  int updateKey();

  int update(FundPerformance fundPerformance);

  AggregationResults<FundPerformance> getSedolByTenorPerformance(String tenor, double performance);
}
