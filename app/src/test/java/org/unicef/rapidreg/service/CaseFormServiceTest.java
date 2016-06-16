package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.unicef.rapidreg.service.CaseFormService.FormLoadStateMachine;

@RunWith(RobolectricTestRunner.class)
public class CaseFormServiceTest {
    private static final int MAX_RETRY_NUM = 3;

    private static CaseFormService caseFormService;
    private static CaseFormDao caseFormDao;
    private FormLoadStateMachine stateMachine;

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

    @Before
    public void setUp() throws Exception {
        stateMachine = FormLoadStateMachine.getInstance(MAX_RETRY_NUM);
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

    @Test
    public void should_return_false_when_do_not_reach_max_retry_num() {
        assertThat(stateMachine.hasReachMaxRetryNum(), is(false));

        stateMachine.addOnce();
        assertThat(stateMachine.hasReachMaxRetryNum(), is(false));

        stateMachine.addOnce();
        assertThat(stateMachine.hasReachMaxRetryNum(), is(false));
    }

    @Test
    public void should_return_true_when_reach_max_retry_num() {
        stateMachine.addOnce();
        stateMachine.addOnce();
        stateMachine.addOnce();

        assertThat(stateMachine.hasReachMaxRetryNum(), is(true));
    }
}
