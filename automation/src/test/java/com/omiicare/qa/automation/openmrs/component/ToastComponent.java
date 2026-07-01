package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Toast / inline-alert component (Page Component layer). OpenMRS surfaces confirmations and errors
 * via toastmessage and Bootstrap alerts; this centralises reading them.
 */
public final class ToastComponent extends BaseComponent {

    private static final String ALERTS = ".toast-message, .alert, [role=\"alert\"]";

    public ToastComponent(Page page) {
        super(page);
    }

    /** True if any visible toast/alert contains the given text. */
    public boolean hasMessageContaining(String text) {
        return within(ALERTS).filter(new Locator.FilterOptions().setHasText(text)).count() > 0;
    }

    /** Text of the first toast/alert, or empty string if none. */
    public String firstMessage() {
        Locator first = within(ALERTS).first();
        if (first.count() == 0) {
            return "";
        }
        String t = first.textContent();
        return t == null ? "" : t.strip();
    }
}
