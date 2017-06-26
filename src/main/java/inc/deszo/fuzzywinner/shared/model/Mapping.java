package inc.deszo.fuzzywinner.shared.model;

import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

public abstract class Mapping {

  @Indexed
  private String sedol;

  @Indexed
  private String isin;

  @Indexed
  private String ftSymbol;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date inceptionDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date updated;

  private Type type;

  public Mapping() {
    super();
  }

  public Mapping(String sedol, String isin, String updated, Type type) throws ParseException {
    this.sedol = sedol;
    this.isin = isin;
    this.setUpdated(updated);
    this.type = type;
  }

  public Mapping(String sedol, String isin, String ftSymbol, String inceptionDate,
                 String updated, Type type) throws ParseException {
    this.sedol = sedol;
    this.isin = isin;
    this.ftSymbol = ftSymbol;
    this.setInceptionDate(inceptionDate);
    this.setUpdated(updated);
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

  public String getInceptionLocalDate() {
    return DateUtils.getDate(inceptionDate, DateUtils.STANDARD_FORMAT);
  }

  public Date getInceptionDate() {
    return inceptionDate;
  }

  public void setInceptionDate(String inceptionDate) throws ParseException {
    this.inceptionDate = DateUtils.getDate(inceptionDate, DateUtils.STANDARD_FORMAT);
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

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }
}
