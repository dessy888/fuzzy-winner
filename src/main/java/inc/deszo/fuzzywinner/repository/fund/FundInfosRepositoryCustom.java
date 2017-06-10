package inc.deszo.fuzzywinner.repository.fund;

import java.text.ParseException;

public interface FundInfosRepositoryCustom {

    int updateInceptionDate(String sedol, String inceptionDate, String updated) throws ParseException;

    int updateFtSymbol(String sedol, String ftSymbol, String updated) throws ParseException;

    int updatePlusFund(String sedol, String plusFund, String updated) throws ParseException;
}
