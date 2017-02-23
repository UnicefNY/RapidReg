package org.unicef.rapidreg.utils;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static Map<String, Object> toMap(JsonObject object) {
        Map<String, Object> map = new HashMap();
        for (Map.Entry<String, JsonElement> element : object.entrySet()) {
            map.put(element.getKey(), fromJson(element.getValue()));
        }
        return map;
    }

    private static List toList(JsonArray array) {
        List list = new ArrayList();
        for (int i = 0; i < array.size(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) {
        if (json == null) {
            return null;
        }
        if (json instanceof JsonObject) {
            return toMap((JsonObject) json);
        }
        if (json instanceof JsonArray) {
            return toList((JsonArray) json);
        }
        if (json instanceof JsonPrimitive) {
            JsonPrimitive result = (JsonPrimitive) json;
            if (result.isBoolean()) {
                return result.getAsBoolean();
            }
            if (result.isNumber()) {
                double doubleValue = result.getAsNumber().doubleValue();
                if (Math.ceil(doubleValue) == doubleValue) {
                    return result.getAsNumber().longValue();
                }
                return result.getAsNumber();
            }
            if (result.isString()) {
                return result.getAsString();
            }
            return json;
        }
        return json;
    }
}