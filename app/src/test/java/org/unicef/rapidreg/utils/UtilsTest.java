package org.unicef.rapidreg.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UtilsTest {

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
}
