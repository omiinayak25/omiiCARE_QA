package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Generic modal dialog component (Page Component layer). Reusable for any confirmation/entry modal.
 */
public final class DialogComponent extends BaseComponent {

    public DialogComponent(Page page) {
        super(page, page.locator("[role=\"dialog\"], .modal, dialog").first());
    }

    public boolean isOpen() {
        return root.count() > 0 && root.isVisible();
    }

    /** Clicks a button (by accessible name) within the dialog. */
    public void clickButton(String name) {
        root.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(name)).first().click();
    }
}
