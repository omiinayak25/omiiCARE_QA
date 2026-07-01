package com.omiicare.qa.automation.db;

import com.omiicare.qa.automation.core.config.FrameworkConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thin JDBC execution helper for the database testing framework.
 *
 * <p>{@code JdbcRunner} opens connections from a JDBC URL resolved through {@link FrameworkConfig}
 * (nothing hardcoded) and exposes small, focused primitives for tests: parameterized queries that
 * return rows as ordered maps, scalar/count helpers, and parameterized updates. Each call opens and
 * closes its own connection via try-with-resources, which keeps tests independent and avoids leaked
 * handles. For suites needing a shared session, {@link #withConnection(ConnectionWork)} exposes a
 * single managed {@link Connection}.
 *
 * <p>Configuration keys (system property / env var / framework.properties):
 *
 * <ul>
 *   <li>{@code db.url} ({@code DB_URL}) — JDBC URL, e.g. {@code jdbc:postgresql://localhost:5432/openmrs}
 *   <li>{@code db.username} ({@code DB_USERNAME}) — optional
 *   <li>{@code db.password} ({@code DB_PASSWORD}) — optional
 *   <li>{@code db.driver} ({@code DB_DRIVER}) — optional explicit driver class to {@code Class.forName}
 * </ul>
 *
 * <p>This class touches a live database only when its methods are invoked; merely constructing it is
 * side-effect free, so it is safe to reference from untagged code. Tests that actually call a method
 * here must be tagged to exclude them from the default build.
 */
public final class JdbcRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcRunner.class);

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String driverClass;

    /** Functional hook for executing arbitrary work against a managed connection. */
    @FunctionalInterface
    public interface ConnectionWork<T> {
        T apply(Connection connection) throws SQLException;
    }

    private JdbcRunner(String jdbcUrl, String username, String password, String driverClass) {
        this.jdbcUrl = Objects.requireNonNull(jdbcUrl, "jdbcUrl must not be null");
        this.username = username;
        this.password = password;
        this.driverClass = driverClass;
    }

    /**
     * Builds a runner from {@link FrameworkConfig}.
     *
     * @return a configured runner
     * @throws IllegalStateException if {@code db.url} is not configured
     */
    public static JdbcRunner fromConfig() {
        FrameworkConfig cfg = FrameworkConfig.get();
        String url = cfg.get("db.url", "");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                    "JDBC URL not configured. Set 'db.url' (or env DB_URL) before using JdbcRunner.");
        }
        String user = emptyToNull(cfg.get("db.username", ""));
        String pass = emptyToNull(cfg.get("db.password", ""));
        String driver = emptyToNull(cfg.get("db.driver", ""));
        return new JdbcRunner(url, user, pass, driver);
    }

    /**
     * Builds a runner from explicit values, bypassing configuration. Useful for embedded/in-memory
     * databases in higher layers.
     *
     * @param jdbcUrl JDBC URL (required)
     * @param username username (nullable)
     * @param password password (nullable)
     * @return a configured runner
     */
    public static JdbcRunner forUrl(String jdbcUrl, String username, String password) {
        return new JdbcRunner(jdbcUrl, username, password, null);
    }

    /** Returns the JDBC URL this runner targets. */
    public String jdbcUrl() {
        return jdbcUrl;
    }

    /**
     * Opens a new JDBC connection. Callers are responsible for closing it.
     *
     * @return an open connection
     * @throws SQLException if the connection cannot be established
     */
    public Connection openConnection() throws SQLException {
        loadDriverIfConfigured();
        LOG.debug("Opening JDBC connection to {}", jdbcUrl);
        if (username != null) {
            return DriverManager.getConnection(jdbcUrl, username, password);
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    /**
     * Executes work against a single managed connection, closing it afterward.
     *
     * @param work the work to perform
     * @param <T> result type
     * @return the work result
     * @throws SQLException if a database error occurs
     */
    public <T> T withConnection(ConnectionWork<T> work) throws SQLException {
        Objects.requireNonNull(work, "work must not be null");
        try (Connection conn = openConnection()) {
            return work.apply(conn);
        }
    }

    /**
     * Runs a parameterized query and materializes every row as an ordered column→value map.
     *
     * @param sql the SQL with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @return the rows (possibly empty), never {@code null}
     * @throws SQLException if a database error occurs
     */
    public List<Map<String, Object>> query(String sql, List<Object> params) throws SQLException {
        Objects.requireNonNull(sql, "sql must not be null");
        return withConnection(
                conn -> {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        bind(ps, params);
                        try (ResultSet rs = ps.executeQuery()) {
                            return readRows(rs);
                        }
                    }
                });
    }

    /** Convenience overload accepting varargs parameters. */
    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        return query(sql, params == null ? List.of() : List.of(params));
    }

    /**
     * Runs a query expected to return a single scalar in the first column of the first row.
     *
     * @param sql the SQL with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @return the scalar value, or {@code null} if no rows
     * @throws SQLException if a database error occurs
     */
    public Object queryScalar(String sql, List<Object> params) throws SQLException {
        return withConnection(
                conn -> {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        bind(ps, params);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                return rs.getObject(1);
                            }
                            return null;
                        }
                    }
                });
    }

    /**
     * Returns the row count for a {@code COUNT(*)}-style query (or any query whose first scalar is a
     * number).
     *
     * @param sql the SQL with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @return the count as a {@code long}
     * @throws SQLException if a database error occurs
     */
    public long count(String sql, List<Object> params) throws SQLException {
        Object scalar = queryScalar(sql, params);
        if (scalar == null) {
            return 0L;
        }
        if (scalar instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(scalar.toString());
    }

    /**
     * Executes a parameterized DML statement.
     *
     * @param sql the SQL with {@code ?} placeholders
     * @param params the ordered bind parameters
     * @return the affected row count
     * @throws SQLException if a database error occurs
     */
    public int update(String sql, List<Object> params) throws SQLException {
        Objects.requireNonNull(sql, "sql must not be null");
        return withConnection(
                conn -> {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        bind(ps, params);
                        return ps.executeUpdate();
                    }
                });
    }

    private void loadDriverIfConfigured() {
        if (driverClass != null && !driverClass.isBlank()) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                        "Configured JDBC driver class not found: " + driverClass, e);
            }
        }
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private static List<Map<String, Object>> readRows(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int c = 1; c <= columnCount; c++) {
                String label = meta.getColumnLabel(c);
                row.put(label, rs.getObject(c));
            }
            rows.add(Collections.unmodifiableMap(row));
        }
        return rows;
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
