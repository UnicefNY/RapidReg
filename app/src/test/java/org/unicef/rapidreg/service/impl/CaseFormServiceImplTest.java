package org.unicef.rapidreg.service.impl;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.CaseFormDao;

import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroAppConfiguration.class})
public class CaseFormServiceImplTest {
    private static final int MAX_RETRY_NUM = 3;

    @Mock
    CaseFormDao caseFormDao;

    @InjectMocks
    CaseFormServiceImpl caseFormService;

    private String formForm = "{\n" +
            "  \"Children\": [\n" +
            "    {\n" +
            "      \"order\": 10,\n" +
            "      \"fields\": [\n" +
            "        {\n" +
            "          \"name\": \"case_id\",\n" +
            "          \"type\": \"text_field\",\n" +
            "          \"editable\": false,\n" +
            "          \"multi_select\": false,\n" +
            "          \"display_name\": {\n" +
            "            \"en\": \"Long ID\"\n" +
            "          },\n" +
            "          \"help_text\": {\n" +
            "            \"en\": \"\"\n" +
            "          },\n" +
            "          \"option_strings_text\": {\n" +
            "            \"en\": []\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"base_language\": \"en\",\n" +
            "      \"name\": {\n" +
            "        \"en\": \"Basic Identity\"\n" +
            "      },\n" +
            "      \"help_text\": {\n" +
            "        \"en\": \"\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(PrimeroAppConfiguration.class);
    }

    @Test
    public void should_return_true_if_form_is_ready() throws Exception {
        User user = new User();
        user.setRole(User.Role.CP.getValue());
        Mockito.when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);

        CaseForm cpCaseForm = createCPCaseForm();
        when(caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, PrimeroAppConfiguration.getApiBaseUrl()))
                .thenReturn(cpCaseForm);

        assertTrue(caseFormService.isReady());
    }

    @Test
    public void should_return_false_if_form_is_not_ready() throws Exception {
        User user = new User();
        user.setRole(User.Role.CP.getValue());
        Mockito.when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
        when(caseFormDao.getCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, PrimeroAppConfiguration.getApiBaseUrl()))
                .thenReturn(null);

        assertFalse(caseFormService.isReady());
    }

    @Test
    public void should_save_case_form_if_not_exist() throws Exception {
        CaseForm caseForm = mock(CaseForm.class);
        when(caseFormDao.getCaseForm("primeromodule-cp", PrimeroAppConfiguration.getApiBaseUrl())).thenReturn(null);

        caseFormService.saveOrUpdate(caseForm);

        verify(caseForm, times(1)).save();
    }

    @Test
    public void should_update_case_form_if_exist() throws Exception {
        CaseForm caseForm = mock(CaseForm.class);
        when(caseForm.getModuleId()).thenReturn("primeromodule-cp");
        Blob formBlob = mock(Blob.class);
        when(caseForm.getForm()).thenReturn(formBlob);
        when(caseFormDao.getCaseForm("primeromodule-cp", PrimeroAppConfiguration.getApiBaseUrl())).thenReturn(caseForm);

        caseFormService.saveOrUpdate(caseForm);

        verify(caseForm, times(1)).setForm(formBlob);
        verify(caseForm, times(1)).update();
    }

    @Test
    public void should_get_cp_case_form() throws IOException {
        CaseForm caseForm = new CaseForm();
        caseForm.setForm(new Blob(formForm.getBytes()));
        when(caseFormDao.getCaseForm(anyString(), anyString())).thenReturn(caseForm);
        CaseTemplateForm form = caseFormService.getCPTemplate();

        assertThat(form.getSections().size(), is(1));

        Section section = form.getSections().get(0);
        assertThat(section.getName().get("en"), is("Basic Identity"));
        assertThat(section.getOrder(), is(10));
        assertThat(section.getHelpText().get("en"), is(""));
        assertThat(section.getBaseLanguage(), is("en"));

        Field field = section.getFields().get(0);
        assertThat(field.getName(), is("case_id"));
        assertThat(field.getDisplayName().get("en"), is("Long ID"));
        assertThat(field.getHelpText().get("en"), is(""));
        assertThat(field.getType(), is("text_field"));
        assertThat(field.getOptionStringsText().get("en").size(), is(0));
        assertThat(field.getSubForm(), is(nullValue()));
    }

    @Test
    public void should_get_gbv_case_form() throws Exception {
        CaseForm caseForm = new CaseForm();
        caseForm.setForm(new Blob(formForm.getBytes()));
        when(caseFormDao.getCaseForm(anyString(), anyString())).thenReturn(caseForm);

        CaseTemplateForm form = caseFormService.getGBVTemplate();

        assertThat(form.getSections().size(), is(1));

        Section section = form.getSections().get(0);
        assertThat(section.getName().get("en"), is("Basic Identity"));
        assertThat(section.getOrder(), is(10));
        assertThat(section.getHelpText().get("en"), is(""));
        assertThat(section.getBaseLanguage(), is("en"));

        Field field = section.getFields().get(0);
        assertThat(field.getName(), is("case_id"));
        assertThat(field.getDisplayName().get("en"), is("Long ID"));
        assertThat(field.getHelpText().get("en"), is(""));
        assertThat(field.getType(), is("text_field"));
        assertThat(field.getOptionStringsText().get("en").size(), is(0));
        assertThat(field.getSubForm(), is(nullValue()));
    }

    private CaseForm createCPCaseForm() {
        CaseForm caseForm = new CaseForm();
        caseForm.setModuleId("primeromodule-cp");
        caseForm.setForm(new Blob());

        return caseForm;
    }

    private CaseForm createGBVCaseForm() {
        CaseForm caseForm = new CaseForm();
        caseForm.setModuleId("primeromodule-gbv");
        caseForm.setForm(new Blob());

        return caseForm;
    }
}
