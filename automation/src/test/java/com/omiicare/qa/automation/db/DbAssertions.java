package com.omiicare.qa.automation.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fluent, AssertJ-backed assertions for database state, built on top of {@link JdbcRunner} and
 * {@link QueryBuilder}.
 *
 * <p>These helpers express common data-integrity checks used across healthcare test suites — does a
 * row exist, how many rows match, and does a particular column hold the expected value — without
 * forcing every test to hand-write JDBC plumbing. All methods execute against a live database, so
 * any test invoking them must be tagged to be excluded from the default build.
 *
 * <p>Example:
 *
 * <pre>{@code
 * DbAssertions db = DbAssertions.using(JdbcRunner.fromConfig());
 * db.assertRowCount("patient", "voided = ?", 1, List.of(0));
 * db.assertExists("person", "uuid = ?", List.of(uuid));
 * db.assertColumnValue("person", "gender", "uuid = ?", List.of(uuid), "F");
 * }</pre>
 */
public final class DbAssertions {

    private static final Logger LOG = LoggerFactory.getLogger(DbAssertions.class);

    private final JdbcRunner runner;

    private DbAssertions(JdbcRunner runner) {
        this.runner = Objects.requireNonNull(runner, "runner must not be null");
    }

    /**
     * Creates an assertions facade over the given runner.
     *
     * @param runner the JDBC runner (required)
     * @return a new facade
     */
    public static DbAssertions using(JdbcRunner runner) {
        return new DbAssertions(runner);
    }

    /**
     * Asserts that {@code table} contains exactly {@code expected} rows matching {@code where}.
     *
     * @param table the table name
     * @param where the predicate fragment with {@code ?} placeholders (may be {@code null}/blank for all rows)
     * @param expected the expected row count
     * @param params the ordered bind parameters for {@code where}
     * @throws SQLException if a database error occurs
     */
    public void assertRowCount(String table, String where, long expected, List<Object> params)
            throws SQLException {
        long actual = rowCount(table, where, params);
        LOG.debug("assertRowCount table={} where='{}' expected={} actual={}", table, where, expected, actual);
        assertThat(actual)
                .as("row count of %s where [%s] %s", table, where, params)
                .isEqualTo(expected);
    }

    /** Asserts that the entire {@code table} contains exactly {@code expected} rows. */
    public void assertRowCount(String table, long expected) throws SQLException {
        assertRowCount(table, null, expected, List.of());
    }

    /**
     * Asserts that at least one row in {@code table} matches {@code where}.
     *
     * @param table the table name
     * @param where the predicate fragment with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @throws SQLException if a database error occurs
     */
    public void assertExists(String table, String where, List<Object> params) throws SQLException {
        long actual = rowCount(table, where, params);
        LOG.debug("assertExists table={} where='{}' actual={}", table, where, actual);
        assertThat(actual)
                .as("expected at least one row in %s where [%s] %s", table, where, params)
                .isGreaterThan(0);
    }

    /**
     * Asserts that no row in {@code table} matches {@code where}.
     *
     * @param table the table name
     * @param where the predicate fragment with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @throws SQLException if a database error occurs
     */
    public void assertNotExists(String table, String where, List<Object> params) throws SQLException {
        long actual = rowCount(table, where, params);
        LOG.debug("assertNotExists table={} where='{}' actual={}", table, where, actual);
        assertThat(actual)
                .as("expected no rows in %s where [%s] %s", table, where, params)
                .isZero();
    }

    /**
     * Asserts that the {@code column} of the first row matching {@code where} equals {@code expected}.
     *
     * @param table the table name
     * @param column the column to read
     * @param where the predicate fragment with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @param expected the expected value (compared with {@code equals}; {@code null} allowed)
     * @throws SQLException if a database error occurs
     */
    public void assertColumnValue(
            String table, String column, String where, List<Object> params, Object expected)
            throws SQLException {
        Object actual = columnValue(table, column, where, params);
        LOG.debug(
                "assertColumnValue table={} column={} where='{}' expected={} actual={}",
                table,
                column,
                where,
                expected,
                actual);
        assertThat(actual)
                .as("value of %s.%s where [%s] %s", table, column, where, params)
                .isEqualTo(expected);
    }

    /**
     * Returns the number of rows in {@code table} matching the optional {@code where} predicate.
     *
     * @param table the table name
     * @param where the predicate fragment with {@code ?} placeholders (nullable/blank for all rows)
     * @param params the ordered bind parameters
     * @return the matching row count
     * @throws SQLException if a database error occurs
     */
    public long rowCount(String table, String where, List<Object> params) throws SQLException {
        QueryBuilder qb = QueryBuilder.selectCount().from(table);
        if (where != null && !where.isBlank()) {
            qb.where(where, toArray(params));
        }
        return runner.count(qb.toSql(), qb.parameters());
    }

    /**
     * Reads a single {@code column} from the first row matching {@code where}.
     *
     * @param table the table name
     * @param column the column to read
     * @param where the predicate fragment with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @return the column value, or {@code null} if no matching row
     * @throws SQLException if a database error occurs
     */
    public Object columnValue(String table, String column, String where, List<Object> params)
            throws SQLException {
        QueryBuilder qb = QueryBuilder.select(column).from(table);
        if (where != null && !where.isBlank()) {
            qb.where(where, toArray(params));
        }
        qb.limit(1);
        List<Map<String, Object>> rows = runner.query(qb.toSql(), qb.parameters());
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> first = rows.get(0);
        // Return the single projected column value regardless of how the driver labels it.
        return first.values().iterator().next();
    }

    private static Object[] toArray(List<Object> params) {
        return params == null ? new Object[0] : params.toArray();
    }
}
