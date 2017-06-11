package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;
import inc.deszo.fuzzywinner.model.fund.FundPerformance;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class FundPerformanceRepositoryImpl implements FundPerformanceRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(FundPerformanceRepositoryImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private FundHistoryPricesRepository fundHistoryPricesRepository;

    @Override
    public void calculate(boolean plusFundOnly) {

        List<FundHistoryPrices> fundHistoryPrices = fundHistoryPricesRepository.getDistinctSedol();
        FundPerformance fundPerformance;

        for (FundHistoryPrices fund : fundHistoryPrices) {
            logger.info("Processing Sedol: {} Isin: {} ftSymbol: {}.", fund.getSedol(),
                    fund.getIsin(), fund.getFtSymbol());

            fundPerformance = new FundPerformance();

            //get the last cob fund price
            List<FundHistoryPrices> lastCobPrice = fundHistoryPricesRepository.getLastUpdated(fund.getSedol(),
                    fund.getIsin(), fund.getFtSymbol());
            Double lastClosePrice = 0.0;
            String lastCobDate = "";
            for (FundHistoryPrices fundLastCob : lastCobPrice) {
                lastClosePrice = fundLastCob.getPrice_close();
                lastCobDate = fundLastCob.getCobLocalDateString();
                logger.info("Last Close Price: {} on {}.", lastClosePrice, lastCobDate);
            }

            //get the oldest fund price
            List<FundHistoryPrices> inceptionPrice = fundHistoryPricesRepository.getOldestPrice(fund.getSedol(),
                    fund.getIsin(), fund.getFtSymbol());
            Double inceptionClosePrice = 0.0;
            String inceptionCobDate = "";
            for (FundHistoryPrices fundInceptionCob : inceptionPrice) {
                inceptionClosePrice = fundInceptionCob.getPrice_close();
                inceptionCobDate = fundInceptionCob.getCobLocalDateString();
                logger.info("Inception Close Price: {} on {}.", inceptionClosePrice, inceptionCobDate);
            }

            String todayDate = DateUtils.getTodayDate(DateUtils.STANDARD_FORMAT);

            //1D performance
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-1D");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-3D");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-5D");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-1W");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-2W");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-3W");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-1M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-2M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-3M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-4M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-5M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-6M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-7M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-8M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-9M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-10M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-11M");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-1Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-2Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-3Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-4Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-5Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-6Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-7Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-8Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-9Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-10Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-11Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-12Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-13Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-14Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-15Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-16Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-17Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-18Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-19Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, "-20Y");
            calculatePerformanceBetweenTwoDates(lastCobPrice.get(0), todayDate, inceptionCobDate);

        }
    }

    private double calculatePerformanceBetweenTwoDates(FundHistoryPrices fundLastCob, String todayDate, String dateDiff) {

        String date;

        if (dateDiff.length() == 10) {
            date = dateDiff;
        } else {
            date = DateUtils.addToDate(todayDate, DateUtils.STANDARD_FORMAT, dateDiff);
        }

        List<FundHistoryPrices> price = fundHistoryPricesRepository.getFundPriceByDate(fundLastCob.getSedol(),
                fundLastCob.getIsin(), fundLastCob.getFtSymbol(), date);

        Double closePrice;
        String cobDate;

        Double priceDiff = 0.0;
        Double priceDiffInPercent = 0.0;

        for (FundHistoryPrices fund : price) {
            closePrice = fund.getPrice_close();
            cobDate = fund.getCobLocalDateString();

            priceDiff = fundLastCob.getPrice_close() - closePrice;
            priceDiffInPercent = MathUtils.round((priceDiff / closePrice) * 100.0, 1);
            logger.info("{} {} {} vs {} {} -> {}.", dateDiff, closePrice, cobDate, fundLastCob.getPrice_close(),
                    fundLastCob.getCobLocalDateString(), priceDiffInPercent);
        }

        return priceDiffInPercent;
    }
}
