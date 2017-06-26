package inc.deszo.fuzzywinner.utils;

public class CurrencyUtils {

  public static final String HL_GBX_CURRENCY = "GBX";

  public static final String HL_USD_CURRENCY = "USD";

  public static double getAmount(String currencyAmt, String currency) {

    String newCurrencyAmt = "0";

    if (currency.equalsIgnoreCase(HL_GBX_CURRENCY)) {
      newCurrencyAmt = currencyAmt
          .replace("p","")
          .replace(",","");
    } else if (currency.equalsIgnoreCase(HL_USD_CURRENCY)) {
      newCurrencyAmt = currencyAmt
          .replace("$","");
    }

    return Double.valueOf(newCurrencyAmt);
  }
}
