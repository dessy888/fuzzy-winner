package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.FundMapping;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundMappingsRepository extends MongoRepository<FundMapping, Long>, FundMappingsRepositoryCustom {

  FundMapping findFirstBySedol(String sedol);

  @Query("{sedol: '?0', isin: '?1'}")
  FundMapping findISIN(String sedol, String isin);

  @Query(value = "{type: 'FUND'}", fields = "{sedol: 1, isin: 1, ftSymbol: 1, inceptionDate: 1, plusFund: 1, type: 1}")
  List<FundMapping> findAllDistinct(Sort sort);

  @Query("{type: {$eq : 'FUND'}, plusFund: {$ne : 'false'}}")
  List<FundMapping> findPlusFunds(Sort sort);
}
