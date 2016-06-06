package org.unicef.rapidreg.forms.childcase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaseSection {
    @SerializedName("name")
    @Expose
    private Map<String, String> name;
    @SerializedName("order")
    @Expose
    private int order;
    @SerializedName("help_text")
    @Expose
    private Map<String, String> helpText;
    @SerializedName("base_language")
    @Expose
    private String baseLanguage;
    @SerializedName("fields")
    @Expose
    private List<CaseField> fields = new ArrayList<>();

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Map<String, String> getHelpText() {
        return helpText;
    }

    public void setHelpText(Map<String, String> helpText) {
        this.helpText = helpText;
    }

    public String getBaseLanguage() {
        return baseLanguage;
    }

    public void setBaseLanguage(String baseLanguage) {
        this.baseLanguage = baseLanguage;
    }

    public List<CaseField> getFields() {
        return fields;
    }

    public void setFields(List<CaseField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Section>").append("\n");
        sb.append("name: ").append(name).append("\n");
        sb.append("order: ").append(order).append("\n");
        sb.append("helpText: ").append(helpText).append("\n");
        sb.append("baseLanguage: ").append(baseLanguage).append("\n");
        for (CaseField field : fields) {
            sb.append(field);
        }

        return sb.toString();
    }
}
