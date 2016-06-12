package org.unicef.rapidreg.widgets.helper;

import android.content.Context;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class WidgetHelperFactory {

    public static WidgetHelper getWidgetHelper(Context context, CaseField field) {
        if (field.isSeperator()) {
            return new SeparatorHelper(context, field);
        }

        if (field.isTickBox()) {
            return new TickBoxHelper(context, field);
        }

        return new GenericHelper(context, field);
    }
}
