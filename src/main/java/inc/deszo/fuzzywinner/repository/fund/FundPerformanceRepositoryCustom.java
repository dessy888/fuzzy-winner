package inc.deszo.fuzzywinner.repository.fund;

import java.text.ParseException;

public interface FundPerformanceRepositoryCustom {

    void calculate(boolean plusFundOnly) throws ParseException;
}
