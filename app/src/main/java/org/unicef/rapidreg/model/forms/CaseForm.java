package org.unicef.rapidreg.model.forms;

import java.util.List;

public class CaseForm {

    private List<FormSection> formSections;

    public CaseForm() {
    }

    public CaseForm(List<FormSection> formSections) {
        this.formSections = formSections;
    }

    public List<FormSection> getFormSections() {
        return formSections;
    }

    public void setFormSections(List<FormSection> formSections) {
        this.formSections = formSections;
    }
}
