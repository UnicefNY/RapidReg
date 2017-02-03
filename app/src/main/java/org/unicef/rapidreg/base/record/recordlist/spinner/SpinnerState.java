package org.unicef.rapidreg.base.record.recordlist.spinner;

import org.unicef.rapidreg.R;

public enum SpinnerState {
    AGE_ASC(R.drawable.age_up, "Age ascending order", "Age"),
    AGE_DES(R.drawable.age_down, "Age descending order", "Age"),
    REG_DATE_ASC(R.drawable.date_up, "Registration date ascending order", "Registration date"),
    REG_DATE_DES(R.drawable.date_down, "Registration date descending order", "Registration date"),
    INQUIRY_DATE_ASC(R.drawable.date_up, "Date of inquiry ascending order", "Date of inquiry"),
    INQUIRY_DATE_DES(R.drawable.date_down, "Date of inquiry descending order", "Date of inquiry"),
    INTERVIEW_DATE_ASC(R.drawable.date_up, "Interview Date ascending order", "Date of interview"),
    INTERVIEW_DATE_DES(R.drawable.date_down, "Interview Date descending order", "Date of interview");

    private int resId;
    private String longName;

    private String shortName;

    SpinnerState(int resId, String longName, String shortName) {
        this.resId = resId;
        this.longName = longName;
        this.shortName = shortName;
    }

    public int getResId() {
        return resId;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }
}