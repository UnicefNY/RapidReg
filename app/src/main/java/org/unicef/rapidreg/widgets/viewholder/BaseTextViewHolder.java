package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.SubformCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseTextViewHolder extends BaseViewHolder<CaseField> {
    public BaseTextViewHolder(Context context, View itemView) {
        super(context, itemView);
    }

    protected List<Map<String, String>> getValues(CaseField field) {
        List<Map<String, String>> values = SubformCache.get(field.getParent()) == null ?
                new ArrayList<Map<String, String>>() : SubformCache.get(field.getParent());

        Map<String, String> value;
        try {
            value = values.get(field.getIndex());
            value.put(field.getDisplayName().get("en"), getResult());
            values.set(field.getIndex(), value);
        } catch (IndexOutOfBoundsException e) {
            value = new HashMap<>();
            value.put(field.getDisplayName().get("en"), getResult());
            values.add(field.getIndex(), value);
        }

        return values;
    }

    protected abstract String getResult();
}