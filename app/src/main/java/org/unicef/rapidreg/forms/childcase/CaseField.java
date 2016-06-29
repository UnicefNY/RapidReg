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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaseField {
    public static final String TYPE_SELECT_BOX = "select_box";
    public static final String TYPE_SINGLE_SELECT_BOX = "single_select_box";
    public static final String TYPE_SINGLE_LINE_RADIO = "single_line_radio";
    public static final String TYPE_MULTI_SELECT_BOX = "multi_select_box";
    public static final String TYPE_SUBFORM_FIELD = "subform_container";
    public static final String TYPE_TICK_BOX = "tick_box";
    public static final String TYPE_TEXT_FIELD = "text_field";
    public static final String TYPE_PHOTO_UPLOAD_LAYOUT = "photo_upload_layout";
    public static final String TYPE_AUDIO_UPLOAD_LAYOUT = "audio_item";
    public static final String TYPE_GENERIC_LAYOUT = "generic_field";
    public static final String TYPE_RADIO_BUTTON = "radio_button";
    public static final String TYPE_NUMERIC_FIELD = "numeric_field";

    private static final int INVALID_INDEX = -1;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("editable")
    @Expose
    private boolean editable;
    @SerializedName("required")
    @Expose
    private boolean required;
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
    @SerializedName("show_on_minify_form")
    @Expose
    private boolean isShowOnMiniForm;

    private String parent;

    private int index = INVALID_INDEX;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isSeparator() {
        return FieldType.SEPARATOR.name().equalsIgnoreCase(type);
    }

    public boolean isTickBox() {
        return FieldType.TICK_BOX.name().equalsIgnoreCase(type);
    }

    public boolean isShowOnMiniForm() {
        return isShowOnMiniForm;
    }

    public void setShowOnMiniForm(boolean showOnMiniForm) {
        isShowOnMiniForm = showOnMiniForm;
    }

    public boolean isPhotoUploadBox() {
        return FieldType.PHOTO_UPLOAD_BOX.name().equalsIgnoreCase(type);
    }

    public boolean isAudioUploadBox() {
        return FieldType.AUDIO_UPLOAD_BOX.name().equalsIgnoreCase(type);
    }

    public boolean isSubform() {
        return FieldType.SUBFORM.name().equalsIgnoreCase(type);
    }

    public boolean isTextField() {
        return FieldType.TEXT_FIELD.name().equalsIgnoreCase(type);
    }

    public boolean isSelectField() {
        return type.equals(TYPE_SELECT_BOX);
    }

    public boolean isRaduiButton() {
        return type.equals(TYPE_RADIO_BUTTON);
    }

    public boolean isNumericField() {
        return type.equals(TYPE_NUMERIC_FIELD);
    }

    public boolean isManyOptions() {
        if (getSelectOptions().size() > 2) {
            return true;
        }
        return false;
    }

    public List<String> getSelectOptions() {
        List<String> items = new ArrayList<>();
        if (getType().equals(CaseField.TYPE_MULTI_SELECT_BOX)) {
            List<Map<String, String>> arrayList = getOptionStringsText().get("en");
            for (Map<String, String> map : arrayList) {
                items.add(map.get("display_text"));
            }
        } else {
            items = getOptionStringsText().get("en");
        }
        return items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<Field>").append("\n");
        sb.append("name: ").append(name).append("\n");
        sb.append("editable: ").append(editable).append("\n");
        sb.append("required: ").append(required).append("\n");
        sb.append("multiSelect: ").append(multiSelect).append("\n");
        sb.append("type: ").append(type).append("\n");
        sb.append("displayName: ").append(displayName).append("\n");
        sb.append("helpText: ").append(helpText).append("\n");
        sb.append("optionStringsText: ").append(optionStringsText).append("\n");
        sb.append("subForm: ").append(subForm).append("\n");
        sb.append("show_on_minify_form: ").append(isShowOnMiniForm).append("\n");
        sb.append("parent: ").append(parent).append("\n");

        return sb.toString();
    }

    public CaseField copy() {
        CaseField newField = new CaseField();
        newField.setName(name);
        newField.setEditable(editable);
        newField.setRequired(required);
        newField.setMultiSelect(multiSelect);
        newField.setType(type);
        newField.setDisplayName(displayName);
        newField.setHelpText(helpText);
        newField.setOptionStringsText(optionStringsText);
        newField.setSubForm(subForm);
        newField.setShowOnMiniForm(isShowOnMiniForm);
        newField.setParent(parent);

        return newField;
    }

    public enum FieldType {
        SEPARATOR(null),
        TICK_BOX(null),
        NUMERIC_FIELD(NumericDialog.class),
        DATE_FIELD(DateDialog.class),
        TEXTAREA(MultipleTextDialog.class),
        TEXT_FIELD(SingleTextDialog.class),
        RADIO_BUTTON(SingleSelectDialog.class),
        SINGLE_SELECT_BOX(SingleSelectDialog.class),
        MULTI_SELECT_BOX(MultipleSelectDialog.class),
        PHOTO_UPLOAD_BOX(null),
        AUDIO_UPLOAD_BOX(null),
        SUBFORM(null);

        private Class<? extends BaseDialog> clz;

        FieldType(Class<? extends BaseDialog> clz) {
            this.clz = clz;
        }

        public Class<? extends BaseDialog> getClz() {
            return clz;
        }
    }
}
