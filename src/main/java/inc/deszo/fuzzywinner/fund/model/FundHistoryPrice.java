package inc.deszo.fuzzywinner.fund.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inc.deszo.fuzzywinner.shared.model.HistoryPrice;
import inc.deszo.fuzzywinner.shared.model.Type;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.ParseException;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "historyprices")
@CompoundIndexes(value =
    {
        @CompoundIndex(name = "Sedol_isin_ftsymbol_cobdate_ind1", def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1" +
            ", 'cobDate': 1}", unique = true),
        @CompoundIndex(name = "Sedol_isin_ftsymbol_ind1", def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1" +
            "}")
    }
)
public class FundHistoryPrice extends HistoryPrice {

  private double price_close;

  public FundHistoryPrice() {
    super();
  }

  public FundHistoryPrice(String sedol, String isin, String ftSymbol, double price_close,
                          String cobDate, Type type) throws ParseException {
    super(sedol, isin, ftSymbol, cobDate, type);
    this.price_close = price_close;
  }

  public double getPrice_close() {
    return price_close;
  }

  public void setPrice_close(double price_close) {
    this.price_close = price_close;
  }
}
