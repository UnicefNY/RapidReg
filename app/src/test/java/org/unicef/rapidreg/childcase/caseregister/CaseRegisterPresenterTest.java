package org.unicef.rapidreg.childcase.caseregister;

import com.raizlabs.android.dbflow.data.Blob;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView.SaveRecordCallback;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_CP;
import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_GBV;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RecordService.class})
public class CaseRegisterPresenterTest {

    @Mock
    CaseService caseService;

    @Mock
    CaseFormService caseFormService;

    @Mock
    CasePhotoService casePhotoService;

    @InjectMocks
    private CaseRegisterPresenter caseRegisterPresenter;

    private static final String CASE_JSON =
            "{" +
                    "\"safety_plan_main_concern\":\"kkkk\"," +
                    "\"safety_plan_resources_economic\":\"hjvgh\"," +
                    "\"consent_for_services\":true," +
                    "\"module_id\":\"primeromodule-gbv\"," +
                    "\"case_id_display\":\"1cb7a98\"," +
                    "\"case_id\":\"6cd8cee7-3dea-4417-ba05-9b0b81cb7a98\"," +
                    "\"owned_by\":\"primero\"," +
                    "\"created_by\":\"primero\"," +
                    "\"previously_owned_by\":\"primero\"," +
                    "\"registration_date\":\"15/12/2016\"" +
            "}";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_cp_template_when_case_type_is_cp() throws Exception {
        CaseTemplateForm form = mock(CaseTemplateForm.class);
        when(caseFormService.getCPTemplate()).thenReturn(form);

        caseRegisterPresenter.setCaseType(MODULE_CASE_CP);
        RecordForm actual = caseRegisterPresenter.getTemplateForm();

        assertThat("Should return CP template when case type is CP.", actual, Is.<RecordForm>is(form));
    }

    @Test
    public void should_return_editable_and_mini_form_when_get_fields() throws Exception {
        Field miniFormField = mock(Field.class);
        when(miniFormField.isShowOnMiniForm()).thenReturn(true);
        when(miniFormField.isPhotoUploadBox()).thenReturn(false);

        Field otherField = mock(Field.class);
        when(otherField.isShowOnMiniForm()).thenReturn(false);

        Section section = mock(Section.class);
        when(section.getFields()).thenReturn(Arrays.asList(new Field[]{miniFormField, otherField}));

        List<Section> sections = Arrays.asList(new Section[]{section});

        CaseTemplateForm form = mock(CaseTemplateForm.class);
        when(form.getSections()).thenReturn(sections);

        when(caseFormService.getCPTemplate()).thenReturn(form);

        caseRegisterPresenter.setCaseType(MODULE_CASE_CP);

        List<Field> actual = caseRegisterPresenter.getFields();

        assertThat("Should contain mini form field", actual.contains(miniFormField), is(true));
        assertThat("Should not contain full form field", actual.contains(otherField), is(false));
        verify(caseFormService,times(1)).getCPTemplate();
    }

    @Test
    public void should_save_fail_when_required_field_is_not_finished() throws Exception {
        CaseTemplateForm cpCaseTemplate = mock(CaseTemplateForm.class);
        when(caseFormService.getCPTemplate()).thenReturn(cpCaseTemplate);
        caseRegisterPresenter.setCaseType(MODULE_CASE_CP);

        ItemValuesMap itemValuesMap = mock(ItemValuesMap.class);
        String photoPath = mock(String.class);
        List<String> photoPaths = Arrays.asList(new String[]{photoPath});
        SaveRecordCallback callback = mock(SaveRecordCallback.class);

        PowerMockito.mockStatic(RecordService.class);
        when(caseService.validateRequiredFields(cpCaseTemplate, itemValuesMap)).thenReturn(false);

        caseRegisterPresenter.saveRecord(itemValuesMap, photoPaths, callback);

        verify(callback).onRequiredFieldNotFilled();
    }


    @Test
    public void should_return_item_values_by_record_id() throws Exception {
        Date date = new Date();
        date.setYear(2016);
        date.setMonth(12);
        date.setDate(15);

        Case caseItem = new Case();
        caseItem.setContent(new Blob(CASE_JSON.getBytes()));
        caseItem.setId(123L);
        caseItem.setUniqueId("6cd8cee7-3dea-4417-ba05-9b0b81cb7a98");
        caseItem.setRegistrationDate(date);

        when(caseService.getById(123L)).thenReturn(caseItem);

        String shortUUID = "uuid";
        PowerMockito.mockStatic(RecordService.class);
        when(caseService.getShortUUID(caseItem.getUniqueId())).thenReturn(shortUUID);

        ItemValuesMap actual = caseRegisterPresenter.getItemValuesByRecordId(123L);

        assertThat("Should have same size of items", actual.getValues().size(), is(14));
        assertThat("safety_plan_main_concern should be right", actual.getAsString("safety_plan_main_concern"), is("kkkk"));
        assertThat("safety_plan_resources_economic should be right", actual.getAsString("safety_plan_resources_economic"), is("hjvgh"));
        assertThat("consent_for_services should be right", actual.getAsBoolean("consent_for_services"), is(true));
        assertThat("module_id should be right", actual.getAsString("module_id"), is("primeromodule-gbv"));
        assertThat("case_id_display should be right", actual.getAsString("case_id_display"), is("1cb7a98"));
        assertThat("case_id should be right", actual.getAsString("case_id"), is("6cd8cee7-3dea-4417-ba05-9b0b81cb7a98"));
        assertThat("owned_by should be right", actual.getAsString("owned_by"), is("primero"));
        assertThat("created_by should be right", actual.getAsString("created_by"), is("primero"));
        assertThat("previously_owned_by should be right", actual.getAsString("previously_owned_by"), is("primero"));
        assertThat("_id_normal_state should be right", actual.getAsString("_id_normal_state"), is(shortUUID));
        assertThat("_id should be right", actual.getAsLong("_id"), is(123L));
        assertThat("_registration_date should be right", actual.getAsString("_registration_date"),
                is(SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(date)));
        assertThat("registration_date should be right", actual.getAsString("registration_date"), is("15/12/2016"));
    }

    @Test
    public void should_return_cp_case_form_when_type_is_cp() throws Exception {
        CaseTemplateForm cpCaseTemplateForm = new CaseTemplateForm();
        when(caseFormService.getCPTemplate()).thenReturn(cpCaseTemplateForm);
        CaseTemplateForm gbvCaseTemplateForm = new CaseTemplateForm();
        when(caseFormService.getGBVTemplate()).thenReturn(gbvCaseTemplateForm);

        caseRegisterPresenter.setCaseType(MODULE_CASE_CP);

        RecordForm actual = caseRegisterPresenter.getTemplateForm();

        assertThat("Should be CP case template form", actual, Is.<RecordForm>is(cpCaseTemplateForm));
    }

    @Test
    public void should_return_gbv_case_form_when_type_is_cp() throws Exception {
        CaseTemplateForm cpCaseTemplateForm = new CaseTemplateForm();
        when(caseFormService.getCPTemplate()).thenReturn(cpCaseTemplateForm);
        CaseTemplateForm gbvCaseTemplateForm = new CaseTemplateForm();
        when(caseFormService.getGBVTemplate()).thenReturn(gbvCaseTemplateForm);

        caseRegisterPresenter.setCaseType(MODULE_CASE_GBV);

        RecordForm actual = caseRegisterPresenter.getTemplateForm();

        assertThat("Should be CP case template form", actual, Is.<RecordForm>is(gbvCaseTemplateForm));
    }
}