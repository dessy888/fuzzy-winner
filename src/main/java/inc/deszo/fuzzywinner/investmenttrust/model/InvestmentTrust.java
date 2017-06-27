package inc.deszo.fuzzywinner.investmenttrust.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

import static inc.deszo.fuzzywinner.utils.DateUtils.getDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "investmenttrusts")
@CompoundIndexes(value =
    {
        @CompoundIndex(name = "Sedol_updated_ind1", def = "{'sedol': 1, 'updated': 1}", unique = true)
    }
)
public class InvestmentTrust {

  private String identifier;

  private String title;

  private String description;

  private String company;

  private String sedol;

  private String isin;

  private String currency;

  private double totalAssets;

  private double grossGearing;

  private String marketCap;

  private String sharesInIssue;

  private double priceBuy;

  private double priceSell;

  private double priceChange;

  private double premiumDiscount;

  private double dividendYield;

  private String dividendFrequency;

  private double estimatedNav;

  private double latestActualNav;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @JsonFormat
      (shape = JsonFormat.Shape.STRING, pattern = DateUtils.STANDARD_FORMAT)
  private Date latestActualNavDate;

  private double _12mAvgPremiumDiscount;

  private String navFrequency;

  private String performanceFee;

  private double ongoingCharge;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @JsonFormat
      (shape = JsonFormat.Shape.STRING, pattern = DateUtils.STANDARD_FORMAT)
  private Date launchDate;

  private String url;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @JsonFormat
      (shape = JsonFormat.Shape.STRING, pattern = DateUtils.STANDARD_FORMAT)
  private Date updated;

  @Indexed(unique = true)
  private String key;

  public InvestmentTrust() {
    super();
  }

