package org.unicef.rapidreg.forms;

import org.junit.Test;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CaseTemplateFormTest {

    @Test
    public void should_return_string_of_case_template_form_when_section_is_empty() throws Exception {
        CaseTemplateForm caseTemplateForm = createCaseTemplateForm();

        assertThat(caseTemplateForm.toString(), is("<Children>\n"));
    }

    @Test
    public void should_return_string_of_case_template_form_when_has_one_section() throws Exception {
        CaseTemplateForm caseTemplateForm = createCaseTemplateForm();
        caseTemplateForm.setSections(createSections(createSection()));

        assertThat(caseTemplateForm.toString(), is("<Children>\n<Section>\nname: null\norder: 0\nhelpText: null\nbaseLanguage: null\n\n"));
    }

    @Test
    public void should_return_string_of_case_template_form_when_has_two_section() throws Exception {
        CaseTemplateForm caseTemplateForm = createCaseTemplateForm();
        caseTemplateForm.setSections(createSections(createSection(), createSection()));

        assertThat(caseTemplateForm.toString(), is("<Children>\n<Section>\nname: null\norder: 0\nhelpText: null\nbaseLanguage: null\n\n<Section>\nname: null\norder: 0\nhelpText: null\nbaseLanguage: null\n\n"));
    }

    private CaseTemplateForm createCaseTemplateForm() {
        return new CaseTemplateForm();
    }

    private List<Section> createSections(Section... sections) {
        return Arrays.asList(sections);
    }

    private Section createSection() {
        return new Section();
    }
}