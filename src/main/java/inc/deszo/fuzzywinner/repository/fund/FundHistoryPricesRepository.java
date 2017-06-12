package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;
import inc.deszo.fuzzywinner.model.fund.FundInfos;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundHistoryPricesRepository extends MongoRepository<FundHistoryPrices, Long>, FundHistoryPricesRepositoryCustom {

  @Query(value = "{}", fields = "{sedol: 1, isin: 1, ftSymbol: 1, inceptionDate: 1}")
  List<FundInfos> findAllDistinct(Sort sort);
}
