package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
        editText.setLines(20);
        editText.setGravity(Gravity.TOP);
        getBuilder().setView(editText);
    }

    @Override
    public String getResult() {
        return editText.getText().toString();
    }
}
