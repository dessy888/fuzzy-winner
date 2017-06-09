package inc.deszo.fuzzywinner.repository;

import inc.deszo.fuzzywinner.model.Domain;
import inc.deszo.fuzzywinner.model.Fund;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundRepository extends MongoRepository<Fund, Long>, FundRepositoryCustom {

    Fund findFirstBySedol(String sedol);

    //Mongo JSON query string
    @Query("{sedol: '?0' }")
    Fund findFundBySedol(String sedol);

    @Query("{yield: { $gt: ?0 }}")
    List<Fund> findFundsByYield(double yield, Sort sort);

    @Query("{yield: { $gt: ?0 }, plusFund: {$ne : \"false\"}}")
    List<Fund> findPlusFundsByYield(double yield, Sort sort);

    @Query("{sedol: '?0', updated: '?1'}")
    Fund updatedFund(String sedol, String updated);

    @Query(value="{}", fields="{sedol : 1}")
    List<Fund> findSedolAndExclueAll(Sort sort);
}
