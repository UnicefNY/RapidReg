package org.unicef.rapidreg.model.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CaseFormRoot {

    @SerializedName("Children")
    @Expose
    private List<CaseFormSection> caseFormSections = new ArrayList<CaseFormSection>();

    public List<CaseFormSection> getCaseFormSections() {
        return caseFormSections;
    }

    public void setCaseFormSections(List<CaseFormSection> caseFormSections) {
        this.caseFormSections = caseFormSections;
    }

}
