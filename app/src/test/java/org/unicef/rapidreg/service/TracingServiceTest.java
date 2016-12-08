package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.db.impl.TracingDaoImpl;
import org.unicef.rapidreg.db.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {UUID.class, PrimeroConfiguration.class} )
public class TracingServiceTest {

    @Mock
    private TracingDaoImpl tracingDao;

    @Mock
    private TracingPhotoDaoImpl tracingPhotoDao;

    @InjectMocks
    TracingService tracingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockStatic(UUID.class);
    }

    @Ignore
    @Test
    public void should_return_all_order_ids_sorted_by_ascending() throws Exception {
        Tracing[] orders = new Tracing[]{new Tracing(1), new Tracing(2), new Tracing(3)};
        List<Tracing> orderList = Arrays.asList(orders);
        when(tracingDao.getAllTracingsOrderByDate(true)).thenReturn(orderList);
        assertThat("When call getAllOrderByDateASC() should return orders sorted by date.", tracingService.getAllOrderByDateASC(), is(Arrays.asList(new Long[]{1L, 2L, 3L})));
    }

    @Ignore
    @Test
    public void should_return_all_order_ids_sorted_by_descending() throws Exception {
        Tracing[] orders = new Tracing[]{new Tracing(3), new Tracing(2), new Tracing(1)};
        List<Tracing> orderList = Arrays.asList(orders);
        when(tracingDao.getAllTracingsOrderByDate(false)).thenReturn(orderList);
        assertThat("When call getAllOrderByDateDES() should return orders sorted by date.", tracingService.getAllOrderByDateDES(), is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Ignore
    @Test
    public void should_return_search_result_when_give_search_conditions() throws Exception {
        String uniqueId = "uniqueId";
        String name = "name";
        int fromAge = 1;
        int toAge = 20;
        Date date = new Date(20161108);

        Tracing[] orders = new Tracing[]{new Tracing(3), new Tracing(2), new Tracing(1)};
        List<Tracing> orderList = Arrays.asList(orders);

        when(tracingDao.getAllTracingsByConditionGroup(any(ConditionGroup.class))).thenReturn(orderList);

        assertThat("When call getSearchResult() should return search result depends on search condition",
                tracingService.getSearchResult(uniqueId, name, fromAge, toAge, date),
                is(Arrays.asList(new Long[]{3L, 2L, 1L})));
    }

    @Ignore
    @Test
    public void should_return_save_tracing_and_store_photos_when_give_item_values_and_photos() throws Exception {

        mockStatic(PrimeroConfiguration.class);

        String uuid = "123test";
        TracingService tracingServiceSpy = spy(tracingService);
        when(tracingServiceSpy.createUniqueId()).thenReturn(uuid);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("userName");
        expect(PrimeroConfiguration.getCurrentUser()).andReturn(user);
        replay(PrimeroConfiguration.class);

        Tracing actual = tracingService.save(new ItemValues(), new ArrayList<String>());

        Mockito.verify(actual).save();
        Mockito.verify(tracingServiceSpy).createUniqueId();

        assertThat("When call save() should return tracing which has same record id", actual.getUniqueId(), is(uuid));
    }


}