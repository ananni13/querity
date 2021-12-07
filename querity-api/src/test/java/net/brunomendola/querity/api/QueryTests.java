package net.brunomendola.querity.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.brunomendola.querity.api.Operator.EQUALS;
import static net.brunomendola.querity.api.Querity.*;
import static org.assertj.core.api.Assertions.assertThat;

class QueryTests {
  @Test
  void givenNoFilter_whenHasFilter_thenReturnsFalse() {
    Query query = Querity.query().build();
    assertThat(query.hasFilter()).isFalse();
  }

  @Test
  void givenFilterWithEmptyCondition_whenHasFilter_thenReturnsFalse() {
    Query query = Querity.query().filter(and()).build();
    assertThat(query.hasFilter()).isFalse();
  }

  @Test
  void givenFilter_whenHasFilter_thenReturnsTrue() {
    Query query = Querity.query().filter(filterBy("lastName", EQUALS, "Skywalker")).build();
    assertThat(query.hasFilter()).isTrue();
  }

  @Test
  void givenNoPagination_whenHasPagination_thenReturnsFalse() {
    Query query = Querity.query().build();
    assertThat(query.hasPagination()).isFalse();
  }

  @Test
  void givenPagination_whenHasPagination_thenReturnsTrue() {
    Query query = Querity.query().pagination(1, 20).build();
    assertThat(query.hasPagination()).isTrue();
  }

  @Test
  void givenPagination2_whenHasPagination_thenReturnsTrue() {
    Query query = Querity.query().pagination(paged(1, 20)).build();
    assertThat(query.hasPagination()).isTrue();
  }

  @Test
  void givenNoSort_whenHasSort_thenReturnsFalse() {
    Query query = Querity.query().build();
    assertThat(query.hasSort()).isFalse();
  }

  @Test
  void givenSort_whenHasSort_thenReturnsTrue() {
    Query query = Querity.query().sort(sortBy("lastName")).build();
    assertThat(query.hasSort()).isTrue();
  }

  @Test
  void givenSort_whenGetSort_thenReturnsListContainingTheSort() {
    Sort sort = sortBy("lastName");
    Query query = Querity.query().sort(sort).build();
    assertThat(query.getSort()).hasSize(1);
    assertThat(query.getSort()).contains(sort);
  }

  @Test
  void givenPreprocessor_whenPreprocess_thenCallPreprocessors() {
    QueryPreprocessor preprocessor = Mockito.spy(new DummyQueryPreprocessor());
    Query query = Querity.query().withPreprocessor(preprocessor).build();
    query.preprocess();

    Mockito.verify(preprocessor).preprocess(query);
  }

  static class DummyQueryPreprocessor implements QueryPreprocessor {
    @Override
    public Query preprocess(Query query) {
      return query;
    }
  }
}
