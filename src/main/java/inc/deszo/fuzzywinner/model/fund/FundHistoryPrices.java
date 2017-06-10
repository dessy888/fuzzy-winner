package inc.deszo.fuzzywinner.model.fund;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "fundhistoryprices")
@CompoundIndexes(value =
        {
                @CompoundIndex(name = "Sedol_isin_ftsymbol_cobdate_ind1", def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1" +
                        ", 'cobDate': 1}", unique = true)
        }
)
public class FundHistoryPrices {

    @Indexed
    private String sedol;

    @Indexed
    private String isin;

    @Indexed
    private String ftSymbol;

    private double price_close;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date cobDate;

    public FundHistoryPrices() {
        super();
    }

    public FundHistoryPrices(String sedol, String isin, String ftSymbol, double price_close, String cobDate) throws ParseException {
        this.sedol = sedol;
        this.isin = isin;
        this.ftSymbol = ftSymbol;
        this.price_close = price_close;
        this.setCobDate(cobDate);
    }

    public String getSedol() {
        return sedol;
    }

    public void setSedol(String sedol) {
        this.sedol = sedol;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getFtSymbol() {
        return ftSymbol;
    }

    public void setFtSymbol(String ftSymbol) {
        this.ftSymbol = ftSymbol;
    }

    public double getPrice_close() {
        return price_close;
    }

    public void setPrice_close(double price_close) {
        this.price_close = price_close;
    }

    public String getCobLocalDateString() {
        return DateUtils.getDate(cobDate, DateUtils.STANDARD_FORMAT);
    }

    public Date getCobDate() {
        return cobDate;
    }

    public void setCobDate(String cobDate) throws ParseException {
        this.cobDate = DateUtils.getDate(cobDate, DateUtils.STANDARD_FORMAT);
    }
}
