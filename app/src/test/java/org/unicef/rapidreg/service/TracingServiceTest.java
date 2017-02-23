package org.unicef.rapidreg.service;


import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.TracingDao;
import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.unicef.rapidreg.service.RecordService.AGE;
import static org.unicef.rapidreg.service.RecordService.CAREGIVER_NAME;
import static org.unicef.rapidreg.service.RecordService.INQUIRY_DATE;
import static org.unicef.rapidreg.service.RecordService.REGISTRATION_DATE;
import static org.unicef.rapidreg.service.RecordService.RELATION_AGE;
import static org.unicef.rapidreg.service.TracingService.TRACING_ID;
import static org.unicef.rapidreg.service.TracingService.TRACING_PRIMARY_ID;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrimeroAppConfiguration.class})
public class TracingServiceTest {
    private String url = "http://35.61.56.113:8443";
    private User user;

    @Mock
    private TracingDao tracingDao;

    @Mock
    private TracingPhotoDao tracingPhotoDao;

    @InjectMocks
    private TracingService tracingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(PrimeroAppConfiguration.class);

        user = new User("userName");
        when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
        when(PrimeroAppConfiguration.getCurrentUsername()).thenReturn(user.getUsername());

        Mockito.when(PrimeroAppConfiguration.getApiBaseUrl()).thenReturn(TextUtils.lintUrl(url));
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
        when(tracingDao.getAllTracingsOrderByDate(false, "userName", TextUtils.lintUrl(url))).thenReturn(tracingList);
        assertThat(tracingService.getAll(), is(tracingList));
        verify(tracingDao, times(1)).getAllTracingsOrderByDate(false, "userName", TextUtils.lintUrl(url));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_ascending() throws Exception {
        Tracing[] orders = new Tracing[]{new Tracing(1), new Tracing(2), new Tracing(3)};
        List<Tracing> orderList = Arrays.asList(orders);
        when(tracingDao.getAllTracingsOrderByDate(true, PrimeroAppConfiguration.getCurrentUsername(), TextUtils
                .lintUrl(url))).thenReturn
                (orderList);
        assertThat("When call getAllOrderByDateASC() should return orders sorted by date.",
                tracingService.getAllOrderByDateASC(), is(Arrays.asList(new Long[]{1L, 2L, 3L})));
    }

