package inc.deszo.fuzzywinner.investmenttrust.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inc.deszo.fuzzywinner.shared.model.Mapping;
import inc.deszo.fuzzywinner.shared.model.Type;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.ParseException;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "mappings")
@CompoundIndexes(value =
    {
        @CompoundIndex(name = "Sedol_isin_ind1", def = "{'sedol': 1, 'isin': 1}", unique = true),
        @CompoundIndex(name = "Sedol_isin_ftsymbol_inceptionDate_ind2", def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1, 'inceptionDate': 1}", unique = true)
    }
)
public class InvestmentTrustMapping extends Mapping {

  public InvestmentTrustMapping() {
    super();
  }

  public InvestmentTrustMapping(String sedol, String isin, String inceptionDate, String updated) throws ParseException {
    super(sedol, isin, updated, Type.INVESTMENTTRUST);
    this.setInceptionDate(inceptionDate);
  }

  public InvestmentTrustMapping(String sedol, String isin, String ftSymbol, String inceptionDate,
                     String updated) throws ParseException {
    super(sedol, isin, ftSymbol, inceptionDate, updated, Type.INVESTMENTTRUST);
  }
}
