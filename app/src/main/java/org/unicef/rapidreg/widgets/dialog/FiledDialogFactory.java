package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

public class FiledDialogFactory {
    public static BaseDialog createDialog(Field.FieldType fieldType, Context context,
                                          Field field, ItemValuesMap itemValues, TextView resultView,
                                          ViewSwitcher viewSwitcher) throws DialogException {
        try {
            return fieldType.getClz().getConstructor(Context.class, Field.class,
                    ItemValuesMap.class, TextView.class, ViewSwitcher.class)
                    .newInstance(context, field, itemValues, resultView, viewSwitcher);
        } catch (Exception e) {
            throw new DialogException(String.format("fieldType: %s", fieldType), e);
        }
    }
}
