package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.fund.model.FundMapping;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundHistoryPricesRepository extends
    MongoRepository<FundHistoryPrice, Long>, FundHistoryPricesRepositoryCustom {

  @Query(value = "{type: 'FUND'}", fields = "{sedol: 1, isin: 1, ftSymbol: 1, inceptionDate: 1, type: 1}")
  List<FundMapping> findAllDistinct(Sort sort);

  int deleteFundHistoryPricesBySedol(String sedol);
}
