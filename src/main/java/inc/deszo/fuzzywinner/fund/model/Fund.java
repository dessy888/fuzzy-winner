package inc.deszo.fuzzywinner.fund.model;

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

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "funds")
@CompoundIndexes(value =
    {
        @CompoundIndex(name = "Sedol_updated_ind1", def = "{'sedol': 1, 'updated': 1}", unique = true)
    }
)
public class Fund {

  private String sedol;

  private String name;

  private String unitType;

  private String loaded;

  private String company;

  private String sector;

  private String plusFund;

  @JsonProperty("price_sell")
  private double priceSell;

  @JsonProperty("price_buy")
  private double priceBuy;

  @JsonProperty("price_change")
  private double priceChange;

  private double yield;

  private double initialCharge;

  private double annualCharge;

  private double annualSaving;

  private double netAnnualCharge;

  private String discountedCode;

  private String perf12m;

  private String perf12t24m;

  private String perf24t36m;

  private String perf36t48m;

  private String perf48t60m;

  private double fundSize;

  private String incomeFrequency;

  private String paymentType;

  private int numHoldings;

  private String url;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date updated;

  @Indexed(unique = true)
  private String key;

  public Fund() {
    super();
  }

  public Fund(String sedol, String name, String unitType, String loaded, String company, String sector,
              String plusFund, String priceSell, String priceBuy, String priceChange, String yield,
              String initialCharge, String annualCharge, String annualSaving, String netAnnualCharge,
              String discountedCode, String perf12m, String perf12t24m, String perf24t36m, String perf36t48m,
              String perf48t60m, String fundSize, String incomeFrequency, String paymentType, String numHoldings,
              String updated) throws ParseException {

    this(sedol, name, unitType, loaded, company, sector, plusFund, Double.valueOf(priceSell),
        Double.valueOf(priceBuy), Double.valueOf(priceChange), Double.valueOf(yield), Double.valueOf(initialCharge),
        Double.valueOf(annualCharge), Double.valueOf(annualSaving), Double.valueOf(netAnnualCharge),
        discountedCode, perf12m, perf12t24m, perf24t36m, perf36t48m, perf48t60m, Double.valueOf(fundSize),
        incomeFrequency, paymentType, Integer.valueOf(numHoldings), updated);

  }

  public Fund(String sedol, String name, String unitType, String loaded, String company, String sector,
              String plusFund, double priceSell, double priceBuy, double priceChange, double yield,
              double initialCharge, double annualCharge, double annualSaving, double netAnnualCharge,
              String discountedCode, String perf12m, String perf12t24m, String perf24t36m, String perf36t48m,
              String perf48t60m, double fundSize, String incomeFrequency, String paymentType,
              int numHoldings, String updated) throws ParseException {
    this.sedol = sedol;
    this.name = name;
    this.unitType = unitType;
    this.loaded = loaded;
    this.company = company;
    this.sector = sector;
    this.plusFund = plusFund;
    this.priceSell = priceSell;
    this.priceBuy = priceBuy;
    this.priceChange = priceChange;
    this.yield = yield;
    this.initialCharge = initialCharge;
    this.annualCharge = annualCharge;
    this.annualSaving = annualSaving;
    this.netAnnualCharge = netAnnualCharge;
    this.discountedCode = discountedCode;
    this.perf12m = perf12m;
    this.perf12t24m = perf12t24m;
    this.perf24t36m = perf24t36m;
    this.perf36t48m = perf36t48m;
    this.perf48t60m = perf48t60m;
    this.fundSize = fundSize;
    this.incomeFrequency = incomeFrequency;
    this.paymentType = paymentType;
    this.numHoldings = numHoldings;
    this.setUrl();
    this.setUpdated(updated);
    this.setKey();
  }

  public String getSedol() {
    return sedol;
  }

  public void setSedol(String sedol) {
    this.sedol = sedol;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUnitType() {
    return unitType;
  }

  public void setUnitType(String unitType) {
    this.unitType = unitType;
  }

  public String getLoaded() {
    return loaded;
  }

  public void setLoaded(String loaded) {
    this.loaded = loaded;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getSector() {
    return sector;
  }

  public void setSector(String sector) {
    this.sector = sector;
  }

  public String getPlusFund() {
    return plusFund;
  }

  public void setPlusFund(String plusFund) {
    this.plusFund = plusFund;
  }

  public double getPriceSell() {
    return priceSell;
  }

  public void setPriceSell(double priceSell) {
    this.priceSell = priceSell;
  }

  public double getPriceBuy() {
    return priceBuy;
  }

  public void setPriceBuy(double priceBuy) {
    this.priceBuy = priceBuy;
  }

  public double getPriceChange() {
    return priceChange;
  }

  public void setPriceChange(double priceChange) {
    this.priceChange = priceChange;
  }

  public double getYield() {
    return yield;
  }

  public void setYield(double yield) {
    this.yield = yield;
  }

  public double getInitialCharge() {
    return initialCharge;
  }

  public void setInitialCharge(double initialCharge) {
    this.initialCharge = initialCharge;
  }

  public double getAnnualCharge() {
    return annualCharge;
  }

  public void setAnnualCharge(double annualCharge) {
    this.annualCharge = annualCharge;
  }

  public double getAnnualSaving() {
    return annualSaving;
  }

  public void setAnnualSaving(double annualSaving) {
    this.annualSaving = annualSaving;
  }

  public double getNetAnnualCharge() {
    return netAnnualCharge;
  }

  public void setNetAnnualCharge(double netAnnualCharge) {
    this.netAnnualCharge = netAnnualCharge;
  }

  public String getDiscountedCode() {
    return discountedCode;
  }

  public void setDiscountedCode(String discountedCode) {
    this.discountedCode = discountedCode;
  }

  public String getPerf12m() {
    return perf12m;
  }

  public void setPerf12m(String perf12m) {
    this.perf12m = perf12m;
  }

  public String getPerf12t24m() {
    return perf12t24m;
  }

  public void setPerf12t24m(String perf12t24m) {
    this.perf12t24m = perf12t24m;
  }

  public String getPerf24t36m() {
    return perf24t36m;
  }

  public void setPerf24t36m(String perf24t36m) {
    this.perf24t36m = perf24t36m;
  }

  public String getPerf36t48m() {
    return perf36t48m;
  }

  public void setPerf36t48m(String perf36t48m) {
    this.perf36t48m = perf36t48m;
  }

  public String getPerf48t60m() {
    return perf48t60m;
  }

  public void setPerf48t60m(String perf48t60m) {
    this.perf48t60m = perf48t60m;
  }

  public double getFundSize() {
    return fundSize;
  }

  public void setFundSize(double fundSize) {
    this.fundSize = fundSize;
  }

  public String getIncomeFrequency() {
    return incomeFrequency;
  }

  public void setIncomeFrequency(String incomeFrequency) {
    this.incomeFrequency = incomeFrequency;
  }

  public String getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public int getNumHoldings() {
    return numHoldings;
  }

  public void setNumHoldings(int numHoldings) {
    this.numHoldings = numHoldings;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl() {
    this.url = "http://www.hl.co.uk/funds/fund-discounts,-prices--and--factsheets/search-results/"
        + this.sedol;
  }

  public String getUpdatedLocalDateString() {
    return DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT);
  }

  public Date getUpdated() {
    return updated;
  }

  @JsonProperty("updated")
  public void setUpdated(String updated) throws ParseException {
    this.updated = DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT);
  }

  public String getKey() {
    return key;
  }

  public void setKey() {
    this.key = this.sedol + "-" + this.getUpdatedLocalDateString();
  }
}
