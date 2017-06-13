package inc.deszo.fuzzywinner.repository.fund;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

public interface FundPerformanceRepositoryCustom {

  void calculate(LocalDate cobDate, boolean plusFundOnly) throws ParseException;

  void genCsvFundReport() throws IOException;

  int updateKey();
}
