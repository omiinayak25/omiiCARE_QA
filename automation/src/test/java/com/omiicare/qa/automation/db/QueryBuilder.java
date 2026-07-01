package com.omiicare.qa.automation.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Pure-logic, fluent SQL builder for the database testing framework.
 *
 * <p>This class performs <strong>no</strong> database access. It assembles parameterized SQL
 * strings (using {@code ?} placeholders) and collects the ordered bind parameters so that callers
 * can hand them to a {@link JdbcRunner}. Keeping construction free of side effects makes the builder
 * fully unit-testable without a live database.
 *
 * <p>Identifiers (table/column names) are validated against a conservative whitelist pattern to
 * reduce the risk of SQL injection through identifier interpolation, while all <em>values</em> are
 * always bound as parameters and never concatenated into the SQL text.
 *
 * <p>Typical usage:
 *
 * <pre>{@code
 * QueryBuilder qb = QueryBuilder.select("id", "name")
 *         .from("patient")
 *         .whereEquals("voided", 0)
 *         .orderBy("name")
 *         .limit(10);
 * String sql = qb.toSql();           // SELECT id, name FROM patient WHERE voided = ? ORDER BY name ASC LIMIT 10
 * List<Object> params = qb.parameters();
 * }</pre>
 */
public final class QueryBuilder {

    /** Sort direction for {@code ORDER BY} clauses. */
    public enum Direction {
        ASC,
        DESC
    }

    /**
     * Conservative identifier validation: letters, digits and underscore, optionally with a single
     * schema/table qualifier (e.g. {@code public.patient} or {@code p.name}). Wildcards ({@code *})
     * are allowed only as a standalone projection.
     */
    private static final String IDENTIFIER_REGEX = "[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?";

    private final List<String> columns = new ArrayList<>();
    private String table;
    private final List<String> whereClauses = new ArrayList<>();
    private final List<Object> parameters = new ArrayList<>();
    private final List<String> orderBy = new ArrayList<>();
    private Integer limit;
    private Integer offset;

    private QueryBuilder() {}

    /**
     * Begins a {@code SELECT} statement.
     *
     * @param columns the projection; when empty, {@code *} is used
     * @return a new builder
     */
    public static QueryBuilder select(String... columns) {
        QueryBuilder qb = new QueryBuilder();
        if (columns == null || columns.length == 0) {
            qb.columns.add("*");
        } else {
            for (String c : columns) {
                qb.columns.add(validateProjection(c));
            }
        }
        return qb;
    }

    /** Convenience factory for {@code SELECT COUNT(*)} statements. */
    public static QueryBuilder selectCount() {
        QueryBuilder qb = new QueryBuilder();
        qb.columns.add("COUNT(*)");
        return qb;
    }

    /**
     * Sets the source table.
     *
     * @param table a valid SQL identifier
     * @return this builder
     */
    public QueryBuilder from(String table) {
        this.table = validateIdentifier(table);
        return this;
    }

    /**
     * Adds an equality predicate, binding {@code value} as a parameter.
     *
     * @param column a valid SQL identifier
     * @param value the bound value (may be {@code null} for {@code IS NULL})
     * @return this builder
     */
    public QueryBuilder whereEquals(String column, Object value) {
        String id = validateIdentifier(column);
        if (value == null) {
            whereClauses.add(id + " IS NULL");
        } else {
            whereClauses.add(id + " = ?");
            parameters.add(value);
        }
        return this;
    }

