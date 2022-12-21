package net.brunomendola.querity.spring.data.mongodb;

import net.brunomendola.querity.api.Querity;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
public class QuerityMongodbAutoConfiguration {
  @Bean
  public Querity querity(MongoTemplate mongoTemplate) {
    return new QuerityMongodbImpl(mongoTemplate);
  }
}