    @Test
    public void should_return_all_order_ids_sorted_by_descending() throws Exception {
        Tracing[] orders = new Tracing[]{new Tracing(3), new Tracing(2), new Tracing(1)};
        List<Tracing> orderList = Arrays.asList(orders);
        when(tracingDao.getAllTracingsOrderByDate(false, PrimeroAppConfiguration.getCurrentUsername(), TextUtils
                .lintUrl(url)))
                .thenReturn(orderList);
        assertThat("When call getAllOrderByDateDES() should return orders sorted by date.",
                tracingService.getAllOrderByDateDES(), is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Test
    public void should_return_search_result_when_give_search_conditions() throws Exception {
        Tracing[] orders = new Tracing[]{new Tracing(3), new Tracing(2), new Tracing(1)};
        List<Tracing> orderList = Arrays.asList(orders);

        when(tracingDao.getAllTracingsByConditionGroup(anyString(), anyString(), any(ConditionGroup.class)))
                .thenReturn(orderList);

        assertThat("When call getCPSearchResult() should return search result depends on search " +
                        "condition",
                tracingService.getSearchResult("uniqueId", "name", 1, 20, new Date(20161108)),
                is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Test
    public void should_save_tracing_when_give_item_values_and_item_values_map_have_register_date()
            throws IOException {
        String url = "http://35.61.65.113:8443";
        Mockito.when(PrimeroAppConfiguration.getApiBaseUrl()).thenReturn(url);

        String uniqueId = "test save tracing";
        ItemValuesMap itemValuesMap = new ItemValuesMap();
        itemValuesMap.addStringItem(RELATION_AGE, "10");
        itemValuesMap.addStringItem(CAREGIVER_NAME, "100");
        itemValuesMap.addStringItem(INQUIRY_DATE, "1/1/2000");
        List<String> photoPaths = Collections.EMPTY_LIST;

        TracingService tracingServiceSpy = spy(tracingService);
        when(tracingServiceSpy.generateUniqueId()).thenReturn(uniqueId);

        Tracing tracingTemp = new Tracing();
        when(tracingDao.save(any(Tracing.class))).thenReturn(tracingTemp);

        Tracing expected = new Tracing();
        when(tracingPhotoDao.save(tracingTemp, photoPaths)).thenReturn(expected);

        Tracing savedTracing = tracingServiceSpy.save(itemValuesMap, photoPaths);
        assertThat(savedTracing.getServerUrl(), is(TextUtils.lintUrl(url)));
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
        when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);

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

    @Test
    public void should_save_when_tracing_id_is_null() throws Exception {
        ItemValuesMap itemValues = new ItemValuesMap();
        itemValues.addStringItem(TRACING_ID, null);

        TracingService tracingServiceSpy = spy(tracingService);
        String uuid = UUID.randomUUID().toString();
        when(tracingServiceSpy.generateUniqueId()).thenReturn(uuid);

        Tracing actual = tracingServiceSpy.saveOrUpdate(itemValues, Collections.emptyList());

        assertThat("Should return same uuid", actual.getUniqueId(), is(uuid));
        verify(tracingDao, times(1)).save(any(Tracing.class));
        verify(tracingPhotoDao, times(1)).save(any(Tracing.class), any());
    }

    @Test
    public void should_update_when_tracing_id_exits() throws Exception {
        ItemValuesMap itemValues = new ItemValuesMap();
        itemValues.addStringItem(TRACING_ID, "tracing_id");
        itemValues.addNumberItem(RELATION_AGE, 18);
        itemValues.addStringItem(INQUIRY_DATE, "25/12/2016");

        TracingService tracingServiceSpy = spy(tracingService);
        Tracing tracing = new Tracing();
        when(tracingDao.getTracingByUniqueId(anyString())).thenReturn(tracing);
        when(tracingDao.update(any(Tracing.class))).thenReturn(tracing);
        when(tracingPhotoDao.update(any(Tracing.class), any())).thenReturn(tracing);
        when(tracingServiceSpy.getShortUUID(anyString())).thenReturn("");

        Tracing actual = tracingServiceSpy.saveOrUpdate(itemValues, Collections.emptyList());
        assertThat("Should return same tracing", actual, is(tracing));
        assertThat("Should return 18 age", actual.getAge(), is(18));
        verify(tracingDao, times(1)).getTracingByUniqueId("tracing_id");
        verify(tracingDao, times(1)).update(tracing);
        verify(tracingPhotoDao, times(1)).update(any(Tracing.class), any());
    }

    private Field makeIncidentField(String name, boolean required) {
        Field field = new Field();
        field.setRequired(required);
        field.setName(name);
        return field;
    }

    @Test
    public void should_get_tracing_by_internal_id() throws Exception {
        Tracing tracing = new Tracing();

        when(tracingDao.getByInternalId(anyString())).thenReturn(tracing);

        assertThat("Should get tracing", tracingService.getByInternalId(""), is(tracing));
        verify(tracingDao, times(1)).getByInternalId("");
    }

    @Test
    public void should_return_true_when_tracing_has_same_rev() throws Exception {
        Tracing tracing = new Tracing();
        tracing.setInternalRev("aa");

        when(tracingDao.getByInternalId(anyString())).thenReturn(tracing);
        assertThat("Should return true", tracingService.hasSameRev("","aa"), is(true));
        verify(tracingDao, times(1)).getByInternalId(anyString());
    }

    @Test
    public void should_return_false_when_tracing_is_null() throws Exception {
        when(tracingDao.getByInternalId(anyString())).thenReturn(null);
        assertThat("Should return false", tracingService.hasSameRev("",""), is(false));
        verify(tracingDao, times(1)).getByInternalId(anyString());
    }

    @Test
    public void should_return_false_when_tracing_rev_different() throws Exception {
        Tracing tracing = new Tracing();
        tracing.setInternalRev("aa");

        when(tracingDao.getByInternalId(anyString())).thenReturn(tracing);
        assertThat("Should return false", tracingService.hasSameRev("","bb"), is(false));
        verify(tracingDao, times(1)).getByInternalId(anyString());
    }

    @Test
    public void should_delete_when_tracing_exists_and_synced_yet() throws Exception {
        long recordId = 123L;

        Tracing deleteTracing = new Tracing(recordId);
        deleteTracing.setSynced(true);
        when(tracingDao.getTracingById(recordId)).thenReturn(deleteTracing);

        Tracing actual = tracingService.deleteByRecordId(recordId);

        verify(tracingDao, times(1)).delete(deleteTracing);
        assertThat(actual, is(deleteTracing));
    }

    @Test
    public void should_not_delete_when_tracing_exists_and_not_synced_yet() throws Exception {
        long recordId = 123L;

        Tracing deleteTracing = new Tracing(recordId);
        deleteTracing.setSynced(false);
        when(tracingDao.getTracingById(recordId)).thenReturn(deleteTracing);

        Tracing actual = tracingService.deleteByRecordId(recordId);

        verify(tracingDao, never()).delete(deleteTracing);
        assertNull(actual);
    }
}