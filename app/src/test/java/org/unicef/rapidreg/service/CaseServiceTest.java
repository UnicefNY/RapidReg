package org.unicef.rapidreg.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CaseServiceTest {
    private CaseDao caseDao = mock(CaseDaoImpl.class);
    private CaseService caseService = new CaseService(caseDao);

    @Test
    public void should_get_required_filed_list_when_exist_in_case_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeCaseField("age", true));
        fields.add(makeCaseField("sex", true));
        fields.add(makeCaseField("name", false));

        List<String> requiredFiledNames = caseService.fetchRequiredFiledNames(fields);
        assertThat(requiredFiledNames, hasSize(2));
        assertThat(requiredFiledNames, containsInAnyOrder("sex", "age"));
    }

    @Test
    public void should_get_empty_required_filed_list_when_does_not_exist_in_case_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeCaseField("age", false));
        fields.add(makeCaseField("sex", false));
        fields.add(makeCaseField("name", false));

        List<String> requiredFiledNames = caseService.fetchRequiredFiledNames(fields);
           assertThat(requiredFiledNames, hasSize(0));
    }

    private Field makeCaseField(String name, boolean required) {
        Field field = new Field();
        field.setRequired(required);
        field.setName(name);
        return field;
    }
}
