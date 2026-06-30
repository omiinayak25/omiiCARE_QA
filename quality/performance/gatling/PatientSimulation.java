// =============================================================================
// omiiCARE QA — Gatling Simulation (Java DSL): Login -> List Patients
// -----------------------------------------------------------------------------
// PERFORMANCE SAFETY RULE: Targets ONLY local / Docker / owned infrastructure
// (default http://localhost:8080 via the BASE_URL system property). NEVER run
// against a public website or any system you do not own.
// -----------------------------------------------------------------------------
// Build/run with Gatling (Maven or the Gatling bundle). Example (bundle):
//   ./gatling.sh -s omiicare.PatientSimulation
// With Maven plugin:
//   mvn gatling:test -Dgatling.simulationClass=omiicare.PatientSimulation \
//       -DBASE_URL=http://localhost:8080
//
// Requires Gatling 3.9+ (Java DSL).
// =============================================================================

package omiicare;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;

public class PatientSimulation extends Simulation {

  // Configuration via system properties (override on the command line with -D).
  private static final String BASE_URL =
      System.getProperty("BASE_URL", "http://localhost:8080");
  private static final String USERNAME =
      System.getProperty("USERNAME", "demo.admin");
  private static final String PASSWORD =
      System.getProperty("PASSWORD", "Admin@12345");

  // HTTP protocol configuration — shared connection settings + base URL.
  private final HttpProtocolBuilder httpProtocol =
      http.baseUrl(BASE_URL)
          .acceptHeader("application/json")
          .contentTypeHeader("application/json")
          .userAgentHeader("omiiCARE-Gatling/1.0");

  // Scenario: authenticate, capture the JWT, then list patients with it.
  private final ScenarioBuilder scn =
      scenario("Login then list patients")
          .exec(
              http("Login")
                  .post("/api/v1/auth/login")
                  .body(
                      StringBody(
                          "{\"username\":\"" + USERNAME
                              + "\",\"password\":\"" + PASSWORD + "\"}"))
                  .check(status().is(200))
                  // Capture either {"token":...} or {"accessToken":...}.
                  .check(
                      jsonPath("$.token")
                          .ofString()
                          .optional()
                          .saveAs("jwt"))
                  .check(
                      jsonPath("$.accessToken")
                          .ofString()
                          .optional()
                          .saveAs("jwtAlt")))
          .exitHereIfFailed()
          .pause(Duration.ofMillis(500), Duration.ofSeconds(2))
          .exec(
              http("List patients")
                  .get("/api/v1/patients?page=0&size=20")
                  .header(
                      "Authorization",
                      "Bearer #{jwt.exists().booleanValue() ? jwt : jwtAlt}")
                  .check(status().is(200)));

  {
    // Closed-model injection: ramp concurrent users, then sustain.
    setUp(
            scn.injectClosed(
                rampConcurrentUsers(0).to(25).during(Duration.ofSeconds(60)),
                constantConcurrentUsers(25).during(Duration.ofSeconds(120))))
        .protocols(httpProtocol)
        .assertions(
            // SLA gates — comparable to the k6 thresholds.
            global().responseTime().percentile(95).lt(500),
            global().responseTime().percentile(99).lt(1000),
            global().failedRequests().percent().lt(1.0));
  }
}
