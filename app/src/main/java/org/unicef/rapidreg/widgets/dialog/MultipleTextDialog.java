package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

public class MultipleTextDialog extends BaseDialog {

    private EditText editText;

    public MultipleTextDialog(Context context, Field field, ItemValuesMap itemValues, TextView resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
    }

    @Override
    public void initView() {
        editText = new EditText(getContext());
        editText.setText(resultView.getText().toString());
        editText.setPadding(20, 10, 20, 10);
        editText.setLines(15);
        editText.setGravity(Gravity.TOP);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            editText.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_view_shape));
        }
        getBuilder().setView(editText);
    }

    @Override
    public String getResult() {
        return editText.getText().toString();
    }
}
