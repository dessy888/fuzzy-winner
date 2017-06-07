package inc.deszo.fuzzywinner.repository;

import java.util.Date;

public interface FundRepositoryCustom {

    int updateFund(String sedol, String isin, String name, String unitType, String loaded, String company,
                   String sector, String plusFund, double price_sell, double price_buy, double price_change,
                   double yield, double initialCharge, double annualCharge, double annualSaving,
                   double netAnnualCharge, String discountedCode, String perf12m, String perf12t24m,
                   String perf24t36m, String perf36t48m, String perf48t60m, double fundSize,
                   String incomeFrequency, String paymentType, int numHoldings, Date updated);

}
