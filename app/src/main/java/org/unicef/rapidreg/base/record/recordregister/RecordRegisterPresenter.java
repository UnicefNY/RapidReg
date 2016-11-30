package org.unicef.rapidreg.base.record.recordregister;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.Iterator;
import java.util.List;

public abstract class RecordRegisterPresenter extends MvpBasePresenter<RecordRegisterView> {



    public void initContext(Context context, List<Field> fields, boolean isMiniForm) {
        if (isViewAttached()) {
            RecordRegisterAdapter adapter = new RecordRegisterAdapter(context, removeSeparatorFields(fields),
                    new ItemValuesMap(), isMiniForm);
            getView().initView(adapter);
        }
    }

    private List<Field> removeSeparatorFields(List<Field> fields) {
        Iterator<Field> iterator = fields.iterator();

        while (iterator.hasNext()) {
            Field field = iterator.next();
            if (field.isSeparator()) {
                iterator.remove();
            }
        }

        return fields;
    }

    public abstract RecordForm getCurrentForm();
}
