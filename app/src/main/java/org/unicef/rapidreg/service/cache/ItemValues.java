package org.unicef.rapidreg.service.cache;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ItemValues implements Serializable{

    private JsonObject values;

    public ItemValues() {
        values = new JsonObject();
    }

    public ItemValues(JsonObject values) {
        this.values = values;
    }

    public static ItemValues fromJson(String json) {
        return new ItemValues(new Gson().fromJson(json, JsonObject.class));
    }

    public void addItem(String itemKey, Object itemValue) {
        addItemToJsonObject(values, itemKey, itemValue);
    }

    public void addBooleanItem(String itemKey, Boolean value) {
        values.addProperty(itemKey, value);
    }

    public void addStringItem(String itemKey, String value) {
        values.addProperty(itemKey, value);
    }

    public void addNumberItem(String itemKey, Number value) {
        values.addProperty(itemKey, value);
    }

    public void addArrayItem(String itemKey, JsonArray array) {
        values.add(itemKey, array);
    }

    public Boolean getAsBoolean(String key) {
        if (values.get(key) == null) {
            return Boolean.valueOf(null);
        }
        return values.get(key).getAsBoolean();
    }

    public String getAsString(String key) {
        if (values.get(key) == null) {
            return null;
        }
        return values.get(key).getAsString();
    }

    public Integer getAsInt(String key) {
        if (values.get(key) == null) {
            return null;
        }
        return values.get(key).getAsInt();
    }

    public JsonObject getValues() {
        return values;
    }

    public JsonArray getChildrenAsJsonArray(String childName) {
        if (values.has(childName)) {
            return values.get(childName).getAsJsonArray();
        }
        return null;
    }

    public void addChild(String childName, JsonObject child) {
        JsonArray children = getChildrenAsJsonArray(childName);
        if (children == null) {
            children = new JsonArray();
            addChildren(childName, children);
        }
        children.add(child);
    }

    public int getChildrenSize(String childName) {
        if (getChildrenAsJsonArray(childName) == null){
            return 0;
        }
        return getChildrenAsJsonArray(childName).size();
    }

    public ItemValues getChildAsItemValues(String childName, int index) {
        JsonArray childrenAsJsonArray = getChildrenAsJsonArray(childName);
        JsonObject child;
        try {
            child = childrenAsJsonArray.get(index).getAsJsonObject();
        } catch (IndexOutOfBoundsException e) {
            child = new JsonObject();
            childrenAsJsonArray.add(child);
        }
        return new ItemValues(child);
    }

    public void addChildren(String key, JsonArray children) {
        values.add(key, children);
    }

    public void addChildrenItemForParent(String childName, int index, String itemKey, Object itemValue) {
        JsonArray children = getChildrenAsJsonArray(childName);
        if (children == null) {
            children = new JsonArray();
            addChildren(childName, children);
        }

        JsonObject child;
        try {
            child = children.get(index).getAsJsonObject();
        } catch (IndexOutOfBoundsException e) {
            child = new JsonObject();
            children.add(child);
        }
        addItemToJsonObject(child, itemKey, itemValue);
    }

    public boolean has(String key) {
        return values.has(key);
    }

    public JsonObject getChildrenAsJsonObject() {
        JsonObject children = new JsonObject();
        for (Map.Entry<String, JsonElement> element : values.entrySet()) {
            if (element.getValue() instanceof JsonArray) {
                children.add(element.getKey(), element.getValue());
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

    public static void addItemToJsonObject(JsonObject jsonObject, String itemKey, Object itemValue) {
        if (itemValue instanceof Number) {
            jsonObject.addProperty(itemKey, (Number) itemValue);
        } else if (itemValue instanceof Boolean) {
            jsonObject.addProperty(itemKey, (Boolean) itemValue);
        } else if (itemValue instanceof String) {
            jsonObject.addProperty(itemKey, (String) itemValue);
        } else if (itemValue instanceof List) {
            jsonObject.add(itemKey, generateJsonArray((List<String>) itemValue));
        }
    }

    public static class CaseProfile {
        public static final String ID_NORMAL_STATE = "_id_normal_state";
        public static final String GENDER_NAME = "_gender_name";
        public static final String REGISTRATION_DATE = "_registration_date";
        public static final String ID = "_id";
    }
}
