package com.omiicare.qa.automation.api.rest;

/**
 * Immutable projection of the OpenMRS {@code GET /ws/rest/v1/session} response. Captures whether
 * the supplied credentials authenticated and, when they did, the authenticated user's identity and
 * the currently selected session location.
 *
 * @param authenticated whether the server reported an authenticated session
 * @param userUuid the authenticated user's UUID, or {@code null} when not authenticated
 * @param username the authenticated user's username, or {@code null} when not authenticated
 * @param sessionLocationUuid the UUID of the selected session location, or {@code null} when none
 */
public record SessionInfo(
        boolean authenticated, String userUuid, String username, String sessionLocationUuid) {

    /** An unauthenticated session sentinel. */
    public static SessionInfo anonymous() {
        return new SessionInfo(false, null, null, null);
    }
}
