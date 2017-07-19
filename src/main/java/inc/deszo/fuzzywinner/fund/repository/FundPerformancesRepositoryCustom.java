package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundPerformance;

import java.text.ParseException;
import java.time.LocalDate;

public interface FundPerformancesRepositoryCustom {

  void calculate(LocalDate cobDate, boolean plusFundOnly, boolean overrideFundPerformance) throws ParseException;

  int updateKey();

  int update(FundPerformance fundPerformance);
}
