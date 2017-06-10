package inc.deszo.fuzzywinner.repository;

import com.mongodb.WriteResult;
import inc.deszo.fuzzywinner.model.Domain;
import inc.deszo.fuzzywinner.model.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

public class FundRepositoryImpl implements FundRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public int updateFund(String sedol, String isin, String name, String unitType, String loaded, String company,
                          String sector, String plusFund, double price_sell, double price_buy, double price_change,
                          double yield, double initialCharge, double annualCharge, double annualSaving,
                          double netAnnualCharge, String discountedCode, String perf12m, String perf12t24m,
                          String perf24t36m, String perf36t48m, String perf48t60m, double fundSize,
                          String incomeFrequency, String paymentType, int numHoldings, Date updated) {

        Query query = new Query(Criteria.where("sedol").is(sedol));
        Update update = new Update();
        update.set("name", name);
        update.set("isin", isin);
        update.set("name", name);
        update.set("unitType", unitType);
        update.set("loaded", loaded);
        update.set("company", company);
        update.set("sector", sector);
        update.set("plusFund", plusFund);
        update.set("price_sell", price_sell);
        update.set("price_buy", price_buy);
        update.set("price_change", price_change);
        update.set("yield", yield);
        update.set("initialCharge", initialCharge);
        update.set("annualCharge", annualCharge);
        update.set("annualSaving", annualSaving);
        update.set("netAnnualCharge", netAnnualCharge);
        update.set("discountedCode", discountedCode);
        update.set("perf12m", perf12m);
        update.set("perf12t24m", perf12t24m);
        update.set("perf24t36m", perf24t36m);
        update.set("perf36t48m", perf36t48m);
        update.set("perf48t60m", perf48t60m);
        update.set("fundSize", fundSize);
        update.set("incomeFrequency", incomeFrequency);
        update.set("paymentType", paymentType);
        update.set("numHoldings", numHoldings);
        update.set("updated", updated);

        WriteResult result = mongoTemplate.updateFirst(query, update, Fund.class);

        if(result!=null)
            return result.getN();
        else
            return 0;
    }

    @Override
    public List<String> getDistinctSedol() {

        return mongoTemplate.getCollection("fund").distinct("sedol");
    }

    @Override
    public AggregationResults<Fund> getFundWithYieldMoreThan (double yield) {
        Aggregation aggFund = newAggregation(
                match(Criteria.where("yield").gt(yield)), sort(Sort.Direction.DESC, "yield").and(Sort.Direction.ASC, "sedol")
        );

        return mongoTemplate.aggregate(aggFund, Fund.class, Fund.class);
    }

    @Override
    public AggregationResults<Fund> getPlusFundWithYieldMoreThan (double yield) {
        Aggregation aggPlusFund = newAggregation(
                match(Criteria.where("yield").gt(yield)), sort(Sort.Direction.DESC, "yield").and(Sort.Direction.ASC, "sedol")
        );

        return mongoTemplate.aggregate(aggPlusFund, Fund.class, Fund.class);
    }

    @Override
    public List<String> getDistinctUpdated() {

        return mongoTemplate.getCollection("fund").distinct("updated");
    }
}
