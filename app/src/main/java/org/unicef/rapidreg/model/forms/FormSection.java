package org.unicef.rapidreg.model.forms;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormSection {

    @SerializedName("name") private Map<String, String> name = new HashMap<String, String>();
    private int order;
    private boolean enabled;
    @SerializedName("help_text")
    private Map<String, String> helpText = new HashMap<String, String>();
    @SerializedName("fields") private List<FormField> fields = new ArrayList<FormField>();
    @SerializedName("base_language") private String baseLanguage;

    public FormSection() {
    }

    public FormSection(Map<String, String> name, int order, boolean enabled, Map<String, String> helpText, List<FormField> fields, String baseLanguage) {
        this.name = name;
        this.order = order;
        this.enabled = enabled;
        this.helpText = helpText;
        this.fields = fields;
        this.baseLanguage = baseLanguage;
    }

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getHelpText() {
        return helpText;
    }

    public void setHelpText(Map<String, String> helpText) {
        this.helpText = helpText;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    public String getBaseLanguage() {
        return baseLanguage;
    }

    public void setBaseLanguage(String baseLanguage) {
        this.baseLanguage = baseLanguage;
    }

    @Override
    public String toString() {
        return "FormSection{" +
                "name=" + name +
                ", order=" + order +
                ", enabled=" + enabled +
                ", helpText=" + helpText +
                ", fields=" + fields +
                ", baseLanguage='" + baseLanguage + '\'' +
                '}';
    }
}
