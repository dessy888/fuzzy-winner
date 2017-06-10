package inc.deszo.fuzzywinner.repository;

import inc.deszo.fuzzywinner.model.Fund;
import inc.deszo.fuzzywinner.model.FundHistoryPrices;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FundHistoryPricesRepository extends MongoRepository<FundHistoryPrices, Long>, FundHistoryPricesRepositoryCustom {


}
