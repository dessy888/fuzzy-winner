package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface InvestmentTrustsRepository extends MongoRepository<InvestmentTrust, Long>, InvestmentTrustsRepositoryCustom {

  @Query("{key: '?0'}")
  InvestmentTrust findInvestmentTrustByKey(String key);
}