    /**
     * Adds an arbitrary predicate fragment with positional placeholders.
     *
     * @param clause the SQL fragment containing zero or more {@code ?} placeholders
     * @param values the values bound to the placeholders in order
     * @return this builder
     * @throws IllegalArgumentException if the placeholder count does not match {@code values}
     */
    public QueryBuilder where(String clause, Object... values) {
        Objects.requireNonNull(clause, "clause must not be null");
        String trimmed = clause.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("where clause must not be blank");
        }
        int placeholders = countPlaceholders(trimmed);
        int provided = values == null ? 0 : values.length;
        if (placeholders != provided) {
            throw new IllegalArgumentException(
                    "Placeholder count ("
                            + placeholders
                            + ") does not match the number of values ("
                            + provided
                            + ")");
        }
        whereClauses.add(trimmed);
        if (values != null) {
            Collections.addAll(parameters, values);
        }
        return this;
    }

    /** Adds an ascending {@code ORDER BY} term. */
    public QueryBuilder orderBy(String column) {
        return orderBy(column, Direction.ASC);
    }

    /** Adds an {@code ORDER BY} term with an explicit direction. */
    public QueryBuilder orderBy(String column, Direction direction) {
        String id = validateIdentifier(column);
        Direction dir = direction == null ? Direction.ASC : direction;
        orderBy.add(id + " " + dir.name());
        return this;
    }

    /**
     * Sets a row limit.
     *
     * @param limit a non-negative limit
     * @return this builder
     * @throws IllegalArgumentException if {@code limit} is negative
     */
    public QueryBuilder limit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be >= 0");
        }
        this.limit = limit;
        return this;
    }

    /**
     * Sets a row offset.
     *
     * @param offset a non-negative offset
     * @return this builder
     * @throws IllegalArgumentException if {@code offset} is negative
     */
    public QueryBuilder offset(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
        this.offset = offset;
        return this;
    }

    /**
     * Renders the assembled SQL string.
     *
     * @return the parameterized SQL
     * @throws IllegalStateException if no table has been set
     */
    public String toSql() {
        if (table == null || table.isBlank()) {
            throw new IllegalStateException("from(table) must be called before toSql()");
        }
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(String.join(", ", columns));
        sb.append(" FROM ").append(table);
        if (!whereClauses.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", whereClauses));
        }
        if (!orderBy.isEmpty()) {
            sb.append(" ORDER BY ").append(String.join(", ", orderBy));
        }
        if (limit != null) {
            sb.append(" LIMIT ").append(limit);
        }
        if (offset != null) {
            sb.append(" OFFSET ").append(offset);
        }
        return sb.toString();
    }

    /**
     * Returns an immutable snapshot of the ordered bind parameters.
     *
     * @return the parameters, in placeholder order
     */
    public List<Object> parameters() {
        return Collections.unmodifiableList(new ArrayList<>(parameters));
    }

    /** Returns a {@code COUNT(*)} variant of the current query (same table and predicates). */
    public QueryBuilder asCount() {
        QueryBuilder qb = new QueryBuilder();
        qb.columns.add("COUNT(*)");
        qb.table = this.table;
        qb.whereClauses.addAll(this.whereClauses);
        qb.parameters.addAll(this.parameters);
        return qb;
    }

    /**
     * Builds a named-parameter description map for diagnostics/logging. The keys are positional
     * ({@code p1}, {@code p2}, ...). This is purely informational and not used for binding.
     *
     * @return an ordered map of positional name to value
     */
    public Map<String, Object> describeParameters() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < parameters.size(); i++) {
            map.put("p" + (i + 1), parameters.get(i));
        }
        return map;
    }

    @Override
    public String toString() {
        return toSql() + " :: " + describeParameters();
    }

    private static int countPlaceholders(String clause) {
        int count = 0;
        for (int i = 0; i < clause.length(); i++) {
            if (clause.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }

    private static String validateProjection(String column) {
        Objects.requireNonNull(column, "column must not be null");
        String c = column.trim();
        if (c.equals("*")) {
            return c;
        }
        return validateIdentifier(c);
    }

    /**
     * Validates a SQL identifier against the conservative whitelist.
     *
     * @param identifier candidate identifier
     * @return the trimmed identifier
     * @throws IllegalArgumentException if invalid
     */
    static String validateIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        String id = identifier.trim();
        if (!id.matches(IDENTIFIER_REGEX)) {
            throw new IllegalArgumentException("Invalid SQL identifier: '" + identifier + "'");
        }
        return id;
    }
}
