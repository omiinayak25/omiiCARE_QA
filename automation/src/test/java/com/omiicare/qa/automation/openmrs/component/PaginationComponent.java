package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Page;

/**
 * Generic pagination component (Page Component layer). Reusable for any paged list/table.
 */
public final class PaginationComponent extends BaseComponent {

    public PaginationComponent(Page page, String rootSelector) {
        super(page, page.locator(rootSelector).first());
    }

    public boolean isPresent() {
        return root.count() > 0;
    }

    public void next() {
        within("a[rel=\"next\"], .next, button:has-text(\"Next\")").first().click();
    }

    public void previous() {
        within("a[rel=\"prev\"], .prev, button:has-text(\"Previous\")").first().click();
    }
}
