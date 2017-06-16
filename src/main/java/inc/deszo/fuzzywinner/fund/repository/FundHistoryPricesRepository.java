package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrices;
import inc.deszo.fuzzywinner.fund.model.FundInfos;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundHistoryPricesRepository extends MongoRepository<FundHistoryPrices, Long>, FundHistoryPricesRepositoryCustom {

  @Query(value = "{}", fields = "{sedol: 1, isin: 1, ftSymbol: 1, inceptionDate: 1}")
  List<FundInfos> findAllDistinct(Sort sort);
}
