package org.unicef.rapidreg.model.forms.cases;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CaseFormBean {
    @SerializedName("Children")
    private List<CaseSectionBean> sections = new ArrayList<>();

    public List<CaseSectionBean> getSections() {
        return sections;
    }

    public void setSections(List<CaseSectionBean> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Children>").append("\n");
        for (CaseSectionBean section : sections) {
            sb.append(section).append("\n");
        }
        return sb.toString();
    }
}
