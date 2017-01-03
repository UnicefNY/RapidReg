package org.unicef.rapidreg.forms;

import org.junit.Test;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TracingTemplateFormTest {
    @Test
    public void should_return_string_of_tracing_template_form_when_section_is_empty() throws
            Exception {
        TracingTemplateForm tracingTemplateForm = createTracingTemplateForm();

        assertThat(tracingTemplateForm.toString(), is("<Enquiries>\n"));
    }

    @Test
    public void should_return_string_of_tracing_template_form_when_has_one_section() throws
            Exception {
        TracingTemplateForm tracingTemplateForm = createTracingTemplateForm();
        tracingTemplateForm.setSections(createSections(createSection()));

        assertThat(tracingTemplateForm.toString(), is("<Enquiries>\n<Section>\nname: null\norder:" +
                " " +
                "0\nhelpText: null\nbaseLanguage: null\n\n"));
    }

    @Test
    public void should_return_string_of_tracing_template_form_when_has_two_section() throws
            Exception {
        TracingTemplateForm tracingTemplateForm = createTracingTemplateForm();
        tracingTemplateForm.setSections(createSections(createSection(), createSection()));

        assertThat(tracingTemplateForm.toString(), is("<Enquiries>\n<Section>\nname: null\norder:" +
                " " +
                "0\nhelpText: null\nbaseLanguage: null\n\n<Section>\nname: null\norder: " +
                "0\nhelpText: null\nbaseLanguage: null\n\n"));
    }

    private TracingTemplateForm createTracingTemplateForm() {
        return new TracingTemplateForm();
    }

    private List<Section> createSections(Section... sections) {
        return Arrays.asList(sections);
    }

    private Section createSection() {
        return new Section();
    }
}