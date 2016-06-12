package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class SingleTextDialog extends BaseDialog {

    private EditText editText;

    public SingleTextDialog(Context context, CaseField caseField, TextView resultView) {
        super(context, caseField, resultView);
    }

    @Override
    public void initView() {
        editText = new EditText(getContext());
        editText.setText(resultView.getText().toString());
        editText.setSingleLine();
        getBuilder().setView(editText);
    }

    @Override
    public String getResult() {
        return editText.getText().toString();
    }
}
