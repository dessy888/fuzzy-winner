package inc.deszo.fuzzywinner.repository.fund;

import java.io.IOException;
import java.text.ParseException;

public interface FundPerformanceRepositoryCustom {

    void calculate(boolean plusFundOnly) throws ParseException;

    void genCSVReport () throws IOException;
}
