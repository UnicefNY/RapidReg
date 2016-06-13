package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;

import org.junit.Before;
import org.junit.Test;
import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.model.Case;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
        Map<String, String> caseMapByUniqueId = caseService.getCaseMapByUniqueId("uuid");

        assertThat(caseMapByUniqueId.size(), is(1));
        assertThat(caseMapByUniqueId.get("name"), is("jack"));

        when(caseDao.getCaseByUniqueId("uuid")).thenReturn(null);
        caseMapByUniqueId = caseService.getCaseMapByUniqueId("uuid");

        assertThat(caseMapByUniqueId.size(), is(0));
    }
}
