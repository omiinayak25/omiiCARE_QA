package com.omiicare.qa.automation.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pure-logic unit tests for {@link QueryBuilder}.
 *
 * <p>These tests are deterministic, require no database or SUT, and are intentionally left
 * <strong>untagged</strong> so they run as part of the default {@code mvn test} build. They exercise
 * SQL rendering, parameter ordering, identifier validation, and guard rails only — no JDBC, no
 * network.
 */
class QueryBuilderTest {

    @Test
    @DisplayName("select with columns renders projection and table")
    void rendersBasicSelect() {
        QueryBuilder qb = QueryBuilder.select("id", "name").from("patient");
        assertThat(qb.toSql()).isEqualTo("SELECT id, name FROM patient");
        assertThat(qb.parameters()).isEmpty();
    }

    @Test
    @DisplayName("select with no columns defaults to star")
    void defaultsToStar() {
        QueryBuilder qb = QueryBuilder.select().from("person");
        assertThat(qb.toSql()).isEqualTo("SELECT * FROM person");
    }

    @Test
    @DisplayName("selectCount renders COUNT(*)")
    void rendersCount() {
        QueryBuilder qb = QueryBuilder.selectCount().from("person");
        assertThat(qb.toSql()).isEqualTo("SELECT COUNT(*) FROM person");
    }

    @Test
    @DisplayName("whereEquals binds a parameter and uses a placeholder")
    void whereEqualsBindsParameter() {
        QueryBuilder qb = QueryBuilder.select("id").from("patient").whereEquals("voided", 0);
        assertThat(qb.toSql()).isEqualTo("SELECT id FROM patient WHERE voided = ?");
        assertThat(qb.parameters()).containsExactly(0);
    }

    @Test
    @DisplayName("whereEquals with null renders IS NULL and binds nothing")
    void whereEqualsNullRendersIsNull() {
        QueryBuilder qb = QueryBuilder.select("id").from("patient").whereEquals("date_voided", null);
        assertThat(qb.toSql()).isEqualTo("SELECT id FROM patient WHERE date_voided IS NULL");
        assertThat(qb.parameters()).isEmpty();
    }

    @Test
    @DisplayName("multiple predicates are joined with AND in order")
    void joinsPredicatesWithAnd() {
        QueryBuilder qb =
                QueryBuilder.select("id")
                        .from("patient")
                        .whereEquals("voided", 0)
                        .where("gender = ?", "F");
        assertThat(qb.toSql())
                .isEqualTo("SELECT id FROM patient WHERE voided = ? AND gender = ?");
        assertThat(qb.parameters()).containsExactly(0, "F");
    }

    @Test
    @DisplayName("order by, limit and offset render in the correct positions")
    void rendersOrderLimitOffset() {
        QueryBuilder qb =
                QueryBuilder.select("id")
                        .from("obs")
                        .orderBy("obs_datetime", QueryBuilder.Direction.DESC)
                        .limit(10)
                        .offset(5);
        assertThat(qb.toSql())
                .isEqualTo("SELECT id FROM obs ORDER BY obs_datetime DESC LIMIT 10 OFFSET 5");
    }

    @Test
    @DisplayName("default order direction is ascending")
    void defaultOrderAscending() {
        QueryBuilder qb = QueryBuilder.select("id").from("obs").orderBy("name");
        assertThat(qb.toSql()).isEqualTo("SELECT id FROM obs ORDER BY name ASC");
    }

    @Test
    @DisplayName("asCount preserves table and predicates but swaps projection")
    void asCountPreservesPredicates() {
        QueryBuilder base = QueryBuilder.select("id").from("patient").whereEquals("voided", 0);
        QueryBuilder count = base.asCount();
        assertThat(count.toSql()).isEqualTo("SELECT COUNT(*) FROM patient WHERE voided = ?");
        assertThat(count.parameters()).containsExactly(0);
    }

    @Test
    @DisplayName("describeParameters maps positional names to values")
    void describesParameters() {
        QueryBuilder qb =
                QueryBuilder.select("id").from("patient").whereEquals("voided", 0).where("gender = ?", "F");
        assertThat(qb.describeParameters())
                .containsEntry("p1", 0)
                .containsEntry("p2", "F")
                .hasSize(2);
    }

    @Test
    @DisplayName("toSql without a table fails fast")
    void toSqlRequiresTable() {
        assertThatThrownBy(() -> QueryBuilder.select("id").toSql())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("from");
    }

    @Test
    @DisplayName("where with mismatched placeholder count is rejected")
    void whereRejectsPlaceholderMismatch() {
        assertThatThrownBy(() -> QueryBuilder.select("id").from("t").where("a = ? AND b = ?", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Placeholder count");
    }

    @Test
    @DisplayName("invalid identifiers are rejected to limit injection surface")
    void rejectsInvalidIdentifiers() {
        assertThatThrownBy(() -> QueryBuilder.select("id").from("patient; DROP TABLE users"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid SQL identifier");
        assertThatThrownBy(() -> QueryBuilder.select("a b").from("t"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("qualified identifiers (schema.table) are accepted")
    void acceptsQualifiedIdentifiers() {
        QueryBuilder qb = QueryBuilder.select("p.id").from("public.patient");
        assertThat(qb.toSql()).isEqualTo("SELECT p.id FROM public.patient");
    }

    @Test
    @DisplayName("negative limit and offset are rejected")
    void rejectsNegativeLimitOffset() {
        assertThatThrownBy(() -> QueryBuilder.select("id").from("t").limit(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> QueryBuilder.select("id").from("t").offset(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("parameters() returns an immutable snapshot")
    void parametersAreImmutable() {
        QueryBuilder qb = QueryBuilder.select("id").from("t").whereEquals("x", 1);
        List<Object> params = qb.parameters();
        assertThatThrownBy(() -> params.add("mutation"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
