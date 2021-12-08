package net.brunomendola.querity.spring.data.jpa;

import lombok.experimental.Delegate;
import net.brunomendola.querity.api.LogicConditionsWrapper;
import net.brunomendola.querity.api.LogicOperator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

abstract class JpaLogicConditionsWrapper extends JpaCondition {
  @Delegate
  private final LogicConditionsWrapper conditionsWrapper;

  protected JpaLogicConditionsWrapper(LogicConditionsWrapper conditionsWrapper) {
    this.conditionsWrapper = conditionsWrapper;
  }

  @Override
  public <T> Predicate toPredicate(Class<T> entityClass, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return getLogicPredicate(getConditionPredicates(entityClass, root, cq, cb), cb);
  }

  private <T> Predicate[] getConditionPredicates(Class<T> entityClass, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return getConditions().stream()
        .map(JpaCondition::of)
        .map(c -> c.toPredicate(entityClass, root, cq, cb))
        .toArray(Predicate[]::new);
  }

  private Predicate getLogicPredicate(Predicate[] conditionPredicates, CriteriaBuilder cb) {
    return getLogic().equals(LogicOperator.AND) ?
        cb.and(conditionPredicates) :
        cb.or(conditionPredicates);
  }
}
