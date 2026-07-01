package com.omiicare.qa.automation.openmrs.component;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Generic form component (Page Component layer): fill inputs by name/id and click buttons by label.
 * Reused by registration and other data-entry pages.
 */
public final class FormComponent extends BaseComponent {

    public FormComponent(Page page) {
        super(page);
    }

    public FormComponent fillByName(String name, String value) {
        within("[name=\"" + name + "\"]").first().fill(value);
        return this;
    }

    public FormComponent fillById(String id, String value) {
        within("#" + id).first().fill(value);
        return this;
    }

    public FormComponent selectByName(String name, String value) {
        within("select[name=\"" + name + "\"]").first().selectOption(value);
        return this;
    }

    /** Clicks a button by its visible/accessible name. */
    public void clickButton(String name) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)).first().click();
    }
}
