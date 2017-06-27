package inc.deszo.fuzzywinner.fund.repository;

import java.text.ParseException;
import java.time.LocalDate;

public interface FundPerformancesRepositoryCustom {

  void calculate(LocalDate cobDate, boolean plusFundOnly) throws ParseException;

  int updateKey();
}
