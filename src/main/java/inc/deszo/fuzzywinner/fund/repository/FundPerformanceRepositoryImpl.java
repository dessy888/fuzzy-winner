package inc.deszo.fuzzywinner.fund.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.fund.model.Fund;
import inc.deszo.fuzzywinner.fund.model.FundHistoryPrices;
import inc.deszo.fuzzywinner.fund.model.FundInfos;
import inc.deszo.fuzzywinner.fund.model.FundPerformance;
import inc.deszo.fuzzywinner.utils.DateUtils;
import inc.deszo.fuzzywinner.utils.MathUtils;
import inc.deszo.fuzzywinner.utils.MongoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static inc.deszo.fuzzywinner.utils.CsvUtils.csvWriter;

public class FundPerformanceRepositoryImpl implements FundPerformanceRepositoryCustom {

  private static final Logger logger = LoggerFactory.getLogger(FundPerformanceRepositoryImpl.class);

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  private FundHistoryPricesRepository fundHistoryPricesRepository;

  @Autowired
  private FundPerformanceRepository fundPerformanceRepository;

  @Autowired
  private FundInfosRepository fundInfosRepository;

  @Autowired
  private FundRepository fundRepository;

  @Override
  public void calculate(LocalDate cobDate, boolean plusFundOnly) throws ParseException {

    //Update fund keys
    //logger.info("Number of funds key updated: {}.", fundPerformanceRepository.updateKey());

    List<FundHistoryPrices> fundHistoryPrices = fundHistoryPricesRepository.getDistinctSedol();
    int fundCount = 0;
    int numOfPerformanceCalculated = 0;

    for (FundHistoryPrices fund : fundHistoryPrices) {

      //process only plusFund
      if (plusFundOnly) {
        FundInfos fundInfos = fundInfosRepository.findFirstBySedol(fund.getSedol());
        if (fundInfos.getPlusFund().equalsIgnoreCase("false")) {
          continue;
        }
      }

      fundCount++;
      logger.info("Fund {}, Sedol {}.", fundCount, fund.getSedol());

      logger.info("Processing Sedol: {} Isin: {} ftSymbol: {}.", fund.getSedol(),
          fund.getIsin(), fund.getFtSymbol());

      //get the last cob fund price
      List<FundHistoryPrices> lastFTCobPrice = fundHistoryPricesRepository.getLastUpdated(fund.getSedol(),
          fund.getIsin(), fund.getFtSymbol());
      Double lastFTClosePrice;
      String lastFTCobDate = "";
      for (FundHistoryPrices fundLastCob : lastFTCobPrice) {
        lastFTClosePrice = fundLastCob.getPrice_close();
        lastFTCobDate = fundLastCob.getCobLocalDateString();
        logger.info("Last FT Close Price: {} on {}.", lastFTClosePrice, lastFTCobDate);
      }

      //get the last updated fund price from HL
      List<Fund> lastHLCobPrice = fundRepository.getLastUpdated(fund.getSedol());
      Double lastHLClosePrice;
      String lastHLCobDate = "";
      for (Fund fundLastCob : lastHLCobPrice) {
        lastHLClosePrice = fundLastCob.getPrice_sell();
        lastHLCobDate = fundLastCob.getUpdatedLocalDateString();
        logger.info("Last HL Close Price: {} on {}.", lastHLClosePrice, lastHLCobDate);
      }

      // if FT cob date is > than HL cob date, use HL.
      String lastCobDate = "";
      FundHistoryPrices lastCobPrice;
      if (DateUtils.diffBetTwoDates(lastHLCobDate, lastFTCobDate, DateUtils.STANDARD_FORMAT) < 0) {

        //note there is a price format difference between HL and FT so get the closing price from FT.
        List<FundHistoryPrices> lastFTCobPriceUsingHLCobDate = fundHistoryPricesRepository.getFundPriceByDate(fund.getSedol(),
            fund.getIsin(), fund.getFtSymbol(), lastHLCobDate);
        for (FundHistoryPrices fundLastCob : lastFTCobPriceUsingHLCobDate) {
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

      //check if performance data has already been calculated.
      List<FundPerformance> fundPerformances = fundPerformanceRepository.findFundPeformance(fund.getSedol(),
          fund.getIsin(), fund.getFtSymbol(), DateUtils.getDate(lastCobDate, DateUtils.STANDARD_FORMAT));

      if (fundPerformances.size() > 0) {
        logger.info("Processed Sedol: {} Isin: {} ftSymbol: {}. Performance data already calculated for {}!", fund.getSedol(),
            fund.getIsin(), fund.getFtSymbol(), lastCobDate);
      } else {
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

        fundPerformanceRepository.save(fundPerformance);
        logger.info("Processed Sedol: {} Isin: {} ftSymbol: {}. Performance data saved for {}!",
            fund.getSedol(), fund.getIsin(), fund.getFtSymbol(), lastCobDate);
        numOfPerformanceCalculated++;
      }
    }
    logger.info("*****Number of Fund Performance Calculated: {}", numOfPerformanceCalculated);
  }

  private double calculatePerformanceBetweenTwoDates(FundHistoryPrices fundLastCob,
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
      logger.info("{} {} {} vs {} {} -> {}%", tenor, closePrice, cobDate, fundLastCob.getPrice_close(),
          fundLastCob.getCobLocalDateString(), priceDiffInPercent);
    }

    return priceDiffInPercent;
  }

  @Override
  public void genCsvFundReport() throws IOException {

    // performance report
    String jsFundPerforamnce = MongoUtils.getJSFile("C:/Users/deszo/IdeaProjects/fuzzy-winner/mongodb/queries/Fund-LookupFundPerformance.js");
    BasicDBObject obj = new BasicDBObject();
    obj.append("$eval", jsFundPerforamnce);

    ObjectMapper mapper = new ObjectMapper();
    CsvMapper csvMapper = new CsvMapper();

    CommandResult commandResult = mongoTemplate.executeCommand(obj);
    final JsonNode arrNode = mapper.readTree(commandResult.toJson()).get("retval").get("_batch");

    if (arrNode.isArray()) {

      List<LinkedHashMap<String, String>> myArrList = new ArrayList<>();

      for (final JsonNode objNode : arrNode) {
        LinkedHashMap<String, String> map;
        map = mapper.readValue(objNode.toString(), new TypeReference<LinkedHashMap<String, String>>() {
        });
        myArrList.add(map);
      }

      String pathname = "C:/Users/deszo/IdeaProjects/fuzzy-winner/reports/csvFundReport_" +
          DateUtils.getTodayDate("MM_dd_yyyy") + ".csv";
      File file = new File(pathname);

      // Create a File and append if it already exists.
      Writer writer = new FileWriter(file, false);

      //Copy List of Map Object into CSV format at specified File location.
      csvWriter(myArrList, writer);

      logger.info("CSV file generated: {}", pathname);
    }
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
}
