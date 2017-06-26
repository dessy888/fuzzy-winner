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
@Document(collection = "fundperformances")
@CompoundIndexes(value =
    {
        @CompoundIndex(name = "Sedol_isin_ftSymbol_cobDate_ind1", def = "{'sedol': 1, 'isin': 1, 'ftSymbol': 1, 'cobDate': 1}", unique = true)
    }
)
public class FundPerformance {

  @Indexed
  private String sedol;

  private String isin;

  private String ftSymbol;

  private String reportName;

  @JsonProperty("1D")
  private double _1D;

  @JsonProperty("3D")
  private double _3D;

  @JsonProperty("5D")
  private double _5D;

  @JsonProperty("1W")
  private double _1W;

  @JsonProperty("2W")
  private double _2W;

  @JsonProperty("3W")
  private double _3W;

  @JsonProperty("1M")
  private double _1M;

  @JsonProperty("2M")
  private double _2M;

  @JsonProperty("3M")
  private double _3M;

  @JsonProperty("4M")
  private double _4M;

  @JsonProperty("5M")
  private double _5M;

  @JsonProperty("6M")
  private double _6M;

  @JsonProperty("7M")
  private double _7M;

  @JsonProperty("8M")
  private double _8M;

  @JsonProperty("9M")
  private double _9M;

  @JsonProperty("10M")
  private double _10M;

  @JsonProperty("11M")
  private double _11M;

  @JsonProperty("1Y")
  private double _1Y;

  @JsonProperty("2Y")
  private double _2Y;

  @JsonProperty("3Y")
  private double _3Y;

  @JsonProperty("4Y")
  private double _4Y;

  @JsonProperty("5Y")
  private double _5Y;

  @JsonProperty("6Y")
  private double _6Y;

  @JsonProperty("7Y")
  private double _7Y;

  @JsonProperty("8Y")
  private double _8Y;

  @JsonProperty("9Y")
  private double _9Y;

  @JsonProperty("10Y")
  private double _10Y;

  @JsonProperty("11Y")
  private double _11Y;

  @JsonProperty("12Y")
  private double _12Y;

  @JsonProperty("13Y")
  private double _13Y;

  @JsonProperty("14Y")
  private double _14Y;

  @JsonProperty("15Y")
  private double _15Y;

  @JsonProperty("16Y")
  private double _16Y;

  @JsonProperty("17Y")
  private double _17Y;

  @JsonProperty("18Y")
  private double _18Y;

  @JsonProperty("19Y")
  private double _19Y;

  @JsonProperty("20Y")
  private double _20Y;

  @JsonProperty("ALL")
  private double _ALL;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date cobDate;

  @Indexed(unique = true)
  private String key;

  public FundPerformance() {
    super();
  }

