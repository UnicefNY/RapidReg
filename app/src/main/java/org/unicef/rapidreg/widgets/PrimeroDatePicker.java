package org.unicef.rapidreg.widgets;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import org.unicef.rapidreg.R;

import java.lang.reflect.Field;

public class PrimeroDatePicker extends DatePicker {

    public PrimeroDatePicker(Context context) {
        super(context);
        changeDividerColor();
    }

    public PrimeroDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        changeDividerColor();
    }

    public PrimeroDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        changeDividerColor();
    }

    private void changeDividerColor() {
        try {
            Class internalRID = Class.forName("com.android.internal.R$id");
            Field month = internalRID.getField("month");
            Field day = internalRID.getField("day");
            Field year = internalRID.getField("year");
            NumberPicker npMonth = (NumberPicker) findViewById(month.getInt(null));
            NumberPicker npDay = (NumberPicker) findViewById(day.getInt(null));
            NumberPicker npYear = (NumberPicker) findViewById(year.getInt(null));
            setDividerColor(npMonth);
            setDividerColor(npDay);
            setDividerColor(npYear);
        } catch (Exception ignored) {
        }
    }

    private void setDividerColor(NumberPicker numberPicker) {
        Field[] numberPickerFields = NumberPicker.class.getDeclaredFields();
        for (Field field : numberPickerFields) {
            if (field.getName().equals("mSelectionDivider")) {
                field.setAccessible(true);
                try {
                    field.set(numberPicker, ContextCompat.getDrawable(getContext(),
                            R.drawable.spinner_date_picker_divider));
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }
}
