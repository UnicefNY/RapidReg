package org.unicef.rapidreg.childcase.fielddialog;

import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class MultipleTextDialog extends BaseDialog {

    private EditText editText;

    public MultipleTextDialog(Context context, CaseField caseField, TextView resultView) {
        super(context, caseField, resultView);
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
