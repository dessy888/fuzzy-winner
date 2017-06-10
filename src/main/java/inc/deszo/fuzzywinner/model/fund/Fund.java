package inc.deszo.fuzzywinner.model.fund;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "fund")
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

    private double price_sell;

    private double price_buy;

    private double price_change;

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date updated;

    public Fund () {
        super();
    }

    public Fund(String sedol, String name, String unitType, String loaded, String company, String sector,
                String plusFund, String price_sell, String price_buy, String price_change, String yield,
                String initialCharge, String annualCharge, String annualSaving, String netAnnualCharge,
                String discountedCode, String perf12m, String perf12t24m, String perf24t36m, String perf36t48m,
                String perf48t60m, String fundSize, String incomeFrequency, String paymentType, String numHoldings,
                String updated) throws ParseException {

        this(sedol, name, unitType, loaded, company, sector, plusFund, Double.valueOf(price_sell),
                Double.valueOf(price_buy), Double.valueOf(price_change), Double.valueOf(yield), Double.valueOf(initialCharge),
                Double.valueOf(annualCharge), Double.valueOf(annualSaving), Double.valueOf(netAnnualCharge),
                discountedCode, perf12m, perf12t24m, perf24t36m, perf36t48m, perf48t60m, Double.valueOf(fundSize),
                incomeFrequency, paymentType, Integer.valueOf(numHoldings), updated);

    }

    public Fund(String sedol, String name, String unitType, String loaded, String company, String sector,
                String plusFund, double price_sell, double price_buy, double price_change, double yield,
                double initialCharge, double annualCharge, double annualSaving, double netAnnualCharge,
                String discountedCode, String perf12m, String perf12t24m, String perf24t36m, String perf36t48m,
                String perf48t60m, double fundSize, String incomeFrequency, String paymentType, int numHoldings, String updated) throws ParseException {
        this.sedol = sedol;
        this.name = name;
        this.unitType = unitType;
        this.loaded = loaded;
        this.company = company;
        this.sector = sector;
        this.plusFund = plusFund;
        this.price_sell = price_sell;
        this.price_buy = price_buy;
        this.price_change = price_change;
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
        this.setUpdated(updated);
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

    public double getPrice_sell() {
        return price_sell;
    }

    public void setPrice_sell(double price_sell) {
        this.price_sell = price_sell;
    }

    public double getPrice_buy() {
        return price_buy;
    }

    public void setPrice_buy(double price_buy) {
        this.price_buy = price_buy;
    }

    public double getPrice_change() {
        return price_change;
    }

    public void setPrice_change(double price_change) {
        this.price_change = price_change;
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

    public String getUpdatedLocalDateString() {
        return DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT);
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) throws ParseException {
        this.updated = DateUtils.getDate(updated, DateUtils.STANDARD_FORMAT);
    }
}
