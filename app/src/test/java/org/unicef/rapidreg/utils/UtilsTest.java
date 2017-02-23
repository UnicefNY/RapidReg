package org.unicef.rapidreg.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Toast.class)
public class UtilsTest {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Toast.class);
    }

    @Test
    public void should_convert_list_to_string() {
        List<String> items = new ArrayList<>();
        items.add("Language1");
        items.add("Language2");
        String result = Utils.toStringResult(items);
        assertThat(result, is("Language1, Language2"));

        items = null;
        result = Utils.toStringResult(items);
        assertThat(result, is(""));

        items = new ArrayList<>();
        result = Utils.toStringResult(items);
        assertThat(result, is(""));
    }

    @Test
    public void should_convert_string_to_map() {
        String mapString = "{a=1, b=2}";
        Map<String, String> map = Utils.convert(mapString);

        assertThat(map.size(), is(2));
        assertThat(map.get("a"), is("1"));
        assertThat(map.get("b"), is("2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_invalid_string() {
        String mapString = "1234";
        Utils.convert(mapString);
    }

    @Test
    public void should_get_date_when_get_registration_data_string() throws Exception {
        String registrationDateString = "23/02/2017";
        Date date = new Date(new SimpleDateFormat("dd/MM/yyyy").parse(registrationDateString).getTime());

        assertThat("Should return same date", Utils.getRegisterDate(registrationDateString), is(date));
    }

    @Test
    public void should_throw_exception_when_data_string_format_error() throws Exception {
        String str = "aa";

        Date date = new Date(System.currentTimeMillis());
        Date actual = Utils.getRegisterDate(str);

        assertThat("Should return same day", actual.getDay(), is(date.getDay()));
        assertThat("Should return same month", actual.getMonth(), is(date.getMonth()));
        assertThat("Should return same year", actual.getYear(), is(date.getYear()));
    }

    @Test
    public void should_show_message_by_toast_when_given_res_id() throws Exception {
        Context context = mock(Context.class);
        ViewGroup group = mock(ViewGroup.class);
        Toast toast = mock(Toast.class);
        TextView textView = mock(TextView.class);
        Resources resources = mock(Resources.class);

        when(Toast.makeText(context, 0, Toast.LENGTH_SHORT)).thenReturn(toast);
        when(toast.getView()).thenReturn(group);
        when(group.getChildAt(anyInt())).thenReturn(textView);
        when(context.getResources()).thenReturn(resources);
        when(resources.getDimension(anyInt())).thenReturn(0f);

        Utils.showMessageByToast(context, 0, Toast.LENGTH_SHORT);

        verify(toast, times(1)).getView();
        verify(group, times(1)).getChildAt(anyInt());
        verify(context, times(1)).getResources();
        verify(resources, times(1)).getDimension(anyInt());
        verify(toast, times(1)).show();
        verify(textView, times(1)).setTextSize(anyFloat());
    }

    @Test
    public void should_show_message_by_toast_when_given_message() throws Exception {
        Context context = mock(Context.class);
        ViewGroup group = mock(ViewGroup.class);
        Toast toast = mock(Toast.class);
        TextView textView = mock(TextView.class);
        Resources resources = mock(Resources.class);

        when(Toast.makeText(context, "", Toast.LENGTH_SHORT)).thenReturn(toast);
        when(toast.getView()).thenReturn(group);
        when(group.getChildAt(anyInt())).thenReturn(textView);
        when(context.getResources()).thenReturn(resources);
        when(resources.getDimension(anyInt())).thenReturn(0f);

        Utils.showMessageByToast(context, "", Toast.LENGTH_SHORT);

        verify(toast, times(1)).getView();
        verify(group, times(1)).getChildAt(anyInt());
        verify(context, times(1)).getResources();
        verify(resources, times(1)).getDimension(anyInt());
        verify(toast, times(1)).show();
        verify(textView, times(1)).setTextSize(anyFloat());
    }
}
