package org.unicef.rapidreg.service.cache;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemValuesMap implements Serializable {
    private Map<String, Object> values;

    public ItemValuesMap() {
        values = new HashMap<>();
    }

    public ItemValuesMap(Map<String, Object> values) {
        this.values = values;
    }

    public static ItemValuesMap fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> values = new Gson().fromJson(json, type);
        return new ItemValuesMap(values);
    }

    public static ItemValuesMap generateItemValues(String parentJson) {
        Type parentType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> caseInfo = new Gson().fromJson(parentJson, parentType);
        ItemValuesMap itemValues = new ItemValuesMap(caseInfo);
        return itemValues;
    }

    public void addItem(String itemKey, Object itemValue) {
        values.put(itemKey, itemValue);
    }

    public void addBooleanItem(String itemKey, Boolean value) {
        values.put(itemKey, value);
    }

    public void addStringItem(String itemKey, String value) {
        values.put(itemKey, value);
    }

    public void addNumberItem(String itemKey, Number value) {
        values.put(itemKey, value);
    }


    public Boolean getAsBoolean(String key) {
        if (values.get(key) == null) {
            return Boolean.valueOf(null);
        }
        return Boolean.valueOf(values.get(key).toString());
    }

    public String getAsString(String key) {
        if (values.get(key) == null) {
            return null;
        }
        return (String) values.get(key);
    }

    public Integer getAsInt(String key) {
        if (values.get(key) == null) {
            return null;
        }
        return Integer.valueOf(values.get(key).toString());
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public List<Map<String, Object>> getChildrenAsJsonArray(String childName) {
        if (values.containsKey(childName)) {
            return (List<Map<String, Object>>) values.get(childName);
        }
        return null;
    }

    public void addChild(String childName, Map<String, Object> child) {
        List<Map<String, Object>> children = getChildrenAsJsonArray(childName);
        if (children == null) {
            children = new ArrayList<>();
            addChildren(childName, children);
        }
        children.add(child);
    }

    public int getChildrenSize(String childName) {
        if (getChildrenAsJsonArray(childName) == null) {
            return 0;
        }
        return getChildrenAsJsonArray(childName).size();
    }

    public ItemValuesMap getChildAsItemValues(String childName, int index) {
        List<Map<String, Object>> childrenAsJsonArray = getChildrenAsJsonArray(childName);
        Map<String, Object> child;
        try {
            child = childrenAsJsonArray.get(index);
        } catch (IndexOutOfBoundsException e) {
            child = new HashMap<>();
            childrenAsJsonArray.add(child);
        }
        return new ItemValuesMap(child);
    }

    public void addChildren(String childName, List<Map<String, Object>> children) {
        values.put(childName, children);
    }

    public void addChildrenItemForParent(String childName, int index, String itemKey, Object itemValue) {
        List<Map<String, Object>> children = getChildrenAsJsonArray(childName);
        if (children == null) {
            children = new ArrayList<>();
            addChildren(childName, children);
        }

        Map<String, Object> child;
        try {
            child = children.get(index);
        } catch (IndexOutOfBoundsException e) {
            child = new HashMap<>();
            children.add(child);
        }
        child.put(itemKey, itemValue);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public Map<String, List<Map<String, Object>>> getChildrenAsJsonObject() {
        Map<String, List<Map<String, Object>>> children = new HashMap<>();
        for (Map.Entry<String, Object> element : values.entrySet()) {
            if (element.getValue() instanceof List) {
                children.put(element.getKey(), (List<Map<String, Object>>) element.getValue());
            }
        }
        return children;
    }

    public static JsonArray generateJsonArray(List<String> elements) {
        return generateJsonArray(elements.toArray(new String[0]));
    }

    public static JsonArray generateJsonArray(String... elements) {
        JsonArray array = new JsonArray();
        for (String element : elements) {
            array.add(element);
        }
        return array;
    }

    public static ItemValuesMap fromItemValuesJsonObject(ItemValues itemValues) {
        Map<String, Object> result = new HashMap<>();
        JsonObject values = itemValues.getValues();
        for (Map.Entry<String, JsonElement> element : values.entrySet()) {
            String childName = element.getKey();
            if (element.getValue() instanceof JsonArray) {
                JsonArray childrenArray = element.getValue().getAsJsonArray();
                List<Map<String, Object>> children = new ArrayList<>();
                for (JsonElement child : childrenArray) {
                    Map<String, Object> childItem = new HashMap<>();
                    for (Map.Entry<String, JsonElement> entry : child.getAsJsonObject().entrySet()) {
                        childItem.put(entry.getKey(), entry.getValue().getAsJsonObject().get(entry.getKey()));
                    }
                    children.add(childItem);
                }
                result.put(childName, children);
            } else if (element.getValue() instanceof JsonObject) {
                JsonObject item = element.getValue().getAsJsonObject();
                result.put(childName, item.get(childName));
            }
        }
        return new ItemValuesMap(result);
    }
}
