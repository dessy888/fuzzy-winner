package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.fund.model.FundMapping;
import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrustMapping;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InvestmentTrustMappingsRepository extends MongoRepository<InvestmentTrustMapping, Long>, InvestmentTrustMappingsRepositoryCustom {

  @Query("{sedol: '?0'}")
  InvestmentTrustMapping findInvestmentTrustMappingBySedol(String sedol);

  @Query(value = "{type: 'INVESTMENTTRUST'}", fields = "{sedol: 1, isin: 1, ftSymbol: 1, inceptionDate: 1, type: 1}")
  List<InvestmentTrustMapping> findAllDistinct(Sort sort);
}
