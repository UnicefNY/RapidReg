package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.model.form.childcase.CaseField;
import org.unicef.rapidreg.model.form.childcase.CaseFormRoot;
import org.unicef.rapidreg.model.form.childcase.CaseSection;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class CaseFormRootServiceTest {
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
        caseFormService = new CaseFormService(caseFormDao);
    }

    @Test
    public void should_get_case_form() throws IOException {
        when(caseFormDao.getCaseFormContent()).thenReturn(new Blob(formForm.getBytes()));
        CaseFormRoot form = caseFormService.getCurrentForm();

        assertThat(form.getSections().size(), is(1));

        CaseSection caseSection = form.getSections().get(0);
        assertThat(caseSection.getName().get("en"), is("Basic Identity"));
        assertThat(caseSection.getOrder(), is(10));
        assertThat(caseSection.getHelpText().get("en"), is(""));
        assertThat(caseSection.getBaseLanguage(), is("en"));

        CaseField caseField = caseSection.getFields().get(0);
        assertThat(caseField.getName(), is("case_id"));
        assertThat(caseField.getDisplayName().get("en"), is("Long ID"));
        assertThat(caseField.getHelpText().get("en"), is(""));
        assertThat(caseField.getType(), is("text_field"));
        assertThat(caseField.getOptionStringsText().get("en").size(), is(0));
        assertThat(caseField.getSubForm(), is(nullValue()));
    }
}
