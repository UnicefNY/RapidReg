package org.unicef.rapidreg.widgets.helper;

import android.content.Context;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;

public class WidgetHelperFactory {

    public static WidgetHelper getWidgetHelper(Context context, CaseField field) {
        String type = field.getType();
        if (Case.TYPE_SEPARATOR.equalsIgnoreCase(type)) {
            return new SeparatorHelper(context, field);
        } else if (Case.TYPE_TICK_BOX.equalsIgnoreCase(type)) {
            return new TickBoxHelper(context, field);
        } else {
            return new GenericHelper(context, field);
        }
    }
}
