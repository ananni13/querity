package net.brunomendola.querity.spring.data.mongodb;

import net.brunomendola.querity.api.Querity;
import net.brunomendola.querity.api.Query;
import net.brunomendola.querity.spring.data.mongodb.domain.Person;
import net.brunomendola.querity.test.QuerityGenericSpringTestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Comparator;
import java.util.List;

import static net.brunomendola.querity.api.Querity.filterByNative;
import static net.brunomendola.querity.api.Querity.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = QuerityMongodbTestApplication.class)
@Testcontainers
class QuerityMongodbImplTests extends QuerityGenericSpringTestSuite<Person, String> {

  public static final String MONGO_DB_DOCKER_IMAGE = "mongo:5.0.4";

  @Container
  private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse(MONGO_DB_DOCKER_IMAGE));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
  }

  @Override
  protected Class<Person> getEntityClass() {
    return Person.class;
  }

  /**
   * Overridden because sort with nulls last is not supported by MongoDB
   */
  @Override
  protected <C extends Comparable<? super C>> Comparator<C> getSortComparator(boolean reversed) {
    Comparator<C> comparator = Comparator.nullsFirst(Comparator.naturalOrder());
    if (reversed) comparator = comparator.reversed();
    return comparator;
  }

  @Test
  void givenMongodbNativeCondition_whenFilterAll_thenReturnOnlyFilteredElements() {
    Criteria criteria = Criteria.where("lastName").is(entity1.getLastName());
    Query query = Querity.query()
        .filter(filterByNative(criteria))
        .build();
    List<Person> result = querity.findAll(Person.class, query);
    assertThat(result)
        .isNotEmpty()
        .isEqualTo(entities.stream()
            .filter(p -> entity1.getLastName().equals(p.getLastName()))
            .toList());
  }

  @Test
  void givenNotConditionWrappingMongodbNativeCondition_whenFilterAll_thenThrowIllegalArgumentException() {
    Criteria criteria = Criteria.where("lastName").is(entity1.getLastName());
    Query query = Querity.query()
        .filter(not(filterByNative(criteria)))
        .build();
    assertThrows(IllegalArgumentException.class,
        () -> querity.findAll(Person.class, query),
        "Not conditions wrapping native conditions is not supported; just write a negative native condition.");
  }
}
