package inc.deszo.fuzzywinner.investmenttrust.model;

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

public class InvestmentTrustHistoryPrice extends HistoryPrice {

  private double price_open;

  private double price_high;

  private double price_low;

  private double price_close;

  private double volume;

  public InvestmentTrustHistoryPrice() {
    super();
  }

  public InvestmentTrustHistoryPrice(String sedol, String isin, String ftSymbol, double price_open,
                                     double price_high, double price_low, double price_close,
                                     double volume, String cobDate, Type type) throws ParseException {
    super(sedol, isin, ftSymbol, cobDate, type);
    this.price_open = price_open;
    this.price_high = price_high;
    this.price_low = price_low;
    this.price_close = price_close;
    this.volume = volume;
  }

  public double getPrice_open() {
    return price_open;
  }

  public void setPrice_open(double price_open) {
    this.price_open = price_open;
  }

  public double getPrice_high() {
    return price_high;
  }

  public void setPrice_high(double price_high) {
    this.price_high = price_high;
  }

  public double getPrice_low() {
    return price_low;
  }

  public void setPrice_low(double price_low) {
    this.price_low = price_low;
  }

  public double getPrice_close() {
    return price_close;
  }

  public void setPrice_close(double price_close) {
    this.price_close = price_close;
  }

  public double getVolume() {
    return volume;
  }

  public void setVolume(double volume) {
    this.volume = volume;
  }
}
