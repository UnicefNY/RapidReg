package org.unicef.rapidreg.childcase;

import android.support.v4.app.Fragment;

import org.unicef.rapidreg.R;

public enum CaseFeature {
    LIST(R.string.cases, new CaseListFragment()),
    ADD(R.string.new_case, new CaseRegisterWrapperFragment()),
    EDIT(R.string.edit, null),
    DETAILS(R.string.case_details, new CaseRegisterWrapperFragment()),
    SEARCH(R.string.search, new CaseSearchFragment());

    private int titleId;
    private Fragment fragment;

    CaseFeature(int titleId, Fragment fragment) {
        this.titleId = titleId;
        this.fragment = fragment;

    }

    public int getTitleId() {
        return titleId;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
