package inc.deszo.fuzzywinner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private String inceptionDate;

    private String plusFund;

    private String updated;

    public FundInfos () {
        super();
    }

    public FundInfos (String sedol, String isin, String updated) {
        this.sedol = sedol;
        this.isin = isin;
        this.updated = updated;
    }

    public FundInfos (String sedol, String isin, String ftSymbol, String inceptionDate, String plusFund, String updated) {
        this.sedol = sedol;
        this.isin = isin;
        this.ftSymbol = ftSymbol;
        this.inceptionDate = inceptionDate;
        this.plusFund = plusFund;
        this.updated = updated;
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

    public String getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(String inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public String getPlusFund() {
        return plusFund;
    }

    public void setPlusFund(String plusFund) {
        this.plusFund = plusFund;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
