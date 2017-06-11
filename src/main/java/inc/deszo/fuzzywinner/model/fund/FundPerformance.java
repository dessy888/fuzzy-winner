package inc.deszo.fuzzywinner.model.fund;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inc.deszo.fuzzywinner.utils.DateUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "fundperformance")
@CompoundIndexes(value =
        {
                @CompoundIndex(name = "Sedol_cobDate", def = "{'sedol': 1, 'cobDate': 1}", unique = true)
        }
)
public class FundPerformance {

    @Indexed
    private String sedol;

    private double _1D;

    private double _3D;

    private double _5D;

    private double _1W;

    private double _2W;

    private double _3W;

    private double _1M;

    private double _2M;

    private double _3M;

    private double _4M;

    private double _5M;

    private double _6M;

    private double _7M;

    private double _8M;

    private double _9M;

    private double _10M;

    private double _11M;

    private double _YTD;

    private double _1Y;

    private double _2Y;

    private double _3Y;

    private double _4Y;

    private double _5Y;

    private double _6Y;

    private double _7Y;

    private double _8Y;

    private double _9Y;

    private double _10Y;

    private double _11Y;

    private double _12Y;

    private double _13Y;

    private double _14Y;

    private double _15Y;

    private double _16Y;

    private double _17Y;

    private double _18Y;

    private double _19Y;

    private double _20Y;

    private double _ALL;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date cobDate;

    public FundPerformance() {
        super();
    }

    public FundPerformance(String sedol, double _1D, double _5D, double _1M, double _3M, double _6M, double _YTD,
                           double _1Y, double _2Y, double _3Y, double _4Y, double _5Y, double _6Y, double _7Y,
                           double _8Y, double _9Y, double _10Y, double _11Y, double _12Y, double _13Y, double _14Y,
                           double _15Y, double _16Y, double _17Y, double _18Y, double _19Y, double _20Y, double _ALL,
                           String cobDate) throws ParseException {
        this.sedol = sedol;
        this._1D = _1D;
        this._5D = _5D;
        this._1M = _1M;
        this._3M = _3M;
        this._6M = _6M;
        this._YTD = _YTD;
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
        this.setCobDate(cobDate);
    }

    public String getSedol() {
        return sedol;
    }

    public void setSedol(String sedol) {
        this.sedol = sedol;
    }

    public double get_1D() {
        return _1D;
    }

    public void set_1D(double _1D) {
        this._1D = _1D;
    }

    public double get_5D() {
        return _5D;
    }

    public void set_5D(double _5D) {
        this._5D = _5D;
    }

    public double get_1M() {
        return _1M;
    }

    public void set_1M(double _1M) {
        this._1M = _1M;
    }

    public double get_3M() {
        return _3M;
    }

    public void set_3M(double _3M) {
        this._3M = _3M;
    }

    public double get_6M() {
        return _6M;
    }

    public void set_6M(double _6M) {
        this._6M = _6M;
    }

    public double get_YTD() {
        return _YTD;
    }

    public void set_YTD(double _YTD) {
        this._YTD = _YTD;
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
