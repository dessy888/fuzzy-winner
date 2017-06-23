package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.fund.model.Fund;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface InvestmentTrustRepositoryCustom {

  int updateInvestmentTrust(InvestmentTrust investmentTrust);

  List<InvestmentTrust> getLastUpdated(String sedol);

  void genCsvInvestmentReport() throws IOException;
}
