package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.Fund;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
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

    @Query("{sedol: '?0', updated: ?1}")
    Fund findFundBySedolUpdated(String sedol, Date updated);

    @Query(value="{}", fields="{sedol : 1}")
    List<Fund> findSedolOnly(Sort sort);
}
