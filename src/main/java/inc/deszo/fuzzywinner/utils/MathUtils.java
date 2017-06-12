package inc.deszo.fuzzywinner.utils;

public class MathUtils {

  public static double round(double value, int precision) {
    int scale = (int) Math.pow(10, precision);
    return (double) Math.round(value * scale) / scale;
  }
}
