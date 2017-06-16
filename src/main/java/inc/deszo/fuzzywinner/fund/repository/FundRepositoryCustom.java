package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.Fund;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.Date;
import java.util.List;

public interface FundRepositoryCustom {

  List<String> getDistinctSedol();

  AggregationResults<Fund> getFundWithYieldMoreThan(double yield);

  AggregationResults<Fund> getPlusFundWithYieldMoreThan(double yield);

  List<Date> getDistinctUpdated();

  List<Fund> getLastUpdated(String sedol);

  int updateKey();
}
