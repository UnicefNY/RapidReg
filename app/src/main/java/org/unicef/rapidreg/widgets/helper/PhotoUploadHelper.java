package org.unicef.rapidreg.widgets.helper;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.widgets.dialog.BaseDialog;
import org.unicef.rapidreg.widgets.dialog.FiledDialogFactory;

public class PhotoUploadHelper extends BaseWidgetHelper{
    public static final String TAG = PhotoUploadHelper.class.getSimpleName();

    private View convertView;

    public PhotoUploadHelper(Context context, CaseField field) {
        super(context, field);
    }

    @Override
    public View getConvertView() {
        if (convertView == null) {
            int resId = resources.getIdentifier(CaseField.TYPE_PHOTO_UPLOAD_LAYOUT, "layout", packageName);
            convertView = inflater.inflate(resId, null);
        }
        return convertView;
    }

    @Override
    public void setValue() {

    }

    @Override
    public void setOnClickListener() {

    }

    private TextView getValueView() {
        return (TextView) getConvertView().findViewById(R.id.value);
    }

    private void disableUneditableField(View view) {
        if (!isEditable()) {
            convertView.setBackgroundResource(R.color.gainsboro);
            view.setEnabled(false);
        }
    }
}
