package org.unicef.rapidreg.childcase.caseregister;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView;
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
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_CP;

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
        when(RecordService.validateRequiredFields(cpCaseTemplate, itemValuesMap)).thenReturn(false);

        caseRegisterPresenter.saveRecord(itemValuesMap, photoPaths, callback);

        verify(callback).onRequiredFieldNotFilled();
    }

}