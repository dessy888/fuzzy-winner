package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundMapping;
import inc.deszo.fuzzywinner.fund.model.FundTopHolding;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundTopHoldingsRepository extends
    MongoRepository<FundTopHolding, Long>, FundTopHoldingsRepositoryCustom {

  @Query(value = "{type: 'FUND'}", fields = "{sedol: 1, updated: 1, type: 1}")
  List<FundTopHolding> findAllDistinct(Sort sort);
}
