package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.Fund;
import inc.deszo.fuzzywinner.model.fund.FundPerformance;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface FundPerformanceRepository extends MongoRepository<FundPerformance, Long>, FundPerformanceRepositoryCustom {

    @Query("{sedol: '?0', isin: '?1', ftSymbol: '?2', cobDate: ?3}")
    List<FundPerformance> findFundPeformance(String sedol, String isin, String ftSymbol, Date cobDate);
}
