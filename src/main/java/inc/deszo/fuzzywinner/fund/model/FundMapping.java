package inc.deszo.fuzzywinner.fund.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inc.deszo.fuzzywinner.shared.model.Mapping;
import inc.deszo.fuzzywinner.shared.model.Type;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "mappings")
@CompoundIndexes(value =
    {
        @CompoundIndex(name = "Sedol_isin_ind1", def = "{'sedol': 1, 'isin': 1}", unique = true),
        @CompoundIndex(name = "Sedol_isin_ftsymbol_inceptionDate_ind2", def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1, 'inceptionDate': 1}", unique = true)
    }
)
public class FundMapping extends Mapping {

  private String plusFund;

  public FundMapping() {
    super();
  }

  public FundMapping(String sedol, String isin, String updated) throws ParseException {
    super(sedol, isin, updated, Type.FUND);
  }

  public FundMapping(String sedol, String isin, String ftSymbol, String inceptionDate,
                     String plusFund, String updated) throws ParseException {
    super(sedol, isin, ftSymbol, inceptionDate, updated, Type.FUND);
    this.plusFund = plusFund;
  }

  public String getPlusFund() {
    return plusFund;
  }

  public void setPlusFund(String plusFund) {
    this.plusFund = plusFund;
  }

}
