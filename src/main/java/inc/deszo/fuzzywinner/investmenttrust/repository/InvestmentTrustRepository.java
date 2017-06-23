package inc.deszo.fuzzywinner.investmenttrust.repository;

import inc.deszo.fuzzywinner.investmenttrust.model.InvestmentTrust;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InvestmentTrustRepository extends MongoRepository<InvestmentTrust, Long>, InvestmentTrustRepositoryCustom {

  @Query("{key: '?0'}")
  InvestmentTrust findInvestmentTrustByKey(String key);
}
