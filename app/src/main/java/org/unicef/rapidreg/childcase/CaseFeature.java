package org.unicef.rapidreg.childcase;

import android.support.v4.app.Fragment;

import org.unicef.rapidreg.R;

public enum CaseFeature {
    LIST(R.string.cases, CaseListFragment.class),
    ADD(R.string.new_case, CaseRegisterWrapperFragment.class),
    EDIT(R.string.edit, null),
    DETAILS(R.string.case_details, CaseRegisterWrapperFragment.class),
    SEARCH(R.string.search, CaseSearchFragment.class);

    private int titleId;
    private Class clz;


    CaseFeature(int titleId, Class clz) {
        this.titleId = titleId;
        this.clz = clz;
    }

    public int getTitleId() {
        return titleId;
    }

    public Fragment getFragment() throws IllegalAccessException, InstantiationException {
        return (Fragment) clz.newInstance();
    }
}
