package inc.deszo.fuzzywinner.repository.fund;

import inc.deszo.fuzzywinner.model.fund.FundHistoryPrices;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FundHistoryPricesRepository extends MongoRepository<FundHistoryPrices, Long>, FundHistoryPricesRepositoryCustom {


}
