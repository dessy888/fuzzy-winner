package inc.deszo.fuzzywinner.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

//@Document(collection = "domain")
public class Domain {

    @Id
    private long id;

    @Indexed(unique = true)
    private String domain;

    private boolean displayAds;

    public Domain(long id, String domain, boolean displayAds) {
        this.id = id;
        this.domain = domain;
        this.displayAds = displayAds;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "id=" + id +
                ", domain='" + domain + '\'' +
                ", displayAds=" + displayAds +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isDisplayAds() {
        return displayAds;
    }

    public void setDisplayAds(boolean displayAds) {
        this.displayAds = displayAds;
    }
}