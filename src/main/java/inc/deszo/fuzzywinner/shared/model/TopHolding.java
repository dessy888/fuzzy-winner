package inc.deszo.fuzzywinner.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

public class TopHolding {

  @Indexed
  private String sedol;

  @Indexed
  private String position;

  private String security;

  private String weight;

  private String url;

  private Type type;

  @Indexed
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date updated;

  public TopHolding() {
    super();
  }

  public TopHolding(String sedol, String updated, Type type, String position,
                      String security, String weight, String url) throws ParseException {
    this.sedol = sedol;
    this.setUpdated(updated);
    this.type = type;
    this.position = position;
    this.security = security;
    this.weight = weight;
    this.url = url;
  }

  public String getSedol() {
    return sedol;
  }

  public void setSedol(String sedol) {
    this.sedol = sedol;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
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

  public String getSecurity() {
    return security;
  }

  public void setSecurity(String security) {
    this.security = security;
  }

  public String getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }
}