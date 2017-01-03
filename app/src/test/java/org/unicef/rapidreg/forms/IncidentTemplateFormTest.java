package org.unicef.rapidreg.forms;

import org.junit.Test;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IncidentTemplateFormTest {

    @Test
    public void should_return_string_of_incident_template_form_when_section_is_empty() throws
            Exception {
        IncidentTemplateForm incidentTemplateForm = createIncidentTemplateForm();

        assertThat(incidentTemplateForm.toString(), is("<Incidents>\n"));
    }

    @Test
    public void should_return_string_of_incident_template_form_when_has_one_section() throws Exception {
        IncidentTemplateForm incidentTemplateForm = createIncidentTemplateForm();
        incidentTemplateForm.setSections(createSections(createSection()));

        assertThat(incidentTemplateForm.toString(), is("<Incidents>\n<Section>\nname: null\norder: " +
                "0\nhelpText: null\nbaseLanguage: null\n\n"));
    }

    @Test
    public void should_return_string_of_incident_template_form_when_has_two_section() throws Exception {
        IncidentTemplateForm incidentTemplateForm = createIncidentTemplateForm();
        incidentTemplateForm.setSections(createSections(createSection(), createSection()));

        assertThat(incidentTemplateForm.toString(), is("<Incidents>\n<Section>\nname: null\norder: " +
                "0\nhelpText: null\nbaseLanguage: null\n\n<Section>\nname: null\norder: " +
                "0\nhelpText: null\nbaseLanguage: null\n\n"));
    }

    private IncidentTemplateForm createIncidentTemplateForm() {
        return new IncidentTemplateForm();
    }

    private List<Section> createSections(Section... sections) {
        return Arrays.asList(sections);
    }

    private Section createSection() {
        return new Section();
    }
}