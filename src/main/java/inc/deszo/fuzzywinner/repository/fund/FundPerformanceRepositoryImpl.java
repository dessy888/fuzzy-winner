package inc.deszo.fuzzywinner.repository.fund;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class FundPerformanceRepositoryImpl implements FundPerformanceRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;


}
