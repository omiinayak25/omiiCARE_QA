package com.omiicare.qa.shared.error;

/**
 * Thrown when a documented healthcare business rule is violated (see docs/BUSINESS_RULES.md). Maps
 * to HTTP 422 Unprocessable Entity.
 */
public class BusinessRuleException extends ApiException {

    private final transient String ruleId;

    public BusinessRuleException(String ruleId, String message) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, message);
        this.ruleId = ruleId;
    }

    public String ruleId() {
        return ruleId;
    }
}
