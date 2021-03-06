package inc.deszo.fuzzywinner.fund.repository;

import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.fund.model.Fund;
import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.fund.model.FundMapping;
import inc.deszo.fuzzywinner.fund.model.FundPerformance;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class FundPerformancesRepositoryImpl implements FundPerformancesRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(FundPerformancesRepositoryImpl.class);

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  private FundHistoryPricesRepository fundHistoryPricesRepository;

  @Autowired
  private FundPerformancesRepository fundPerformancesRepository;

  @Autowired
  private FundMappingsRepository fundMappingsRepository;

  @Autowired
  private FundsRepository fundsRepository;

  @Override
  public void calculate(LocalDate cobDate, boolean plusFundOnly, boolean overrideFundPerformance) throws ParseException {

    //Update fund keys
    //logger.info("Number of funds key updated: {}.", fundPerformancesRepository.updateKey());

    List<FundHistoryPrice> fundHistoryPrices = fundHistoryPricesRepository.getDistinctSedol();
    int fundCount = 1;
    int numOfPerformanceCalculated = 0;

    for (FundHistoryPrice fund : fundHistoryPrices) {
      int numOfFundPerformanceCalculated = calculateFund(fund, fundCount, cobDate, plusFundOnly, overrideFundPerformance);

      if (numOfFundPerformanceCalculated > 0) {
        numOfPerformanceCalculated += calculateFund(fund, fundCount, cobDate, plusFundOnly, overrideFundPerformance);
        fundCount++;
      }
    }

    logger.info("*****Number of Fund Performance Calculated: {}", numOfPerformanceCalculated);
  }

  @Override
  public int calculateFund(FundHistoryPrice fund, int fundCount, LocalDate cobDate, boolean plusFundOnly,
                            boolean overrideFundPerformance) throws ParseException {

    int numOfPerformanceCalculated = 0;

    //process only plusFund
    if (plusFundOnly) {
      FundMapping fundMapping = fundMappingsRepository.findFirstBySedol(fund.getSedol());
      if (fundMapping.getPlusFund().equalsIgnoreCase("false")) {
        return numOfPerformanceCalculated;
      }
    }

    //fundCount++;
    logger.info("Fund {}, Sedol {}.", fundCount, fund.getSedol());

    logger.info("Processing Sedol: {} Isin: {} ftSymbol: {}.", fund.getSedol(),
        fund.getIsin(), fund.getFtSymbol());

    String lastCobDate = "";
    FundHistoryPrice lastCobPrice = null;

    if (cobDate == null) {
      //get the last cob fund price
      List<FundHistoryPrice> lastFTCobPrice = fundHistoryPricesRepository.getLastUpdated(fund.getSedol(),
          fund.getIsin(), fund.getFtSymbol());
      Double lastFTClosePrice;
      String lastFTCobDate = "";
      for (FundHistoryPrice fundLastCob : lastFTCobPrice) {
        lastFTClosePrice = fundLastCob.getPrice_close();
        lastFTCobDate = fundLastCob.getCobLocalDateString();
        logger.info("Last FT Close Price: {} on {}.", lastFTClosePrice, lastFTCobDate);
      }

      //get the last updated fund price from HL
      List<Fund> lastHLCobPrice = fundsRepository.getLastUpdated(fund.getSedol());
      Double lastHLClosePrice;
      String lastHLCobDate = "";
      for (Fund fundLastCob : lastHLCobPrice) {
        lastHLClosePrice = fundLastCob.getPriceSell();
        lastHLCobDate = fundLastCob.getUpdatedLocalDateString();
        logger.info("Last HL Close Price: {} on {}.", lastHLClosePrice, lastHLCobDate);
      }

      // if FT cob date is > than HL cob date, use HL.
      if (DateUtils.diffBetTwoDates(lastHLCobDate, lastFTCobDate, DateUtils.STANDARD_FORMAT) < 0) {

        //note there is a price format difference between HL and FT so get the closing price from FT.
        List<FundHistoryPrice> lastFTCobPriceUsingHLCobDate = fundHistoryPricesRepository.getFundPriceByDate(fund.getSedol(),
            fund.getIsin(), fund.getFtSymbol(), lastHLCobDate);
        for (FundHistoryPrice fundLastCob : lastFTCobPriceUsingHLCobDate) {
          lastCobDate = fundLastCob.getCobLocalDateString();
          logger.info("Last FT Close Price using HL cobDate: {} on {}.", fundLastCob.getPrice_close(),
              lastCobDate);
        }

        lastCobPrice = lastFTCobPriceUsingHLCobDate.get(0);
      } else {
        lastCobDate = lastFTCobDate;
        lastCobPrice = lastFTCobPrice.get(0);
      }
      logger.info("Last Close Price used: {} on {}.", lastCobPrice.getPrice_close(), lastCobDate);

    } else {
      lastCobDate = DateUtils.getLocalDate(cobDate, DateUtils.STANDARD_FORMAT);
      List<FundHistoryPrice> lastCobPrices = fundHistoryPricesRepository.getFundPriceByDate(fund.getSedol(),
          fund.getIsin(), fund.getFtSymbol(), lastCobDate);

      logger.info("Using Close Date: {}", lastCobDate);

      Boolean priceFound = false;

      for (FundHistoryPrice fundLastCob : lastCobPrices) {
        lastCobPrice = fundLastCob;
        logger.info("Last FT Close Price: {} on {}.", fundLastCob.getPrice_close(), lastCobDate);
        priceFound = true;
      }

      if (!priceFound) {
        logger.error("BOOM! No Close Price found for {} on {}!", fund.getSedol(), lastCobDate);
        return numOfPerformanceCalculated;
      }
    }

    //get the oldest fund price
    List<FundHistoryPrice> inceptionPrice = fundHistoryPricesRepository.getOldestPrice(fund.getSedol(),
        fund.getIsin(), fund.getFtSymbol());
    Double inceptionClosePrice;
    String inceptionCobDate = "";
    for (FundHistoryPrice fundInceptionCob : inceptionPrice) {
      inceptionClosePrice = fundInceptionCob.getPrice_close();
      inceptionCobDate = fundInceptionCob.getCobLocalDateString();
      logger.info("Inception Close Price: {} on {}.", inceptionClosePrice, inceptionCobDate);
    }

    //check if performance data has already been calculated.
    List<FundPerformance> fundPerformances = fundPerformancesRepository.findFundPeformance(fund.getSedol(),
        fund.getIsin(), fund.getFtSymbol(), DateUtils.getDate(lastCobDate, DateUtils.STANDARD_FORMAT));

    if (fundPerformances.size() > 0) {
      if (overrideFundPerformance) {
        fundPerformancesRepository.update(calculateFundPerformance(fund, lastCobPrice,
            lastCobDate, inceptionCobDate));

        logger.info("Processed Sedol: {} Isin: {} ftSymbol: {}. Performance data updated for {}!",
            fund.getSedol(), fund.getIsin(), fund.getFtSymbol(), lastCobDate);
        numOfPerformanceCalculated++;

      } else {
        logger.info("Processed Sedol: {} Isin: {} ftSymbol: {}. Performance data already calculated for {}!", fund.getSedol(),
            fund.getIsin(), fund.getFtSymbol(), lastCobDate);
      }
    } else {

      fundPerformancesRepository.save(calculateFundPerformance(fund, lastCobPrice,
          lastCobDate, inceptionCobDate));

      logger.info("Processed Sedol: {} Isin: {} ftSymbol: {}. Performance data saved for {}!",
          fund.getSedol(), fund.getIsin(), fund.getFtSymbol(), lastCobDate);
      numOfPerformanceCalculated++;
    }

    return numOfPerformanceCalculated;
  }

  private FundPerformance calculateFundPerformance(FundHistoryPrice fund,
                                                   FundHistoryPrice lastCobPrice,
                                                   String lastCobDate,
                                                   String inceptionCobDate) throws ParseException {

    FundPerformance fundPerformance = new FundPerformance();
    fundPerformance.setSedol(fund.getSedol());
    fundPerformance.setIsin(fund.getIsin());
    fundPerformance.setFtSymbol(fund.getFtSymbol());
    fundPerformance.setReportName("PerformanceFromDate");
    fundPerformance.set_1D(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "1D"));
    fundPerformance.set_3D(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "3D"));
    fundPerformance.set_5D(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "5D"));
    fundPerformance.set_1W(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "1W"));
    fundPerformance.set_2W(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "2W"));
    fundPerformance.set_3W(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "3W"));
    fundPerformance.set_1M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "1M"));
    fundPerformance.set_2M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "2M"));
    fundPerformance.set_3M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "3M"));
    fundPerformance.set_4M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "4M"));
    fundPerformance.set_5M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "5M"));
    fundPerformance.set_6M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "6M"));
    fundPerformance.set_7M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "7M"));
    fundPerformance.set_8M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "8M"));
    fundPerformance.set_9M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "9M"));
    fundPerformance.set_10M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "10M"));
    fundPerformance.set_11M(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "11M"));
    fundPerformance.set_1Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "1Y"));
    fundPerformance.set_2Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "2Y"));
    fundPerformance.set_3Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "3Y"));
    fundPerformance.set_4Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "4Y"));
    fundPerformance.set_5Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "5Y"));
    fundPerformance.set_6Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "6Y"));
    fundPerformance.set_7Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "7Y"));
    fundPerformance.set_8Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "8Y"));
    fundPerformance.set_9Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "9Y"));
    fundPerformance.set_10Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "10Y"));
    fundPerformance.set_11Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "11Y"));
    fundPerformance.set_12Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "12Y"));
    fundPerformance.set_13Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "13Y"));
    fundPerformance.set_14Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "14Y"));
    fundPerformance.set_15Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "15Y"));
    fundPerformance.set_16Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "16Y"));
    fundPerformance.set_17Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "17Y"));
    fundPerformance.set_18Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "18Y"));
    fundPerformance.set_19Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "19Y"));
    fundPerformance.set_20Y(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, "20Y"));
    fundPerformance.set_ALL(calculatePerformanceBetweenTwoDates(lastCobPrice, lastCobDate, inceptionCobDate));
    fundPerformance.setCobDate(lastCobDate);
    fundPerformance.setKey();

    return fundPerformance;
  }

  private double calculatePerformanceBetweenTwoDates(FundHistoryPrice fundLastCob,
                                                     String lastCobDate, String dateDiff)
      throws ParseException {

    String date;
    String tenor;

    if (dateDiff.length() == 10) {
      date = dateDiff;
      tenor = "ALL";
    } else {
      date = DateUtils.addToDate(lastCobDate, DateUtils.STANDARD_FORMAT, "-" + dateDiff);
      tenor = dateDiff;
    }

    List<FundHistoryPrice> price = fundHistoryPricesRepository.getFundPriceByDate(fundLastCob.getSedol(),
        fundLastCob.getIsin(), fundLastCob.getFtSymbol(), date);

    Double closePrice;
    String cobDate;

    Double priceDiff;
    Double priceDiffInPercent = 0.0;

    for (FundHistoryPrice fund : price) {
      closePrice = fund.getPrice_close();
      cobDate = fund.getCobLocalDateString();

      priceDiff = fundLastCob.getPrice_close() - closePrice;
      priceDiffInPercent = MathUtils.round((priceDiff / closePrice) * 100.0, 1);
      logger.debug("{} {} {} vs {} {} -> {}%", tenor, closePrice, cobDate, fundLastCob.getPrice_close(),
          fundLastCob.getCobLocalDateString(), priceDiffInPercent);
    }

    return priceDiffInPercent;
  }

  @Override
  public AggregationResults<FundPerformance> getSedolByTenorPerformance(String tenor, double performance) {

    Aggregation aggFund = newAggregation(
        match(Criteria.where(tenor).lte(performance)),
        group("sedol").count().as("total"),
        project("total").and("sedol").previousOperation(),
        sort(Sort.Direction.DESC, "sedol")
    );

    return mongoTemplate.aggregate(aggFund, FundPerformance.class, FundPerformance.class);
  }


  @Override
  public int updateKey() {

    int numOfRecordsUpdated = 0;

    Query query = new Query();
    query.with(new Sort(Sort.Direction.DESC, "sedol"));
    //query.addCriteria(Criteria.where("key").is(null));

    List<FundPerformance> fundPerformances = mongoTemplate.find(query, FundPerformance.class);

    for (FundPerformance fundPerformance : fundPerformances) {
      fundPerformance.setKey();

      Query fundQuery = new Query();
      fundQuery.addCriteria(Criteria.where("sedol").is(fundPerformance.getSedol()));
      fundQuery.addCriteria(Criteria.where("cobDate").is(fundPerformance.getCobDate()));

      Update update = new Update();
      update.set("key", fundPerformance.getKey());

      WriteResult result = mongoTemplate.updateFirst(fundQuery, update, FundPerformance.class);

      if (result != null) {
        numOfRecordsUpdated += result.getN();
      } else {
        logger.error("Sedol {} key not updated.", fundPerformance.getSedol());
      }
    }

    return numOfRecordsUpdated;
  }

  @Override
  public int update(FundPerformance fundPerformance) {

    Query query = new Query(Criteria.where("key").is(fundPerformance.getKey()));
    Update update = new Update();
    update.set("_1D", fundPerformance.get_1D());
    update.set("_1M", fundPerformance.get_1M());
    update.set("_1W", fundPerformance.get_1W());
    update.set("_1Y", fundPerformance.get_1Y());
    update.set("_2M", fundPerformance.get_2M());
    update.set("_2W", fundPerformance.get_2W());
    update.set("_2Y", fundPerformance.get_2Y());
    update.set("_3D", fundPerformance.get_3D());
    update.set("_3M", fundPerformance.get_3M());
    update.set("_3Y", fundPerformance.get_3Y());
    update.set("_4M", fundPerformance.get_4M());
    update.set("_4Y", fundPerformance.get_4Y());
    update.set("_5D", fundPerformance.get_5D());
    update.set("_5M", fundPerformance.get_5M());
    update.set("_5Y", fundPerformance.get_5Y());
    update.set("_6M", fundPerformance.get_6M());
    update.set("_6Y", fundPerformance.get_6Y());
    update.set("_7M", fundPerformance.get_7M());
    update.set("_7Y", fundPerformance.get_7Y());
    update.set("_8M", fundPerformance.get_8M());
    update.set("_8Y", fundPerformance.get_8Y());
    update.set("_9M", fundPerformance.get_9M());
    update.set("_9Y", fundPerformance.get_9Y());
    update.set("_10M", fundPerformance.get_10M());
    update.set("_10Y", fundPerformance.get_10Y());
    update.set("_11M", fundPerformance.get_11M());
    update.set("_11Y", fundPerformance.get_11Y());
    update.set("_12Y", fundPerformance.get_12Y());
    update.set("_13Y", fundPerformance.get_13Y());
    update.set("_14Y", fundPerformance.get_14Y());
    update.set("_15Y", fundPerformance.get_15Y());
    update.set("_16Y", fundPerformance.get_16Y());
    update.set("_17Y", fundPerformance.get_17Y());
    update.set("_18Y", fundPerformance.get_18Y());
    update.set("_19Y", fundPerformance.get_19Y());
    update.set("_20Y", fundPerformance.get_20Y());
    update.set("_ALL", fundPerformance.get_ALL());

    WriteResult result = mongoTemplate.updateFirst(query, update, FundPerformance.class);

    if (result != null) {
      return result.getN();
    } else {
      return 0;
    }
  }
}
