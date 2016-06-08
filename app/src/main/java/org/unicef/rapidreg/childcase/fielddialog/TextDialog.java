package org.unicef.rapidreg.childcase.fielddialog;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class TextDialog extends BaseDialog {

    private EditText editText;

    public TextDialog(Context context, CaseField caseField, TextView resultView) {
        super(context, caseField, resultView);
    }

    @Override
    public void initView() {
        editText = new EditText(getContext());
        editText.setText(resultView.getText().toString());
        getBuilder().setView(editText);
    }

    @Override
    public String getResult() {
        return editText.getText().toString();
    }
}
