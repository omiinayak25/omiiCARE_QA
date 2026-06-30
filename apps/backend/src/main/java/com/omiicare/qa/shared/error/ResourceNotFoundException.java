package com.omiicare.qa.shared.error;

/** Thrown when a requested entity does not exist (or is not visible to the tenant). */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String resource, Object id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, resource + " not found: " + id);
    }
}