  public InvestmentTrust(String identifier, String title, String description, String company,
                         String sedol, String isin, String currency, double totalAssets,
                         double grossGearing, String marketCap, String sharesInIssue,
                         double priceBuy, double priceSell, double priceChange, double premiumDiscount,
                         double dividendYield, String dividendFrequency, double estimatedNav,
                         double latestActualNav, String latestActualNavDate, double _12mAvgPremiumDiscount,
                         String navFrequency, String performanceFee, double ongoingCharge,
                         String launchDate, String updated) throws ParseException {
    this.identifier = identifier;
    this.title = title;
    this.description = description;
    this.company = company;
    this.sedol = sedol;
    this.isin = isin;
    this.currency = currency;
    this.totalAssets = totalAssets;
    this.grossGearing = grossGearing;
    this.marketCap = marketCap;
    this.sharesInIssue = sharesInIssue;
    this.priceBuy = priceBuy;
    this.priceSell = priceSell;
    this.priceChange = priceChange;
    this.premiumDiscount = premiumDiscount;
    this.dividendYield = dividendYield;
    this.dividendFrequency = dividendFrequency;
    this.estimatedNav = estimatedNav;
    this.latestActualNav = latestActualNav;
    this.setLatestActualNavDate(latestActualNavDate);
    this._12mAvgPremiumDiscount = _12mAvgPremiumDiscount;
    this.navFrequency = navFrequency;
    this.performanceFee = performanceFee;
    this.ongoingCharge = ongoingCharge;
    this.setLaunchDate(launchDate);
    this.setUrl();
    this.setUpdated(updated);
    this.setKey();
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
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

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public double getTotalAssets() {
    return totalAssets;
  }

  public void setTotalAssets(double totalAssets) {
    this.totalAssets = totalAssets;
  }

  public double getGrossGearing() {
    return grossGearing;
  }

  public void setGrossGearing(double grossGearing) {
    this.grossGearing = grossGearing;
  }

  public String getMarketCap() {
    return marketCap;
  }

  public void setMarketCap(String marketCap) {
    this.marketCap = marketCap;
  }

  public String getSharesInIssue() {
    return sharesInIssue;
  }

  public void setSharesInIssue(String sharesInIssue) {
    this.sharesInIssue = sharesInIssue;
  }

  public double getPriceBuy() {
    return priceBuy;
  }

  public void setPriceBuy(double priceBuy) {
    this.priceBuy = priceBuy;
  }

  public double getPriceSell() {
    return priceSell;
  }

  public void setPriceSell(double priceSell) {
    this.priceSell = priceSell;
  }

  public double getPriceChange() {
    return priceChange;
  }

  public void setPriceChange(double priceChange) {
    this.priceChange = priceChange;
  }

  public double getPremiumDiscount() {
    return premiumDiscount;
  }

  public void setPremiumDiscount(double premiumDiscount) {
    this.premiumDiscount = premiumDiscount;
  }

  public double getDividendYield() {
    return dividendYield;
  }

  public void setDividendYield(double dividendYield) {
    this.dividendYield = dividendYield;
  }

  public String getDividendFrequency() {
    return dividendFrequency;
  }

  public void setDividendFrequency(String dividendFrequency) {
    this.dividendFrequency = dividendFrequency;
  }

  public double getEstimatedNav() {
    return estimatedNav;
  }

  public void setEstimatedNav(double estimatedNav) {
    this.estimatedNav = estimatedNav;
  }

  public double getLatestActualNav() {
    return latestActualNav;
  }

  public void setLatestActualNav(double latestActualNav) {
    this.latestActualNav = latestActualNav;
  }

  public String getLatestActualNavDateLocalDateString() {
    return getDate(latestActualNavDate, DateUtils.STANDARD_FORMAT);
  }

  public Date getLatestActualNavDate() {
    return latestActualNavDate;
  }

  @JsonProperty("latestActualNavDate")
  public void setLatestActualNavDate(String latestActualNavDate) throws ParseException {
    if (!latestActualNavDate.equalsIgnoreCase("n/a")) {
      this.latestActualNavDate = getDate(latestActualNavDate, DateUtils.HL_FORMAT);
    }
  }

  public double get_12mAvgPremiumDiscount() {
    return _12mAvgPremiumDiscount;
  }

  public void set_12mAvgPremiumDiscount(double _12mAvgPremiumDiscount) {
    this._12mAvgPremiumDiscount = _12mAvgPremiumDiscount;
  }

  public String getNavFrequency() {
    return navFrequency;
  }

  public void setNavFrequency(String navFrequency) {
    this.navFrequency = navFrequency;
  }

  public String getPerformanceFee() {
    return performanceFee;
  }

  public void setPerformanceFee(String performanceFee) {
    this.performanceFee = performanceFee;
  }

  public double getOngoingCharge() {
    return ongoingCharge;
  }

  public void setOngoingCharge(double ongoingCharge) {
    this.ongoingCharge = ongoingCharge;
  }

  public String getLaunchDateLocalDateString() {
    return getDate(launchDate, DateUtils.STANDARD_FORMAT);
  }

  public Date getLaunchDate() {
    return launchDate;
  }

  @JsonProperty("launchDate")
  public void setLaunchDate(String launchDate) throws ParseException {
    if (!launchDate.equalsIgnoreCase("n/a")) {
      this.launchDate = getDate(launchDate, DateUtils.HL_FORMAT);
    }
  }

  public String getUrl() {
    return url;
  }

  public void setUrl() {
    this.url = "http://www.hl.co.uk/shares/shares-search-results/"
        + this.sedol;
  }

  public String getUpdatedLocalDateString() {
    return getDate(updated, DateUtils.STANDARD_FORMAT);
  }

  public Date getUpdated() {
    return updated;
  }

  @JsonProperty("updated")
  public void setUpdated(String updated) throws ParseException {
    this.updated = getDate(updated, DateUtils.STANDARD_FORMAT);
  }

  public String getKey() {
    return key;
  }

  public void setKey() {
    this.key = this.sedol + "-" + this.getUpdatedLocalDateString();
  }

  @Override
  public String toString() {
    return "InvestmentTrust{" +
        "identifier='" + identifier + '\'' +
        ", title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", company='" + company + '\'' +
        ", sedol='" + sedol + '\'' +
        ", isin='" + isin + '\'' +
        ", currency='" + currency + '\'' +
        ", totalAssets=" + totalAssets +
        ", grossGearing=" + grossGearing +
        ", marketCap=" + marketCap +
        ", sharesInIssue=" + sharesInIssue +
        ", priceBuy=" + priceBuy +
        ", priceSell=" + priceSell +
        ", priceChange=" + priceChange +
        ", premiumDiscount=" + premiumDiscount +
        ", dividendYield=" + dividendYield +
        ", dividendFrequency='" + dividendFrequency + '\'' +
        ", estimatedNav=" + estimatedNav +
        ", latestActualNav=" + latestActualNav +
        ", latestActualNavDate=" + latestActualNavDate +
        ", _12mAvgPremiumDiscount=" + _12mAvgPremiumDiscount +
        ", navFrequency='" + navFrequency + '\'' +
        ", performanceFee='" + performanceFee + '\'' +
        ", ongoingCharge=" + ongoingCharge +
        ", launchDate=" + launchDate +
        ", updated=" + updated +
        ", key='" + key + '\'' +
        '}';
  }
}
