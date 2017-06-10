package inc.deszo.fuzzywinner.repository;

import java.util.Date;

public interface FundInfosRepositoryCustom {

    int updateInceptionDate(String sedol, String inceptionDate, String updated);

    int updateFtSymbol(String sedol, String ftSymbol, String updated);

    int updatePlusFund(String sedol, String plusFund, String updated);
}
