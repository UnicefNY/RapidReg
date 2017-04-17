package org.unicef.rapidreg.widgets.dialog;

import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.PrimeroDatePicker;

import java.util.Calendar;

public class DateDialog extends BaseDialog {
    private String result;
    private DatePicker datePicker;

    public DateDialog(Context context, Field field, ItemValuesMap itemValues, TextView resultView, ViewSwitcher viewSwitcher) {
        super(context, field, itemValues, resultView, viewSwitcher);
        result = resultView.getText().toString().trim();
    }

    @Override
    public void initView() {
        datePicker = new PrimeroDatePicker(getContext());
        datePicker.setCalendarViewShown(false);
        if (!"".equals(result)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.getRegisterDateByYyyyMmDd(result));
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar
                    .DAY_OF_MONTH));
        }
        getBuilder().setView(datePicker);
    }

    @Override
    public String getResult() {
        int month = datePicker.getMonth() + 1;
        int dayOfMonth = datePicker.getDayOfMonth();
        return String.format("%s/%s/%s", datePicker.getYear(), month < 10 ? "0" + month : month,
                dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
    }
}
