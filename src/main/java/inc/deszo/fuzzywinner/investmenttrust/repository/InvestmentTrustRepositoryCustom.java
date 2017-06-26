package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;

import java.io.IOException;
import java.util.List;

public interface InvestmentTrustRepositoryCustom {

  int updateInvestmentTrust(InvestmentTrust investmentTrust);

  List<InvestmentTrust> getLastUpdated(String sedol);

  void genCsvInvestmentReport() throws IOException;
}
