
package org.unicef.rapidreg.model.forms.cases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CaseFormSection {

    @SerializedName("unique_id")
    @Expose
    private String uniqueId;
    @SerializedName("parent_form")
    @Expose
    private String parentForm;
    @SerializedName("visible")
    @Expose
    private Boolean visible;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("order_form_group")
    @Expose
    private Integer orderFormGroup;
    @SerializedName("order_subform")
    @Expose
    private Integer orderSubform;
    @SerializedName("form_group_keyed")
    @Expose
    private Boolean formGroupKeyed;
    @SerializedName("form_group_name")
    @Expose
    private String formGroupName;
    @SerializedName("fields")
    @Expose
    private List<CaseFormField> caseFormFields = new ArrayList<CaseFormField>();
    @SerializedName("editable")
    @Expose
    private Boolean editable;
    @SerializedName("fixed_order")
    @Expose
    private Boolean fixedOrder;
    @SerializedName("perm_visible")
    @Expose
    private Boolean permVisible;
    @SerializedName("perm_enabled")
    @Expose
    private Boolean permEnabled;
    @SerializedName("core_form")
    @Expose
    private Boolean coreForm;
    @SerializedName("validations")
    @Expose
    private List<Object> validations = new ArrayList<Object>();
    @SerializedName("base_language")
    @Expose
    private String baseLanguage;
    @SerializedName("is_nested")
    @Expose
    private Boolean isNested;
    @SerializedName("is_first_tab")
    @Expose
    private Boolean isFirstTab;
    @SerializedName("initial_subforms")
    @Expose
    private Integer initialSubforms;
    @SerializedName("collapsed_fields")
    @Expose
    private List<Object> collapsedFields = new ArrayList<Object>();
    @SerializedName("subform_header_links")
    @Expose
    private List<Object> subformHeaderLinks = new ArrayList<Object>();
    @SerializedName("display_help_text_view")
    @Expose
    private Boolean displayHelpTextView;
    @SerializedName("shared_subform")
    @Expose
    private Object sharedSubform;
    @SerializedName("shared_subform_group")
    @Expose
    private Object sharedSubformGroup;
    @SerializedName("is_summary_section")
    @Expose
    private Boolean isSummarySection;
    @SerializedName("mobile_form")
    @Expose
    private Boolean mobileForm;
    @SerializedName("name")
    @Expose
    private HashMap<String, String> name;
    @SerializedName("help_text")
    @Expose
    private HashMap<String, String> helpText;
    @SerializedName("description")
    @Expose
    private HashMap<String, String> description;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getParentForm() {
        return parentForm;
    }

    public void setParentForm(String parentForm) {
        this.parentForm = parentForm;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrderFormGroup() {
        return orderFormGroup;
    }

    public void setOrderFormGroup(Integer orderFormGroup) {
        this.orderFormGroup = orderFormGroup;
    }

    public Integer getOrderSubform() {
        return orderSubform;
    }

    public void setOrderSubform(Integer orderSubform) {
        this.orderSubform = orderSubform;
    }

    public Boolean getFormGroupKeyed() {
        return formGroupKeyed;
    }

    public void setFormGroupKeyed(Boolean formGroupKeyed) {
        this.formGroupKeyed = formGroupKeyed;
    }

    public String getFormGroupName() {
        return formGroupName;
    }

    public void setFormGroupName(String formGroupName) {
        this.formGroupName = formGroupName;
    }

    public List<CaseFormField> getCaseFormFields() {
        return caseFormFields;
    }

    public void setCaseFormFields(List<CaseFormField> caseFormFields) {
        this.caseFormFields = caseFormFields;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getFixedOrder() {
        return fixedOrder;
    }

    public void setFixedOrder(Boolean fixedOrder) {
        this.fixedOrder = fixedOrder;
    }

    public Boolean getPermVisible() {
        return permVisible;
    }

    public void setPermVisible(Boolean permVisible) {
        this.permVisible = permVisible;
    }

    public Boolean getPermEnabled() {
        return permEnabled;
    }

    public void setPermEnabled(Boolean permEnabled) {
        this.permEnabled = permEnabled;
    }

    public Boolean getCoreForm() {
        return coreForm;
    }

    public void setCoreForm(Boolean coreForm) {
        this.coreForm = coreForm;
    }

    public List<Object> getValidations() {
        return validations;
    }

    public void setValidations(List<Object> validations) {
        this.validations = validations;
    }

    public String getBaseLanguage() {
        return baseLanguage;
    }

    public void setBaseLanguage(String baseLanguage) {
        this.baseLanguage = baseLanguage;
    }

    public Boolean getIsNested() {
        return isNested;
    }

    public void setIsNested(Boolean isNested) {
        this.isNested = isNested;
    }

    public Boolean getIsFirstTab() {
        return isFirstTab;
    }

    public void setIsFirstTab(Boolean isFirstTab) {
        this.isFirstTab = isFirstTab;
    }

    public Integer getInitialSubforms() {
        return initialSubforms;
    }

    public void setInitialSubforms(Integer initialSubforms) {
        this.initialSubforms = initialSubforms;
    }

    public List<Object> getCollapsedFields() {
        return collapsedFields;
    }

    public void setCollapsedFields(List<Object> collapsedFields) {
        this.collapsedFields = collapsedFields;
    }

    public List<Object> getSubformHeaderLinks() {
        return subformHeaderLinks;
    }

    public void setSubformHeaderLinks(List<Object> subformHeaderLinks) {
        this.subformHeaderLinks = subformHeaderLinks;
    }

    public Boolean getDisplayHelpTextView() {
        return displayHelpTextView;
    }

    public void setDisplayHelpTextView(Boolean displayHelpTextView) {
        this.displayHelpTextView = displayHelpTextView;
    }

    public Object getSharedSubform() {
        return sharedSubform;
    }

    public void setSharedSubform(Object sharedSubform) {
        this.sharedSubform = sharedSubform;
    }

    public Object getSharedSubformGroup() {
        return sharedSubformGroup;
    }

    public void setSharedSubformGroup(Object sharedSubformGroup) {
        this.sharedSubformGroup = sharedSubformGroup;
    }

    public Boolean getIsSummarySection() {
        return isSummarySection;
    }

    public void setIsSummarySection(Boolean isSummarySection) {
        this.isSummarySection = isSummarySection;
    }

    public Boolean getMobileForm() {
        return mobileForm;
    }

    public void setMobileForm(Boolean mobileForm) {
        this.mobileForm = mobileForm;
    }

    public HashMap<String, String> getName() {
        return name;
    }

    public void setName(HashMap<String, String> name) {
        this.name = name;
    }

    public HashMap<String, String> getHelpText() {
        return helpText;
    }

    public void setHelpText(HashMap<String, String> helpText) {
        this.helpText = helpText;
    }

    public HashMap<String, String> getDescription() {
        return description;
    }

    public void setDescription(HashMap<String, String> description) {
        this.description = description;
    }

}
