package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.db.TracingFormDao;
import org.unicef.rapidreg.db.impl.TracingFormDaoImpl;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.service.impl.TracingFormServiceImpl;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TracingFormServiceTest {
    private TracingFormService tracingFormService;
    private TracingFormDao tracingFormDao;

    private String formForm = "{\n" +
            "  \"Enquiries\": [\n" +
            "    {\n" +
            "      \"order\": 10,\n" +
            "      \"fields\": [\n" +
            "        {\n" +
            "          \"name\": \"tracingworker_name\",\n" +
            "          \"type\": \"text_field\",\n" +
            "          \"editable\": true,\n" +
            "          \"multi_select\": false,\n" +
            "          \"mobile_visible\":true,\n" +
            "          \"show_on_minify_form\":true,\n" +
            "          \"required\":false,\n" +
            "          \"display_name\": {\n" +
            "            \"en\": \"Field/Tracing/Social Worker\"\n" +
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
        tracingFormDao = mock(TracingFormDaoImpl.class);
        tracingFormService = new TracingFormServiceImpl(tracingFormDao);
    }

    @Test
    public void should_be_true_when_form_is_ready() {
        TracingForm tracingForm = new TracingForm();
        tracingForm.setForm(new Blob());
        when(tracingFormDao.getTracingForm()).thenReturn(tracingForm);
        boolean result = tracingFormService.isReady();
        assertThat(result, is(true));
        verify(tracingFormDao, times(2)).getTracingForm();
    }

    @Test
    public void should_be_false_when_form_is_not_exist_in_db() {
        when(tracingFormDao.getTracingForm()).thenReturn(null);
        assertThat(tracingFormService.isReady(), is(false));
        verify(tracingFormDao, times(1)).getTracingForm();
    }

    @Test
    public void should_be_false_when_tracing_form_can_not_get_form() {
        TracingForm tracingForm = new TracingForm();
        when(tracingFormDao.getTracingForm()).thenReturn(tracingForm);
        assertThat(tracingFormService.isReady(), is(false));
        verify(tracingFormDao, times(2)).getTracingForm();
    }

    @Test
    public void should_get_tracing_form() throws IOException {
        TracingForm tracingForm = new TracingForm();
        tracingForm.setForm(new Blob(formForm.getBytes()));
        when(tracingFormDao.getTracingForm()).thenReturn(tracingForm);
        TracingTemplateForm form = tracingFormService.getCPTemplate();

        assertThat(form.getSections().size(), is(1));

        Section section = form.getSections().get(0);
        assertThat(section.getName().get("en"), is("Record Owner"));
        assertThat(section.getOrder(), is(10));
        assertThat(section.getHelpText().get("en"), is(""));
        assertThat(section.getBaseLanguage(), is("en"));

        Field field = section.getFields().get(0);
        assertThat(field.getName(), is("tracingworker_name"));
        assertThat(field.getDisplayName().get("en"), is("Field/Tracing/Social Worker"));
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
    public void should_save_when_existing_tracing_form_is_null() {
        when(tracingFormDao.getTracingForm()).thenReturn(null);
        TracingForm tracingForm = mock(TracingForm.class);
        tracingFormService.saveOrUpdateForm(tracingForm);
        verify(tracingForm, times(1)).save();
    }

    @Test
    public void should_update_when_existing_tracing_form_is_not_null() {
        TracingForm existingTracingForm = mock(TracingForm.class);
        when(tracingFormDao.getTracingForm()).thenReturn(existingTracingForm);
        TracingForm tracingForm = mock(TracingForm.class);
        tracingFormService.saveOrUpdateForm(tracingForm);
        verify(existingTracingForm, times(1)).setForm(tracingForm.getForm());
        verify(existingTracingForm, times(1)).update();
    }
}