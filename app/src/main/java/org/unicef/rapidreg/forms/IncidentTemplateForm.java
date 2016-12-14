package org.unicef.rapidreg.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class IncidentTemplateForm implements RecordForm {

    @SerializedName("Incidents")
    @Expose
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Incidents>").append("\n");
        for (Section section : sections) {
            sb.append(section).append("\n");
        }
        return sb.toString();
    }

}
