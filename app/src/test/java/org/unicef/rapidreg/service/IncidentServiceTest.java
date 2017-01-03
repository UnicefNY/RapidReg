package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.db.IncidentDao;
import org.unicef.rapidreg.db.impl.IncidentDaoImpl;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unicef.rapidreg.service.IncidentService.INCIDENT_ID;
import static org.unicef.rapidreg.service.RecordService.AGE;
import static org.unicef.rapidreg.service.RecordService.REGISTRATION_DATE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroConfiguration.class})
public class IncidentServiceTest {

    private IncidentDao incidentDao = mock(IncidentDaoImpl.class);
    private IncidentService incidentService = new IncidentService(incidentDao);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(PrimeroConfiguration.class);
    }

    @Test
    public void should_return_incident_by_incident_id() {
        long incidentId = 1L;
        Incident incident = mock(Incident.class);
        when(incidentDao.getIncidentById(incidentId)).thenReturn(incident);
        assertThat(incidentService.getById(incidentId), is(incident));
        verify(incidentDao, times(1)).getIncidentById(incidentId);
    }

    @Test
    public void should_return_incident_by_uniqueId() {
        String uniqueId = "uniqueId";
        Incident incident = mock(Incident.class);
        when(incidentDao.getIncidentByUniqueId(uniqueId)).thenReturn(incident);
        assertThat(incidentService.getByUniqueId(uniqueId), is(incident));
        verify(incidentDao, times(1)).getIncidentByUniqueId(uniqueId);
    }

    @Test
    public void should_return_incident_list() {
        Incident incident = new Incident();
        List<Incident> incidentList = new ArrayList<Incident>();
        incidentList.add(incident);
        when(incidentDao.getAllIncidentsOrderByDate(false, PrimeroConfiguration.getCurrentUser().getUsername())).thenReturn(incidentList);
        assertThat(incidentService.getAll(), is(incidentList));
    }

    @Test
    public void should_return_all_ids() {
        Long l = 1L;
        List<Long> list = new ArrayList<>();
        list.add(l);
        when(incidentDao.getAllIds(PrimeroConfiguration.getCurrentUser().getUsername())).thenReturn(list);
        assertThat(incidentService.getAllIds(), is(list));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_age_ascending() {
        Incident[] orders = new Incident[]{new Incident(1), new Incident(2), new Incident(3)};
        List<Incident> orderList = Arrays.asList(orders);
        when(incidentDao.getAllIncidentsOrderByAge(true, PrimeroConfiguration.getCurrentUser().getUsername())).thenReturn(orderList);
        assertThat(incidentService.getAllOrderByAgeASC(), is(Arrays.asList(new Long[]{1L, 2L,
                3L})));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_age_descending() {
        Incident[] orders = new Incident[]{new Incident(1), new Incident(2), new Incident(3)};
        List<Incident> orderList = Arrays.asList(orders);
        when(incidentDao.getAllIncidentsOrderByAge(false, PrimeroConfiguration.getCurrentUser().getUsername())).thenReturn(orderList);
        assertThat(incidentService.getAllOrderByAgeDES(), is(Arrays.asList(new Long[]{1L, 2L,
                3L})));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_date_ascending() throws Exception {
        Incident[] orders = new Incident[]{new Incident(1), new Incident(2), new Incident(3)};
        List<Incident> orderList = Arrays.asList(orders);
        when(incidentDao.getAllIncidentsOrderByDate(true, PrimeroConfiguration.getCurrentUser().getUsername())).thenReturn(orderList);
        assertThat("When call getAllOrdersByDateAsc() should return orders sorted by date.",
                incidentService.getAllOrderByDateASC(), is(Arrays.asList(new Long[]{1L, 2L, 3L})));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_date_descending() throws Exception {
        Incident[] orders = new Incident[]{new Incident(3), new Incident(2), new Incident(1)};
        List<Incident> orderList = Arrays.asList(orders);
        when(incidentDao.getAllIncidentsOrderByDate(false, PrimeroConfiguration.getCurrentUser().getUsername())).thenReturn(orderList);
        assertThat("When call getAllOrderByDateDES() should return orders sorted by date.",
                incidentService.getAllOrderByDateDES(), is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Test
    public void should_save_incident_when_give_item_values_and_item_values_map_have_register_date()
            throws IOException {
        String uniqueId = "test save incident";
        IncidentService incidentServiceSpy = spy(incidentService);
        when(incidentServiceSpy.generateUniqueId()).thenReturn(uniqueId);
        User user = new User("userName");
        when(PrimeroConfiguration.getCurrentUser()).thenReturn(user);
        ItemValuesMap itemValuesMap = new ItemValuesMap();
        itemValuesMap.addStringItem(REGISTRATION_DATE, "11/11/1111");

        Incident incident = incidentServiceSpy.save(itemValuesMap);
        when(incidentDao.save(any(Incident.class))).thenReturn(incident);

        assertThat(incident.getAge(), is(0));
        assertThat(incident.getUniqueId(), is(uniqueId));
        assertThat(incident.getRegistrationDate(), is(Utils.getRegisterDate("11/11/1111")));
    }

    @Test
    public void
    should_save_incident_when_give_item_values_and_item_values_map_has_not_register_date()
            throws IOException {
        String uniqueId = "test save incident";
        IncidentService incidentServiceSpy = spy(incidentService);
        when(incidentServiceSpy.generateUniqueId()).thenReturn(uniqueId);
        User user = new User("userName");
        Mockito.when(PrimeroConfiguration.getCurrentUser()).thenReturn(user);
        ItemValuesMap itemValuesMap = new ItemValuesMap();

        Incident incident = incidentServiceSpy.save(itemValuesMap);
        when(incidentDao.save(any(Incident.class))).thenReturn(incident);

        assertThat(incident.getAge(), is(0));
        assertThat(incident.getUniqueId(), is(uniqueId));
    }


    @Test
    public void should_update_incident_when_give_item_values() throws Exception {
        ItemValuesMap itemValues = new ItemValuesMap();
        itemValues.addStringItem(INCIDENT_ID, "existedUniqueId");
        itemValues.addNumberItem(AGE, 18);
        itemValues.addStringItem(REGISTRATION_DATE, "25/12/2016");

        Incident expected = new Incident();
        PowerMockito.when(incidentDao.getIncidentByUniqueId("existedUniqueId")).thenReturn
                (expected);

        expected.setContent(new Blob(new String("").getBytes()));

        PowerMockito.when(incidentDao.update(expected)).thenReturn(expected);

        Incident actual = incidentService.update(itemValues);

        verify(incidentDao, times(1)).update(actual);

        assertFalse("Sync status should be false", actual.isSynced());
        assertThat("Age should be 18", actual.getAge(), is(18));
        assertThat("Registration date should be 25/12/2016", actual.getRegistrationDate(), is
                (Utils.getRegisterDate("25/12/2016")));
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

        Assert.assertThat("When call getSearchResult() should return search result depends on " +
                        "search " +
                        "condition",
                incidentService.getSearchResult("uniqueId", "name", 1, 20, new Date(20161108)),
                is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Test
    public void should_get_empty_required_filed_list_when_does_not_exist_in_incident_fields() {
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
