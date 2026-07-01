package com.omiicare.qa.automation.openmrs.workflow;

import com.microsoft.playwright.Page;
import com.omiicare.qa.automation.core.config.FrameworkConfig;

/**
 * Abstract base for business workflows (Business Workflow layer). A workflow orchestrates page
 * objects, components and services into a complete business process. Business logic belongs here —
 * NOT in tests and NOT in page objects. Workflows expose intention-revealing methods that read like
 * the domain (login, registerPatient, findPatient, ...).
 */
public abstract class BaseWorkflow {

    /** Config key for the OpenMRS UI context-root URL. */
    public static final String BASE_URL_KEY = "omii.openmrs.ui.baseUrl";

    /** Default OpenMRS Reference Application context root (the public demo). */
    public static final String DEFAULT_BASE_URL = "https://o2.openmrs.org/openmrs";

    protected final Page page;
    protected final String baseUrl;

    protected BaseWorkflow(Page page) {
        if (page == null) {
            throw new IllegalArgumentException("page must not be null");
        }
        this.page = page;
        this.baseUrl = FrameworkConfig.get().get(BASE_URL_KEY, DEFAULT_BASE_URL);
    }

    /** @return the Playwright page this workflow drives. */
    public Page page() {
        return page;
    }
}
