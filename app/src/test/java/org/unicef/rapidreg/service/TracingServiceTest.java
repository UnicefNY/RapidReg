package org.unicef.rapidreg.service;


import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.db.TracingDao;
import org.unicef.rapidreg.db.TracingPhotoDao;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.unicef.rapidreg.service.RecordService.CAREGIVER_NAME;
import static org.unicef.rapidreg.service.RecordService.INQUIRY_DATE;
import static org.unicef.rapidreg.service.RecordService.RELATION_AGE;
import static org.unicef.rapidreg.service.TracingService.TRACING_ID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroConfiguration.class})
public class TracingServiceTest {

    @Mock
    private TracingDao tracingDao;

    @Mock
    private TracingPhotoDao tracingPhotoDao;

    @InjectMocks
    private TracingService tracingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(PrimeroConfiguration.class);
    }

    @Test
    public void should_return_tracing_by_tracing_id() {
        long tracingId = 1L;
        Tracing tracing = mock(Tracing.class);
        when(tracingDao.getTracingById(tracingId)).thenReturn(tracing);
        assertThat(tracingService.getById(tracingId), is(tracing));
        verify(tracingDao, times(1)).getTracingById(tracingId);
    }

    @Test
    public void should_return_tracing_by_uniqueId() {
        String uniqueId = "uniqueId";
        Tracing tracing = mock(Tracing.class);
        when(tracingDao.getTracingByUniqueId(uniqueId)).thenReturn(tracing);
        assertThat(tracingService.getByUniqueId(uniqueId), is(tracing));
        verify(tracingDao, times(1)).getTracingByUniqueId(uniqueId);
    }

    @Test
    public void should_return_tracing_list() {
        Tracing tracing = new Tracing();
        List<Tracing> tracingList = new ArrayList<Tracing>();
        tracingList.add(tracing);
        when(tracingDao.getAllTracingsOrderByDate(false)).thenReturn(tracingList);
        assertThat(tracingService.getAll(), is(tracingList));
    }

    @Test
    public void should_return_all_ids() {
        Long l = 1L;
        List<Long> list = new ArrayList<>();
        list.add(l);
        when(tracingDao.getAllIds()).thenReturn(list);
        assertThat(tracingService.getAllIds(), is(list));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_ascending() throws Exception {
        Tracing[] orders = new Tracing[]{new Tracing(1), new Tracing(2), new Tracing(3)};
        List<Tracing> orderList = Arrays.asList(orders);
        when(tracingDao.getAllTracingsOrderByDate(true)).thenReturn(orderList);
        assertThat("When call getAllOrderByDateASC() should return orders sorted by date.",
                tracingService.getAllOrderByDateASC(), is(Arrays.asList(new Long[]{1L, 2L, 3L})));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_descending() throws Exception {
        Tracing[] orders = new Tracing[]{new Tracing(3), new Tracing(2), new Tracing(1)};
        List<Tracing> orderList = Arrays.asList(orders);
        when(tracingDao.getAllTracingsOrderByDate(false)).thenReturn(orderList);
        assertThat("When call getAllOrderByDateDES() should return orders sorted by date.",
                tracingService.getAllOrderByDateDES(), is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Test
    public void should_return_search_result_when_give_search_conditions() throws Exception {

        Tracing[] orders = new Tracing[]{new Tracing(3), new Tracing(2), new Tracing(1)};
        List<Tracing> orderList = Arrays.asList(orders);

        when(tracingDao.getAllTracingsByConditionGroup(any(ConditionGroup.class))).thenReturn
                (orderList);

        assertThat("When call getSearchResult() should return search result depends on search " +
                        "condition",
                tracingService.getSearchResult("uniqueId", "name", 1, 20, new Date(20161108)),
                is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Test
    public void should_save_tracing_when_give_item_values_and_item_values_map_have_register_date()
            throws IOException {
        String uniqueId = "test save tracing";
        ItemValuesMap itemValuesMap = new ItemValuesMap();
        itemValuesMap.addStringItem(RELATION_AGE, "10");
        itemValuesMap.addStringItem(CAREGIVER_NAME, "100");
        itemValuesMap.addStringItem(INQUIRY_DATE, "1/1/2000");
        List<String> photoPaths = Collections.EMPTY_LIST;
        User user = new User("userName");
        when(PrimeroConfiguration.getCurrentUser()).thenReturn(user);

        TracingService tracingServiceSpy = spy(tracingService);
        when(tracingServiceSpy.generateUniqueId()).thenReturn(uniqueId);

        Tracing tracingTemp = new Tracing();
        when(tracingDao.save(any(Tracing.class))).thenReturn(tracingTemp);

        Tracing expected = new Tracing();
        when(tracingPhotoDao.save(tracingTemp, photoPaths)).thenReturn(expected);

        tracingServiceSpy.save(itemValuesMap, photoPaths);
        verify(tracingDao, times(1)).save(any(Tracing.class));
        verify(tracingPhotoDao, times(1)).save(any(Tracing.class), anyList());
    }

    @Test
    public void should_update_tracing_when_give_item_values() throws Exception {
        String uniqueId = "tracing";

        ItemValuesMap itemValues = new ItemValuesMap();
        itemValues.addStringItem(TRACING_ID, uniqueId);
        itemValues.addStringItem(RELATION_AGE, "18");
        itemValues.addStringItem(CAREGIVER_NAME, "100");
        itemValues.addStringItem(INQUIRY_DATE, "25/12/2016");

        List<String> photoBitPaths = Collections.EMPTY_LIST;

        User user = new User("userName");
        when(PrimeroConfiguration.getCurrentUser()).thenReturn(user);

        Tracing tracing = new Tracing();
        when(tracingDao.getTracingByUniqueId(anyString())).thenReturn(tracing);

        tracing.setUniqueId(uniqueId);

        Tracing tracingTemp = new Tracing();
        when(tracingDao.update(any(Tracing.class))).thenReturn(tracingTemp);

        Tracing expected = new Tracing();
        when(tracingPhotoDao.update(tracingTemp, photoBitPaths)).thenReturn(expected);

        tracingService.update(itemValues, photoBitPaths);

        verify(tracingDao, times(1)).update(any(Tracing.class));
        verify(tracingPhotoDao, times(1)).update(any(Tracing.class), anyList());

    }

    @Test
    public void should_get_required_filed_list_when_exist_in_incident_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeIncidentField("age", true));
        fields.add(makeIncidentField("sex", true));
        fields.add(makeIncidentField("name", false));

        List<String> requiredFiledNames = tracingService.fetchRequiredFiledNames(fields);
        assertThat(requiredFiledNames, hasSize(2));
        assertThat(requiredFiledNames, containsInAnyOrder("sex", "age"));
    }


    @Test
    public void should_get_empty_required_filed_list_when_does_not_exist_in_incident_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeIncidentField("age", false));
        fields.add(makeIncidentField("sex", false));
        fields.add(makeIncidentField("name", false));

        List<String> requiredFiledNames = tracingService.fetchRequiredFiledNames(fields);
        assertThat(requiredFiledNames, hasSize(0));
    }

    private Field makeIncidentField(String name, boolean required) {
        Field field = new Field();
        field.setRequired(required);
        field.setName(name);
        return field;
    }

}