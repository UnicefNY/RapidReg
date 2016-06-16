package org.unicef.rapidreg.widgets.helper;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;

public abstract class BaseWidgetHelper implements WidgetHelper {
    protected final Context context;
    protected final CaseField field;
    protected LayoutInflater inflater;
    protected Resources resources;
    protected String packageName;

    public BaseWidgetHelper(Context context, CaseField field) {
        this.context = context;
        this.field = field;

        initRes();
    }

    //TODO: need to get display name according to the current system language
    protected String getLabel() {
        return field.getDisplayName().get("en");
    }

    protected TextView getLabelView() {
        return (TextView) getConvertView().findViewById(R.id.label);
    }

    protected boolean isEditable() {
       return field.isEditable();
    }

    private void initRes() {
        inflater = LayoutInflater.from(context);
        resources = context.getResources();
        packageName = context.getPackageName();
    }
}
