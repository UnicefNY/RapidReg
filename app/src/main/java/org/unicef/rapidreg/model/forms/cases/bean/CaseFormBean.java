package org.unicef.rapidreg.model.forms.cases.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaseFormBean {
    @SerializedName("Children")
    private List<CaseSectionBean> caseSections;

    public List<CaseSectionBean> getCaseSections() {
        return caseSections;
    }

    public void setCaseSections(List<CaseSectionBean> caseSections) {
        this.caseSections = caseSections;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Children>").append("\n");
        for (CaseSectionBean section : caseSections) {
            sb.append(section).append("\n");
        }
        return sb.toString();
    }
}
