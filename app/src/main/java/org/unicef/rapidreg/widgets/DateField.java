package org.unicef.rapidreg.widgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;

public class DateField extends TextField implements DatePickerDialog.OnDateSetListener, DialogInterface.OnClickListener, View.OnTouchListener {

    public DateField(Context context) {
        super(context);
    }

    public DateField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
