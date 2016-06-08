package org.unicef.rapidreg.childcase.fielddialog;

import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import org.unicef.rapidreg.forms.childcase.CaseField;

public class DateDialog extends BaseDialog {

    private String result;
    private DatePicker datePicker;

    public DateDialog(Context context, CaseField caseField, TextView resultView) {
        super(context, caseField, resultView);
    }

    @Override
    public void initView() {
        result = resultView.getText().toString().trim();

        datePicker = new DatePicker(getContext());
        datePicker.setCalendarViewShown(false);
        if (!"".equals(result)) {
            String[] date = result.split("/");
            int year = Integer.valueOf(date[2]);
            int month = Integer.valueOf(date[0]) - 1;
            int day = Integer.valueOf(date[1]);
            datePicker.updateDate(year, month, day);
        }
        getBuilder().setView(datePicker);
    }

    @Override
    public String getResult() {
        return String.format("%s/%s/%s", datePicker.getMonth() + 1,
                datePicker.getDayOfMonth(), datePicker.getYear());
    }
}
