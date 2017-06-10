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
@Document(collection = "fundinfos")
@CompoundIndexes(value =
        {
                @CompoundIndex(name = "Sedol_isin_ind1", def = "{'sedol': 1, 'isin': 1}", unique = true),
                @CompoundIndex(name = "Sedol_isin_ftsymbol_inceptionDate_ind2", def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1, 'inceptionDate': 1}", unique = true)
        }
)
public class FundInfos {

    @Indexed
    private String sedol;

    @Indexed
    private String isin;

    @Indexed
    private String ftSymbol;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date inceptionDate;

    private String plusFund;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date updated;

    public FundInfos () {
        super();
    }

    public FundInfos(String sedol, String isin, String updated) throws ParseException {
        this.sedol = sedol;
        this.isin = isin;
        this.setUpdated(updated);
    }

    public FundInfos(String sedol, String isin, String ftSymbol, String inceptionDate, String plusFund, String updated) throws ParseException {
        this.sedol = sedol;
        this.isin = isin;
        this.ftSymbol = ftSymbol;
        this.setInceptionDate(inceptionDate);
        this.plusFund = plusFund;
        this.setUpdated(updated);
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

    public String getInceptionLocalDate() {
        return DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT);
    }

    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(String inceptionDate) throws ParseException {
        this.inceptionDate = DateUtils.getDate(inceptionDate, DateUtils.STANDARD_FORMAT);
    }

    public String getPlusFund() {
        return plusFund;
    }

    public void setPlusFund(String plusFund) {
        this.plusFund = plusFund;
    }

    public String getUpdatedLocalDate() {
        return DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT);
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) throws ParseException {
        this.updated = DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT);
    }
}
