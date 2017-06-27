package inc.deszo.fuzzywinner.fund.repository;

import inc.deszo.fuzzywinner.fund.model.Fund;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface FundsRepository extends MongoRepository<Fund, Long>, FundsRepositoryCustom {

  Fund findFirstBySedol(String sedol);

  //Mongo JSON query string
  @Query("{sedol: '?0' }")
  Fund findFundBySedol(String sedol);

  @Query("{yield: { $gt: ?0 }}")
  List<Fund> findFundsByYield(double yield, Sort sort);

  @Query("{yield: { $gt: ?0 }, plusFund: {$ne : \"false\"}}")
  List<Fund> findPlusFundsByYield(double yield, Sort sort);

  @Query("{sedol: '?0', updated: ?1}")
  Fund findFundBySedolUpdated(String sedol, Date updated);

  @Query(value = "{}", fields = "{sedol : 1}")
  List<Fund> findSedolOnly(Sort sort);
}
