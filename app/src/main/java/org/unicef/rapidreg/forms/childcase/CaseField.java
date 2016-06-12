package org.unicef.rapidreg.forms.childcase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.unicef.rapidreg.widgets.dialog.BaseDialog;
import org.unicef.rapidreg.widgets.dialog.DateDialog;
import org.unicef.rapidreg.widgets.dialog.MultipleSelectDialog;
import org.unicef.rapidreg.widgets.dialog.MultipleTextDialog;
import org.unicef.rapidreg.widgets.dialog.NumericDialog;
import org.unicef.rapidreg.widgets.dialog.SingleSelectDialog;
import org.unicef.rapidreg.widgets.dialog.SingleTextDialog;

import java.util.List;
import java.util.Map;

public class CaseField {
    public static final String TYPE_SEPARATOR = "separator";
    public static final String TYPE_TICK_BOX = "form_tick_box";
    public static final String TYPE_TEXT_FIELD = "form_text_field";
    public static final String TYPE_SELECT_BOX = "select_box";
    public static final String TYPE_SINGLE_SELECT_BOX = "single_select_box";
    public static final String TYPE_MULTI_SELECT_BOX = "multi_select_box";

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("editable")
    @Expose
    private boolean editable;
    @SerializedName("multi_select")
    @Expose
    private boolean multiSelect;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("display_name")
    @Expose
    private Map<String, String> displayName;
    @SerializedName("help_text")
    @Expose
    private Map<String, String> helpText;
    @SerializedName("option_strings_text")
    @Expose
    private Map<String, List> optionStringsText;
    @SerializedName("subform")
    @Expose
    private CaseSection subForm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Map<String, List> getOptionStringsText() {
        return optionStringsText;
    }

    public void setOptionStringsText(Map<String, List> optionStringsText) {
        this.optionStringsText = optionStringsText;
    }

    public CaseSection getSubForm() {
        return subForm;
    }

    public void setSubForm(CaseSection subForm) {
        this.subForm = subForm;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Field>").append("\n");
        sb.append("name: ").append(name).append("\n");
        sb.append("editable: ").append(editable).append("\n");
        sb.append("multiSelect: ").append(multiSelect).append("\n");
        sb.append("type: ").append(type).append("\n");
        sb.append("displayName: ").append(displayName).append("\n");
        sb.append("helpText: ").append(helpText).append("\n");
        sb.append("optionStringsText: ").append(optionStringsText).append("\n");
        sb.append("subForm: ").append(subForm).append("\n");

        return sb.toString();
    }

    public enum FieldType {
        TICK_BOX(null),
        NUMERIC_FIELD(NumericDialog.class),
        DATE_FIELD(DateDialog.class),
        TEXTAREA(MultipleTextDialog.class),
        TEXT_FIELD(SingleTextDialog.class),
        RADIO_BUTTON(SingleSelectDialog.class),
        SINGLE_SELECT_BOX(SingleSelectDialog.class),
        MULTI_SELECT_BOX(MultipleSelectDialog.class);

        private Class<? extends BaseDialog> clz;

        FieldType(Class<? extends BaseDialog> clz) {
            this.clz = clz;
        }

        public Class<? extends BaseDialog> getClz() {
            return clz;
        }
    }
}