  public FundPerformance(String sedol, String isin, String ftSymbol, String reportName, double _1D, double _3D,
                         double _5D, double _1W, double _2W,
                         double _3W, double _1M, double _2M, double _3M, double _4M, double _5M, double _6M, double _7M,
                         double _8M, double _9M, double _10M, double _11M, double _1Y, double _2Y, double _3Y, double _4Y,
                         double _5Y, double _6Y, double _7Y, double _8Y, double _9Y, double _10Y, double _11Y, double _12Y,
                         double _13Y, double _14Y, double _15Y, double _16Y, double _17Y, double _18Y, double _19Y, double _20Y,
                         double _ALL, Date cobDate) {
    this.sedol = sedol;
    this.isin = isin;
    this.ftSymbol = ftSymbol;
    this.reportName = reportName;
    this._1D = _1D;
    this._3D = _3D;
    this._5D = _5D;
    this._1W = _1W;
    this._2W = _2W;
    this._3W = _3W;
    this._1M = _1M;
    this._2M = _2M;
    this._3M = _3M;
    this._4M = _4M;
    this._5M = _5M;
    this._6M = _6M;
    this._7M = _7M;
    this._8M = _8M;
    this._9M = _9M;
    this._10M = _10M;
    this._11M = _11M;
    this._1Y = _1Y;
    this._2Y = _2Y;
    this._3Y = _3Y;
    this._4Y = _4Y;
    this._5Y = _5Y;
    this._6Y = _6Y;
    this._7Y = _7Y;
    this._8Y = _8Y;
    this._9Y = _9Y;
    this._10Y = _10Y;
    this._11Y = _11Y;
    this._12Y = _12Y;
    this._13Y = _13Y;
    this._14Y = _14Y;
    this._15Y = _15Y;
    this._16Y = _16Y;
    this._17Y = _17Y;
    this._18Y = _18Y;
    this._19Y = _19Y;
    this._20Y = _20Y;
    this._ALL = _ALL;
    this.cobDate = cobDate;
    this.setKey();
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

  public String getReportName() {
    return reportName;
  }

  public void setReportName(String reportName) {
    this.reportName = reportName;
  }

  public double get_1D() {
    return _1D;
  }

  public void set_1D(double _1D) {
    this._1D = _1D;
  }

  public double get_3D() {
    return _3D;
  }

  public void set_3D(double _3D) {
    this._3D = _3D;
  }

  public double get_5D() {
    return _5D;
  }

  public void set_5D(double _5D) {
    this._5D = _5D;
  }

  public double get_1W() {
    return _1W;
  }

  public void set_1W(double _1W) {
    this._1W = _1W;
  }

  public double get_2W() {
    return _2W;
  }

  public void set_2W(double _2W) {
    this._2W = _2W;
  }

  public double get_3W() {
    return _3W;
  }

  public void set_3W(double _3W) {
    this._3W = _3W;
  }

  public double get_1M() {
    return _1M;
  }

  public void set_1M(double _1M) {
    this._1M = _1M;
  }

  public double get_2M() {
    return _2M;
  }

  public void set_2M(double _2M) {
    this._2M = _2M;
  }

  public double get_3M() {
    return _3M;
  }

  public void set_3M(double _3M) {
    this._3M = _3M;
  }

  public double get_4M() {
    return _4M;
  }

  public void set_4M(double _4M) {
    this._4M = _4M;
  }

  public double get_5M() {
    return _5M;
  }

  public void set_5M(double _5M) {
    this._5M = _5M;
  }

  public double get_6M() {
    return _6M;
  }

  public void set_6M(double _6M) {
    this._6M = _6M;
  }

  public double get_7M() {
    return _7M;
  }

  public void set_7M(double _7M) {
    this._7M = _7M;
  }

  public double get_8M() {
    return _8M;
  }

  public void set_8M(double _8M) {
    this._8M = _8M;
  }

  public double get_9M() {
    return _9M;
  }

  public void set_9M(double _9M) {
    this._9M = _9M;
  }

  public double get_10M() {
    return _10M;
  }

  public void set_10M(double _10M) {
    this._10M = _10M;
  }

  public double get_11M() {
    return _11M;
  }

  public void set_11M(double _11M) {
    this._11M = _11M;
  }

  public double get_1Y() {
    return _1Y;
  }

  public void set_1Y(double _1Y) {
    this._1Y = _1Y;
  }

  public double get_2Y() {
    return _2Y;
  }

  public void set_2Y(double _2Y) {
    this._2Y = _2Y;
  }

  public double get_3Y() {
    return _3Y;
  }

  public void set_3Y(double _3Y) {
    this._3Y = _3Y;
  }

  public double get_4Y() {
    return _4Y;
  }

  public void set_4Y(double _4Y) {
    this._4Y = _4Y;
  }

  public double get_5Y() {
    return _5Y;
  }

  public void set_5Y(double _5Y) {
    this._5Y = _5Y;
  }

  public double get_6Y() {
    return _6Y;
  }

  public void set_6Y(double _6Y) {
    this._6Y = _6Y;
  }

  public double get_7Y() {
    return _7Y;
  }

  public void set_7Y(double _7Y) {
    this._7Y = _7Y;
  }

  public double get_8Y() {
    return _8Y;
  }

  public void set_8Y(double _8Y) {
    this._8Y = _8Y;
  }

  public double get_9Y() {
    return _9Y;
  }

  public void set_9Y(double _9Y) {
    this._9Y = _9Y;
  }

  public double get_10Y() {
    return _10Y;
  }

  public void set_10Y(double _10Y) {
    this._10Y = _10Y;
  }

  public double get_11Y() {
    return _11Y;
  }

  public void set_11Y(double _11Y) {
    this._11Y = _11Y;
  }

  public double get_12Y() {
    return _12Y;
  }

  public void set_12Y(double _12Y) {
    this._12Y = _12Y;
  }

  public double get_13Y() {
    return _13Y;
  }

  public void set_13Y(double _13Y) {
    this._13Y = _13Y;
  }

  public double get_14Y() {
    return _14Y;
  }

  public void set_14Y(double _14Y) {
    this._14Y = _14Y;
  }

  public double get_15Y() {
    return _15Y;
  }

  public void set_15Y(double _15Y) {
    this._15Y = _15Y;
  }

  public double get_16Y() {
    return _16Y;
  }

  public void set_16Y(double _16Y) {
    this._16Y = _16Y;
  }

  public double get_17Y() {
    return _17Y;
  }

  public void set_17Y(double _17Y) {
    this._17Y = _17Y;
  }

  public double get_18Y() {
    return _18Y;
  }

  public void set_18Y(double _18Y) {
    this._18Y = _18Y;
  }

  public double get_19Y() {
    return _19Y;
  }

  public void set_19Y(double _19Y) {
    this._19Y = _19Y;
  }

  public double get_20Y() {
    return _20Y;
  }

  public void set_20Y(double _20Y) {
    this._20Y = _20Y;
  }

  public double get_ALL() {
    return _ALL;
  }

  public void set_ALL(double _ALL) {
    this._ALL = _ALL;
  }

  public void setCobDate(Date cobDate) {
    this.cobDate = cobDate;
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

  public String getKey() {
    return key;
  }

  public void setKey() {
    this.key = this.sedol + "-" + this.getCobLocalDateString();
  }
}
