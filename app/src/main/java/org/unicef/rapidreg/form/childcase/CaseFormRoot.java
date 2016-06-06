package org.unicef.rapidreg.form.childcase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CaseFormRoot {
    @SerializedName("Children")
    @Expose
    private List<CaseSection> sections = new ArrayList<>();

    public List<CaseSection> getSections() {
        return sections;
    }

    public void setSections(List<CaseSection> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Children>").append("\n");
        for (CaseSection section : sections) {
            sb.append(section).append("\n");
        }
        return sb.toString();
    }
}
