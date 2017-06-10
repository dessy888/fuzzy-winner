package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundPerformance;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FundPerformanceRepository extends MongoRepository<FundPerformance, Long>, FundPerformanceRepositoryCustom {


}
