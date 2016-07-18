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
    public static Object toJSON(Object object) throws JSONException {
        if (object instanceof Map) {
            JSONObject json = new JSONObject();
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), toJSON(map.get(key)));
            }
            return json;
        } else if (object instanceof Iterable) {
            JSONArray json = new JSONArray();
            for (Object value : ((Iterable) object)) {
                json.put(value);
            }
            return json;
        } else {
            return object;
        }
    }

    public static Map<String, Object> getMap(JsonObject object, String key) throws JSONException {
        return toMap(object.getAsJsonObject(key));
    }

    public static Map<String, Object> toMap(JsonObject object) throws JSONException {
        Map<String, Object> map = new HashMap();
        for (Map.Entry<String, JsonElement> element : object.entrySet()) {
            map.put(element.getKey(), fromJson(element.getValue()));
        }
        return map;
    }

    public static List toList(JsonArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < array.size(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == null) {
            return null;
        }
        if (json instanceof JsonObject) {
            return toMap((JsonObject) json);
        }
        if (json instanceof JsonArray) {
            return toList((JsonArray) json);
        }
        if(json instanceof JsonPrimitive){
            JsonPrimitive result = (JsonPrimitive) json;
            if(result.isBoolean()){
                return result.getAsBoolean();
            }
            if (result.isNumber()){
                return result.getAsNumber().longValue();
            }
            if (result.isString()){
                return result.getAsString();
            }
            return json;
        }
        return json;
    }
}