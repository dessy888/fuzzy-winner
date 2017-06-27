package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.fund.model.FundHistoryPrice;
import inc.deszo.fuzzywinner.fund.model.FundMapping;
import inc.deszo.fuzzywinner.fund.repository.FundHistoryPricesRepositoryCustom;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrustHistoryPrice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InvestmentTrustHistoryPricesRepository extends
    MongoRepository<InvestmentTrustHistoryPrice, Long>, InvestmentTrustHistoryPricesRepositoryCustom {

  @Query(value = "{type: 'INVESTMENTTRUST'}", fields = "{sedol: 1, isin: 1, ftSymbol: 1, inceptionDate: 1, type: 1}")
  List<FundMapping> findAllDistinct(Sort sort);
}
