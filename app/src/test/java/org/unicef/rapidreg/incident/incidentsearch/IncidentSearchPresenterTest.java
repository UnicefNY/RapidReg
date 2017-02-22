package org.unicef.rapidreg.incident.incidentsearch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IncidentSearchPresenterTest {

    @Mock
    IncidentService incidentService;

    @Mock
    IncidentFormService incidentFormService;

    @InjectMocks
    IncidentSearchPresenter incidentSearchPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_get_incident_list_when_search_result() throws Exception {
        Map<String, String> searchConditions = mock(Map.class);
        List<Long> list = new ArrayList<>();

        when(searchConditions.get(anyString())).thenReturn("0");
        when(incidentService.getSearchResult(anyString(),anyString(),anyInt(),anyInt(),anyString(),anyString()))
                .thenReturn(list);

        assertThat("Should return same list", incidentSearchPresenter.getSearchResult(searchConditions), is(list));
    }

    @Test
    public void should_return_list_when_get_violence_type_list() throws Exception {
        List<String> list = new ArrayList<>();

        when(incidentFormService.getViolenceTypeList()).thenReturn(list);

        assertThat("Should return same list", incidentSearchPresenter.getViolenceTypeList(), is(list));
    }

    @Test
    public void should_return_list_when_get_incident_location_list() throws Exception {
        List<String> list = new ArrayList<>();

        when(incidentFormService.getLocationList()).thenReturn(list);

        assertThat("Should return same list", incidentSearchPresenter.getIncidentLocationList(), is(list));
    }
}