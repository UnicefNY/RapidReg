package org.unicef.rapidreg.widgets.helper;

import android.content.Context;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class WidgetHelperFactory {

    public static WidgetHelper getWidgetHelper(Context context, CaseField field) {
        String type = field.getType();
        if (CaseField.TYPE_SEPARATOR.equalsIgnoreCase(type)) {
            return new SeparatorHelper(context, field);
        } else if (CaseField.TYPE_TICK_BOX.equalsIgnoreCase(type)) {
            return new TickBoxHelper(context, field);
        } else {
            return new GenericHelper(context, field);
        }
    }
}
