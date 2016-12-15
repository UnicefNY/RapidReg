package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.service.impl.CaseFormServiceImpl;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseFormServiceTest {
    private static final int MAX_RETRY_NUM = 3;

    private static CaseFormService caseFormService;
    private static CaseFormDao caseFormDao;

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

    @BeforeClass
    public static void setup() {
        caseFormDao = mock(CaseFormDaoImpl.class);
        caseFormService = new CaseFormServiceImpl(caseFormDao);
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void should_get_case_form() throws IOException {
        CaseForm caseForm = new CaseForm();
        caseForm.setForm(new Blob(formForm.getBytes()));
        when(caseFormDao.getCaseForm(anyString())).thenReturn(caseForm);
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

}
