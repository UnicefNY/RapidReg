package org.unicef.rapidreg.base.record.recordregister;

import org.unicef.rapidreg.R;

public enum RecordRegisterBtnType {
    CASE_CP("CP", R.drawable.ic_face_white_18dp),
    CASE_GBV("GBV", R.drawable.ic_pan_tool_white_18dp),
    TRACING("Tracing", R.drawable.tracing);

    private final String btnTitle;
    private final int resId;
    RecordRegisterBtnType(String btnTitle, int resId) {
        this.btnTitle = btnTitle;
        this.resId = resId;
    }

    public String getBtnTitle() {
        return btnTitle;
    }

    public int getResId() {
        return resId;
    }
}
