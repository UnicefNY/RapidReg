package org.unicef.rapidreg.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cfzhang on 12/8/16.
 */

public class IncidentTemplateForm implements RecordForm {

    @SerializedName("Incident")
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
        StringBuilder sb = new StringBuilder("<Incident>").append("\n");
        for (Section section : sections) {
            sb.append(section).append("\n");
        }
        return sb.toString();
    }

}
