package org.unicef.rapidreg.childcase.casesearch;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.childcase.casesearch.CaseSearchPresenter;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.CaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PrimeroAppConfiguration.class)
public class CaseSearchPresenterTest {

    @Mock
    CaseService caseService;

    @InjectMocks
    CaseSearchPresenter caseSearchPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(PrimeroAppConfiguration.class);
    }

    @Test
    public void should_get_CP_search_result() throws Exception {
        List<Long> list = new ArrayList<>();
        Map<String, String> searchCondition = mock(Map.class);
        User user = new User();
        user.setRole(User.ROLE_CP);

        when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
        when(searchCondition.get(anyString())).thenReturn("0");
        when(caseService.getCPSearchResult(anyString(),anyString(),anyInt(),anyInt(),anyString(),any()))
                .thenReturn(list);

        assertThat("Should return CP search list", caseSearchPresenter.getSearchResult(searchCondition),
                is(list));
        verify(caseService, times(1)).
                getCPSearchResult(anyString(), anyString(),anyInt(),anyInt(),anyString(),any());
    }

    @Test
    public void should_get_GBV_search_result() throws Exception {
        List<Long> list = new ArrayList<>();
        User user = new User();
        user.setRole(User.ROLE_GBV);

        when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
        when(caseService.getGBVSearchResult(anyString(),anyString(),anyString(),any()))
                .thenReturn(list);

        assertThat("Should return GBV search list", caseSearchPresenter.getSearchResult(Collections.emptyMap()),
                is(list));
        verify(caseService, times(1)).getGBVSearchResult(anyString(),anyString(),anyString(),any());
    }
}