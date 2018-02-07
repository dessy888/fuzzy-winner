package inc.deszo.fuzzywinner.fund.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inc.deszo.fuzzywinner.shared.model.TopHolding;
import inc.deszo.fuzzywinner.shared.model.Type;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.ParseException;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "topholdings")
@CompoundIndexes(value =
    {
        @CompoundIndex(name = "Sedol_isin_ftsymbol_position_security_weight_updated_ind1",
            def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1, 'position': 1, 'security': 1" +
            ", 'weight': 1, 'updated': 1}", unique = true)
    }
)
public class FundTopHolding extends TopHolding {

  public FundTopHolding() {
    super();
  }

  public FundTopHolding(String sedol, String updated, Type type, String position,
                        String security, String weight, String url) throws ParseException {
    super(sedol, updated, Type.FUND, position, security, weight, url);
  }
}
