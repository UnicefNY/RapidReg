package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.unicef.rapidreg.db.IncidentDao;
import org.unicef.rapidreg.db.impl.IncidentDaoImpl;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Incident;

import java.sql.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class IncidentServiceTest {

    private IncidentDao incidentDao = mock(IncidentDaoImpl.class);
    private IncidentService incidentService = new IncidentService(incidentDao);


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        incidentService = new IncidentService(incidentDao);
    }


    @Test
    public void should_return_all_order_ids_sorted_by_ascending() throws Exception {
        Incident[] orders = new Incident[]{new Incident(1), new Incident(2), new Incident(3)};
        List<Incident> orderList = Arrays.asList(orders);
        when(incidentDao.getAllIncidentsOrderByDate(true)).thenReturn(orderList);
        assertThat("When call getAllOrdersByDateAsc() should return orders sorted by date.",
                incidentService.getAllOrderByDateASC(), is(Arrays.asList(new Long[]{1L,2L,3L})));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_descending() throws Exception {
        Incident[] orders = new Incident[]{new Incident(3), new Incident(2), new Incident(1)};
        List<Incident> orderList = Arrays.asList(orders);
        when(incidentDao.getAllIncidentsOrderByDate(false)).thenReturn(orderList);
        assertThat("When call getAllOrderByDateDES() should return orders sorted by date.",
                incidentService.getAllOrderByDateDES(), is(Arrays.asList(new Long[]{3L,2L,1L})));
    }

    @Test
    public void should_get_required_filed_list_when_exist_in_incident_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeIncidentField("age", true));
        fields.add(makeIncidentField("sex", true));
        fields.add(makeIncidentField("name", false));

        List<String> requiredFiledNames = incidentService.fetchRequiredFiledNames(fields);
        assertThat(requiredFiledNames, hasSize(2));
        assertThat(requiredFiledNames, containsInAnyOrder("sex", "age"));
    }

    @Test
    public void should_return_search_result_when_give_search_conditions() throws Exception {

        Incident[] orders = new Incident[]{new Incident(3), new Incident(2), new Incident(1)};
        List<Incident> orderList = Arrays.asList(orders);

        when(incidentDao.getIncidentListByConditionGroup(any(ConditionGroup.class))).thenReturn
                (orderList);

        Assert.assertThat("When call getSearchResult() should return search result depends on search " +
                        "condition",
                incidentService.getSearchResult("uniqueId", "name", 1, 20, new Date(20161108)),
                is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Test
    public void should_get_empty_required_filed_list_when_does_not_exist_in_case_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeIncidentField("age", false));
        fields.add(makeIncidentField("sex", false));
        fields.add(makeIncidentField("name", false));

        List<String> requiredFiledNames = incidentService.fetchRequiredFiledNames(fields);
        assertThat(requiredFiledNames, hasSize(0));
    }

    private Field makeIncidentField(String name, boolean required) {
        Field field = new Field();
        field.setRequired(required);
        field.setName(name);
        return field;
    }

}
