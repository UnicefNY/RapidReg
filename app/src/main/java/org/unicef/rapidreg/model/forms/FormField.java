
package org.unicef.rapidreg.model.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class FormField {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("editable")
    @Expose
    private Boolean editable;
    @SerializedName("multi_select")
    @Expose
    private Boolean multiSelect;
    @SerializedName("display_name")
    @Expose
    private Map<String, String> displayName;
    @SerializedName("help_text")
    @Expose
    private Map<String, String> helpText;
    @SerializedName("option_strings_text")
    @Expose
    private Map<String, List<SelectOption>> optionStringsText;
    private Object value;

    public FormField() {
    }

    public FormField(String name, String type, Boolean editable, Boolean multiSelect, Map<String, String> displayName, Map<String, String> helpText, Map<String, List<SelectOption>> optionStringsText, Object value) {
        this.name = name;
        this.type = type;
        this.editable = editable;
        this.multiSelect = multiSelect;
        this.displayName = displayName;
        this.helpText = helpText;
        this.optionStringsText = optionStringsText;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(Boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public Map<String, String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Map<String, String> displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getHelpText() {
        return helpText;
    }

    public void setHelpText(Map<String, String> helpText) {
        this.helpText = helpText;
    }

    public Map<String, List<SelectOption>> getOptionStringsText() {
        return optionStringsText;
    }

    public void setOptionStringsText(Map<String, List<SelectOption>> optionStringsText) {
        this.optionStringsText = optionStringsText;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
