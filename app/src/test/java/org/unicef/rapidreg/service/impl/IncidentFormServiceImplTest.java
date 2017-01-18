package org.unicef.rapidreg.service.impl;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.unicef.rapidreg.base.RecordConfiguration;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.repository.impl.IncidentFormDaoImpl;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.impl.IncidentFormServiceImpl;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncidentFormServiceImplTest {
    private IncidentFormService incidentFormService;
    private IncidentFormDao incidentFormDao;

    private String formForm = "{\n" +
            "  \"Incidents\": [\n" +
            "    {\n" +
            "      \"order\": 10,\n" +
            "      \"fields\": [\n" +
            "        {\n" +
            "          \"name\": \"incidentworker_name\",\n" +
            "          \"type\": \"text_field\",\n" +
            "          \"editable\": true,\n" +
            "          \"multi_select\": false,\n" +
            "          \"mobile_visible\":true,\n" +
            "          \"show_on_minify_form\":true,\n" +
            "          \"required\":false,\n" +
            "          \"display_name\": {\n" +
            "            \"en\": \"Field/Incident/Social Worker\"\n" +
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
            "        \"en\": \"Record Owner\"\n" +
            "      },\n" +
            "      \"help_text\": {\n" +
            "        \"en\": \"\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    @Before
    public void setUp() throws Exception {
        incidentFormDao = mock(IncidentFormDaoImpl.class);
        incidentFormService = new IncidentFormServiceImpl(incidentFormDao);
    }

    @Test
    public void should_be_true_when_form_is_ready() {
        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob());
        when(incidentFormDao.getIncidentForm(RecordConfiguration.MODULE_ID_GBV)).thenReturn
                (incidentForm);
        boolean result = incidentFormService.isReady();
        assertThat(result, is(true));
        verify(incidentFormDao, times(1)).getIncidentForm(RecordConfiguration.MODULE_ID_GBV);
    }

    @Test
    public void should_be_false_when_form_is_not_exist_in_db() {
        when(incidentFormDao.getIncidentForm(RecordConfiguration.MODULE_ID_GBV)).thenReturn(null);
        assertThat(incidentFormService.isReady(), is(false));
        verify(incidentFormDao, times(1)).getIncidentForm(RecordConfiguration.MODULE_ID_GBV);
    }

    @Test
    public void should_be_false_when_incident_form_can_not_get_form() {
        IncidentForm incidentForm = new IncidentForm();
        when(incidentFormDao.getIncidentForm(RecordConfiguration.MODULE_ID_GBV)).thenReturn
                (incidentForm);
            assertThat(incidentFormService.isReady(), is(false));
        verify(incidentFormDao, times(1)).getIncidentForm(RecordConfiguration.MODULE_ID_GBV);
    }

    @Test
    public void should_get_incident_form() throws IOException {
        IncidentForm incidentForm = new IncidentForm();
        incidentForm.setForm(new Blob(formForm.getBytes()));
        when(incidentFormDao.getIncidentForm(anyString())).thenReturn(incidentForm);
        IncidentTemplateForm form = incidentFormService.getGBVTemplate();

        assertThat(form.getSections().size(), is(1));

        Section section = form.getSections().get(0);
        assertThat(section.getName().get("en"), is("Record Owner"));
        assertThat(section.getOrder(), is(10));
        assertThat(section.getHelpText().get("en"), is(""));
        assertThat(section.getBaseLanguage(), is("en"));

        Field field = section.getFields().get(0);
        assertThat(field.getName(), is("incidentworker_name"));
        assertThat(field.getDisplayName().get("en"), is("Field/Incident/Social Worker"));
        assertThat(field.getHelpText().get("en"), is(""));
        assertThat(field.getType(), is("text_field"));
        assertThat(field.getOptionStringsText().get("en").size(), is(0));
        assertThat(field.isShowOnMiniForm(), is(true));
        assertThat(field.isMultiSelect(), is(false));
        assertThat(field.isEditable(), is(true));
        assertThat(field.isRequired(), is(false));
        assertThat(field.getSubForm(), is(nullValue()));
    }

    @Test
    public void should_save_when_existing_incident_form_is_null() {
        when(incidentFormDao.getIncidentForm(anyString())).thenReturn(null);
        IncidentForm incidentForm = mock(IncidentForm.class);
        incidentFormService.saveOrUpdate(incidentForm);
        verify(incidentForm, times(1)).save();
    }

    @Test
    public void should_update_when_existing_incident_form_is_not_null() {
        IncidentForm existingIncidentForm = mock(IncidentForm.class);
        when(incidentFormDao.getIncidentForm(anyString())).thenReturn(existingIncidentForm);
        IncidentForm incidentForm = mock(IncidentForm.class);
        incidentFormService.saveOrUpdate(incidentForm);
        verify(existingIncidentForm,times(1)).setForm(incidentForm.getForm());
        verify(existingIncidentForm,times(1)).update();
    }
}
