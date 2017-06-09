package inc.deszo.fuzzywinner.repository;

import inc.deszo.fuzzywinner.model.Fund;
import inc.deszo.fuzzywinner.model.FundInfos;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundInfosRepository extends MongoRepository<FundInfos, Long>, FundInfosRepositoryCustom {

    FundInfos findFirstBySedol(String sedol);

    @Query("{sedol: '?0', isin: '?1'}")
    FundInfos findISIN(String sedol, String isin);
}
