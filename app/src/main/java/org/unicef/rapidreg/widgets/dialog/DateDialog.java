package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.PrimeroDatePicker;

import java.util.Calendar;

public class DateDialog extends BaseDialog {
    private String result;
    private DatePicker datePicker;

    public DateDialog(Context context, Field field, ItemValuesMap itemValues, TextView
            resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        datePicker = new PrimeroDatePicker(getContext());
        datePicker.setCalendarViewShown(false);
        if (!"".equals(result)) {
            String[] date = result.split("/");
            int year = Integer.valueOf(date[2]);
            int month = Integer.valueOf(date[1]) - 1;
            int day = Integer.valueOf(date[0]);
            datePicker.updateDate(year, month, day);
        }
        getBuilder().setView(datePicker);
    }

    @Override
    public String verifyResult() {
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        if (datePicker.getYear() > year) {
            return context.getResources().getString(R.string.invalid_date_msg);
        }
        if (datePicker.getMonth() > month) {
            return context.getResources().getString(R.string.invalid_date_msg);
        }
        if (datePicker.getDayOfMonth() > dayOfMonth) {
            return context.getResources().getString(R.string.invalid_date_msg);
        }
        return "";
    }

    @Override
    public String getResult() {
        return String.format("%s/%s/%s", datePicker.getDayOfMonth(), datePicker.getMonth() + 1,
                datePicker.getYear());
    }
}
