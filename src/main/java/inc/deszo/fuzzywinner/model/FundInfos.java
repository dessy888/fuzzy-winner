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
                @CompoundIndex(name = "Sedol_isin_ind1", def = "{'sedol': 1, 'isin': 1}", unique = true)
        }
)
public class FundInfos {

    @Indexed
    private String sedol;

    @Indexed
    private String isin;

    private String updated;

    public FundInfos () {
        super();
    }

    public FundInfos (String sedol, String isin, String updated) {
        this.sedol = sedol;
        this.isin = isin;
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

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
