package org.unicef.rapidreg.base;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.Iterator;
import java.util.List;

public class RecordRegisterPresenter extends MvpBasePresenter<RecordRegisterView> {
    private int type;

    public RecordRegisterPresenter(int type) {
        this.type = type;
    }

    public void initContext(Context context, int position) {
        if (isViewAttached()) {
            RecordForm form = null;

            if (type == RecordModel.CASE) {
                form = CaseFormService.getInstance().getCurrentForm();
            } else if (type == RecordModel.TRACING) {
                form = TracingFormService.getInstance().getCurrentForm();
            }

            if (form != null) {
                List<Field> fields = form.getSections().get(position).getFields();
                RecordRegisterAdapter adapter = new RecordRegisterAdapter(context, removeSeparatorFields(fields),
                        new ItemValuesMap(), false);
                getView().initView(adapter);
            }
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
}
