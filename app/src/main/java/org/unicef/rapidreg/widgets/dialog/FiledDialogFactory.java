package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.cache.ItemValues;

public class FiledDialogFactory {
    public static BaseDialog createDialog(CaseField.FieldType fieldType, Context context,
                                          CaseField caseField, ItemValues itemValues, TextView resultView,
                                          ViewSwitcher viewSwitcher) throws DialogException {
        try {
            return fieldType.getClz().getConstructor(Context.class, CaseField.class,
                    ItemValues.class, TextView.class, ViewSwitcher.class)
                    .newInstance(context, caseField, itemValues, resultView, viewSwitcher);
        } catch (Exception e) {
            throw new DialogException(String.format("fieldType: %s", fieldType), e);
        }
    }
}
