package org.unicef.rapidreg.childcase.caseregister;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_CP;

@RunWith(MockitoJUnitRunner.class)
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
        List<Field> mockFields = Arrays.asList(new Field[]{miniFormField, otherField});

        Section section = mock(Section.class);
        when(section.getFields()).thenReturn(mockFields);
        List<Section> sections = Arrays.asList(new Section[]{section});

        CaseTemplateForm form = mock(CaseTemplateForm.class);
        when(form.getSections()).thenReturn(sections);

        when(caseFormService.getCPTemplate()).thenReturn(form);

        caseRegisterPresenter.setCaseType(MODULE_CASE_CP);

        List<Field> actual = caseRegisterPresenter.getFields();

        assertThat("Should contain mini form field", actual.contains(miniFormField), is(true));
        assertThat("Should not contain full form field", actual.contains(otherField), is(false));
    }
}