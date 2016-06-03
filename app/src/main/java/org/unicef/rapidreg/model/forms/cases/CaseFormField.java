
package org.unicef.rapidreg.model.forms.cases;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class CaseFormField {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("visible")
    @Expose
    private Boolean visible;
    @SerializedName("hide_on_view_page")
    @Expose
    private Boolean hideOnViewPage;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("highlight_information")
    @Expose
    private HashMap<String, String> highlightInformation;
    @SerializedName("editable")
    @Expose
    private Boolean editable;
    @SerializedName("multi_select")
    @Expose
    private Boolean multiSelect;
    @SerializedName("hidden_text_field")
    @Expose
    private Boolean hiddenTextField;
    @SerializedName("option_strings_source")
    @Expose
    private Object optionStringsSource;
    @SerializedName("base_language")
    @Expose
    private String baseLanguage;
    @SerializedName("subform_section_id")
    @Expose
    private Object subformSectionId;
    @SerializedName("autosum_total")
    @Expose
    private Boolean autosumTotal;
    @SerializedName("autosum_group")
    @Expose
    private String autosumGroup;
    @SerializedName("selected_value")
    @Expose
    private Object selectedValue;
    @SerializedName("create_property")
    @Expose
    private Boolean createProperty;
    @SerializedName("searchable_select")
    @Expose
    private Object searchableSelect;
    @SerializedName("link_to_path")
    @Expose
    private Object linkToPath;
    @SerializedName("field_tags")
    @Expose
    private Object fieldTags;
    @SerializedName("custom_template")
    @Expose
    private Object customTemplate;
    @SerializedName("expose_unique_id")
    @Expose
    private Object exposeUniqueId;
    @SerializedName("subform_sort_by")
    @Expose
    private Object subformSortBy;
    @SerializedName("required")
    @Expose
    private Object required;
    @SerializedName("display_name")
    @Expose
    private HashMap<String, String> displayName;
    @SerializedName("help_text")
    @Expose
    private HashMap<String, String> helpText;
    @SerializedName("option_strings_text")
    @Expose
    private HashMap<String, ArrayList> optionStringsText;
    @SerializedName("guiding_questions")
    @Expose
    private HashMap<String, String> guidingQuestions;
    @SerializedName("tally")
    @Expose
    private HashMap<String, String> tally;
    @SerializedName("tick_box_label")
    @Expose
    private HashMap<String, String> tickBoxLabel;
    @SerializedName("subform")
    @Expose
    private CaseFormSection subForm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVisible() {
        return visible;
    }


    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getHideOnViewPage() {
        return hideOnViewPage;
    }

    public void setHideOnViewPage(Boolean hideOnViewPage) {
        this.hideOnViewPage = hideOnViewPage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, String> getHighlightInformation() {
        return highlightInformation;
    }

    public void setHighlightInformation(HashMap<String, String> highlightInformation) {
        this.highlightInformation = highlightInformation;
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

    public Boolean getHiddenTextField() {
        return hiddenTextField;
    }

    public void setHiddenTextField(Boolean hiddenTextField) {
        this.hiddenTextField = hiddenTextField;
    }

    public Object getOptionStringsSource() {
        return optionStringsSource;
    }

    public void setOptionStringsSource(Object optionStringsSource) {
        this.optionStringsSource = optionStringsSource;
    }

    public String getBaseLanguage() {
        return baseLanguage;
    }

    public void setBaseLanguage(String baseLanguage) {
        this.baseLanguage = baseLanguage;
    }

    public Object getSubformSectionId() {
        return subformSectionId;
    }

    public void setSubformSectionId(Object subformSectionId) {
        this.subformSectionId = subformSectionId;
    }

    public Boolean getAutosumTotal() {
        return autosumTotal;
    }

    public void setAutosumTotal(Boolean autosumTotal) {
        this.autosumTotal = autosumTotal;
    }

    public String getAutosumGroup() {
        return autosumGroup;
    }

    public void setAutosumGroup(String autosumGroup) {
        this.autosumGroup = autosumGroup;
    }

    public Object getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(Object selectedValue) {
        this.selectedValue = selectedValue;
    }

    public Boolean getCreateProperty() {
        return createProperty;
    }

    public void setCreateProperty(Boolean createProperty) {
        this.createProperty = createProperty;
    }

    public Object getSearchableSelect() {
        return searchableSelect;
    }

    public void setSearchableSelect(Object searchableSelect) {
        this.searchableSelect = searchableSelect;
    }

    public Object getLinkToPath() {
        return linkToPath;
    }

    public void setLinkToPath(Object linkToPath) {
        this.linkToPath = linkToPath;
    }

    public Object getFieldTags() {
        return fieldTags;
    }

    public void setFieldTags(Object fieldTags) {
        this.fieldTags = fieldTags;
    }

    public Object getCustomTemplate() {
        return customTemplate;
    }

    public void setCustomTemplate(Object customTemplate) {
        this.customTemplate = customTemplate;
    }

    public Object getExposeUniqueId() {
        return exposeUniqueId;
    }

    public void setExposeUniqueId(Object exposeUniqueId) {
        this.exposeUniqueId = exposeUniqueId;
    }

    public Object getSubformSortBy() {
        return subformSortBy;
    }

    public void setSubformSortBy(Object subformSortBy) {
        this.subformSortBy = subformSortBy;
    }

    public Object getRequired() {
        return required;
    }

    public void setRequired(Object required) {
        this.required = required;
    }

    public HashMap<String, String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(HashMap<String, String> displayName) {
        this.displayName = displayName;
    }

    public HashMap<String, String> getHelpText() {
        return helpText;
    }

    public void setHelpText(HashMap<String, String> helpText) {
        this.helpText = helpText;
    }

    public HashMap<String, ArrayList> getOptionStringsText() {
        return optionStringsText;
    }

    public void setOptionStringsText(HashMap<String, ArrayList> optionStringsText) {
        this.optionStringsText = optionStringsText;
    }

    public HashMap<String, String> getGuidingQuestions() {
        return guidingQuestions;
    }

    public void setGuidingQuestions(HashMap<String, String> guidingQuestions) {
        this.guidingQuestions = guidingQuestions;
    }

    public HashMap<String, String> getTally() {
        return tally;
    }

    public void setTally(HashMap<String, String> tally) {
        this.tally = tally;
    }

    public HashMap<String, String> getTickBoxLabel() {
        return tickBoxLabel;
    }

    public void setTickBoxLabel(HashMap<String, String> tickBoxLabel) {
        this.tickBoxLabel = tickBoxLabel;
    }

    public CaseFormSection getSubForm() {
        return subForm;
    }

    public void setSubForm(CaseFormSection subForm) {
        this.subForm = subForm;
    }
}
