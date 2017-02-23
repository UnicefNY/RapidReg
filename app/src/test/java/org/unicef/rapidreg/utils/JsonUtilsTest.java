package org.unicef.rapidreg.utils;


import com.google.gson.JsonObject;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonUtilsTest {

    @Test
    public void should_get_null_map_by_json_object() throws Exception {
        JsonObject object = new JsonObject();

        assertThat("Should return null map", JsonUtils.toMap(object), is(Collections.emptyMap()));
    }

    @Test
    public void should_get_map_by_json_object() throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("1", 11L);
        object.addProperty("2", 22L);

        Map<String, Object> map = new HashMap<>();
        map.put("1", 11L);
        map.put("2", 22L);

        Map actual = JsonUtils.toMap(object);

        assertThat("Should return same map size", actual.size(), is(map.size()));
        assertThat(actual.get("1"), is(map.get("1")));
        assertThat(actual.get("2"), is(map.get("2")));
        assertThat(actual, is(map));
    }
}