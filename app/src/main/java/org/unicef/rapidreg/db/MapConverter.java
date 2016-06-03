package org.unicef.rapidreg.db;


import android.util.Log;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.unicef.rapidreg.utils.MapUtils;

import java.util.Map;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class MapConverter extends TypeConverter<String, Map> {

    @Override
    public String getDBValue(Map model) {
        Log.d("MapConverter", model.toString());
        return model == null ? null : model.toString();
    }

    @Override
    public Map getModelValue(String data) {
        return MapUtils.convert(data);
    }
}
