package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.db.impl.TracingDaoImpl;
import org.unicef.rapidreg.db.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.User;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UUID.class, PrimeroConfiguration.class, TracingService.class})
public class TracingServiceTest {

    @Mock
    private TracingDaoImpl tracingDao;

    @Mock
    private TracingPhotoDaoImpl tracingPhotoDao;

    private TracingService tracingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        tracingService = new TracingService(tracingDao, tracingPhotoDao);

        PowerMockito.mockStatic(PrimeroConfiguration.class);
        User user = new User("primero");
        when(PrimeroConfiguration.getCurrentUser()).thenReturn(user);
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
}