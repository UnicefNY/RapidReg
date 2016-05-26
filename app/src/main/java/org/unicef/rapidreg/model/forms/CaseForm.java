package org.unicef.rapidreg.model.forms;

import java.util.List;

public class CaseForm {

    private List<FormSection> formsections;

    public CaseForm() {
    }

    public CaseForm(List<FormSection> formsections) {
        this.formsections = formsections;
    }

    public List<FormSection> getFormsections() {
        return formsections;
    }

    public void setFormsections(List<FormSection> formsections) {
        this.formsections = formsections;
    }
}
