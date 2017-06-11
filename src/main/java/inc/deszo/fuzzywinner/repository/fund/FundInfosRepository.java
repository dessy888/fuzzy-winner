package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundInfos;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundInfosRepository extends MongoRepository<FundInfos, Long>, FundInfosRepositoryCustom {

    FundInfos findFirstBySedol(String sedol);

    @Query("{sedol: '?0', isin: '?1'}")
    FundInfos findISIN(String sedol, String isin);

    @Query(value = "{}", fields = "{sedol: 1, isin: 1, ftSymbol: 1, inceptionDate: 1, plusFund: 1}")
    List<FundInfos> findAllDistinct(Sort sort);

    @Query("{plusFund: {$ne : 'false'}}")
    List<FundInfos> findPlusFunds(Sort sort);
}
