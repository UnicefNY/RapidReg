package org.unicef.rapidreg.tracing.tracingsearch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.unicef.rapidreg.service.TracingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TracingSearchPresenterTest {

    @Mock
    TracingService tracingService;

    @InjectMocks
    TracingSearchPresenter tracingSearchPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_get_tracing_list_when_get_search_result() throws Exception {
        Map<String, String> searchConditions = mock(Map.class);
        List<Long> list = new ArrayList<>();

        when(searchConditions.get(anyString())).thenReturn("0");
        when(tracingService.getSearchResult(anyString(),anyString(),anyInt(),anyInt(),any()))
                .thenReturn(list);

        assertThat("Should return same list", tracingSearchPresenter.getSearchResult(searchConditions), is(list));
    }
}