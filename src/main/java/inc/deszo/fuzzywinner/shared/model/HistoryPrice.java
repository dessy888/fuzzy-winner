package inc.deszo.fuzzywinner.shared.model;

import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

public abstract class HistoryPrice {

  @Indexed
  private String sedol;

  @Indexed
  private String isin;

  @Indexed
  private String ftSymbol;

  private Type type;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date cobDate;

  public HistoryPrice() {
    super();
  }

  public HistoryPrice(String sedol, String isin, String ftSymbol, String cobDate,
                      Type type) throws ParseException {
    this.sedol = sedol;
    this.isin = isin;
    this.ftSymbol = ftSymbol;
    this.setCobDate(cobDate);
    this.type = type;
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

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
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