package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundPerformance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface FundPerformancesRepository extends MongoRepository<FundPerformance, Long>, FundPerformancesRepositoryCustom {

  @Query("{sedol: '?0', isin: '?1', ftSymbol: '?2', cobDate: ?3}")
  List<FundPerformance> findFundPeformance(String sedol, String isin, String ftSymbol, Date cobDate);
}
