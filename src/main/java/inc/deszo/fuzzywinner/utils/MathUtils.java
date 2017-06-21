package inc.deszo.fuzzywinner.utils;

public class MathUtils {

  public static double round(double value, int precision) {
    int scale = (int) Math.pow(10, precision);
    return (double) Math.round(value * scale) / scale;
  }

  public static double convert(String value) {
    if (!value.equalsIgnoreCase("n/a")) {
      return Double.valueOf(value
          .replace(",","")
          .replace("%","")
          .replace("£","")
          .replace(" million","")
          .replace("m",""));
    } else {
      return 0;
    }
  }
}
