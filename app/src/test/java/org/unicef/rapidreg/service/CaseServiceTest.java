package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseSection;
import org.unicef.rapidreg.model.Case;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CaseServiceTest {
    private CaseDao caseDao = mock(CaseDaoImpl.class);
    private CaseService caseService = new CaseService(caseDao);
    private Case child;

    @Before
    public void setUp() throws Exception {
        child = new Case();
    }

    @Test
    public void should_get_case_map_by_unique_id() {
        String caseJson = "{\"name\": \"jack\"}";
        child.setContent(new Blob(caseJson.getBytes()));
        when(caseDao.getCaseByUniqueId("uuid")).thenReturn(child);
        Map<String, String> cases = caseService.getCaseMapByUniqueId("uuid");

        assertThat(cases.size(), is(2));
        assertThat(cases.get("unique_id"), is("uuid"));
        assertThat(cases.get("name"), is("jack"));

        when(caseDao.getCaseByUniqueId("uuid")).thenReturn(null);
        cases = caseService.getCaseMapByUniqueId("uuid");

        assertThat(cases.size(), is(0));
    }

    @Test
    public void should_get_required_filed_list_when_exist_in_case_fields() {
        List<CaseField> caseFields = new CaseSection().getFields();
        caseFields.add(makeCaseField("age", true));
        caseFields.add(makeCaseField("sex", true));
        caseFields.add(makeCaseField("name", false));

        List<String> requiredFiledNames = caseService.fetchRequiredFiledNames(caseFields);
        assertThat(requiredFiledNames, hasSize(2));
        assertThat(requiredFiledNames, containsInAnyOrder("sex", "age"));
    }

    @Test
    public void should_get_empty_required_filed_list_when_does_not_exist_in_case_fields() {
        List<CaseField> caseFields = new CaseSection().getFields();
        caseFields.add(makeCaseField("age", false));
        caseFields.add(makeCaseField("sex", false));
        caseFields.add(makeCaseField("name", false));

        List<String> requiredFiledNames = caseService.fetchRequiredFiledNames(caseFields);
        assertThat(requiredFiledNames, hasSize(0));
    }


    private CaseField makeCaseField(String name, boolean required) {
        CaseField caseField = new CaseField();
        caseField.setRequired(required);
        HashMap<String, String> displayNames = new HashMap<>();
        displayNames.put("en", name);

        caseField.setDisplayName(displayNames);
        return caseField;
    }
}
