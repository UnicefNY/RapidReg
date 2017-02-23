package org.unicef.rapidreg.childcase;

import android.support.v4.app.Fragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.childcase.caselist.CaseListFragment;
import org.unicef.rapidreg.childcase.caseregister.CaseMiniFormFragment;
import org.unicef.rapidreg.childcase.caseregister.CaseRegisterWrapperFragment;
import org.unicef.rapidreg.childcase.casesearch.CaseSearchFragment;
import org.unicef.rapidreg.exception.FragmentSwitchException;

public enum CaseFeature implements Feature {
    LIST(R.string.cases, CaseListFragment.class),
    ADD_CP_MINI(R.string.new_cp_case, CaseMiniFormFragment.class),
    ADD_GBV_MINI(R.string.new_gbv_case, CaseMiniFormFragment.class),
    ADD_CP_FULL(R.string.new_cp_case, CaseRegisterWrapperFragment.class),
    ADD_GBV_FULL(R.string.new_gbv_case, CaseRegisterWrapperFragment.class),
    EDIT_MINI(R.string.edit, CaseMiniFormFragment.class),
    EDIT_FULL(R.string.edit, CaseRegisterWrapperFragment.class),
    DETAILS_CP_MINI(R.string.cp_case_details, CaseMiniFormFragment.class),
    DETAILS_CP_FULL(R.string.cp_case_details, CaseRegisterWrapperFragment.class),
    DETAILS_GBV_MINI(R.string.gbv_case_details, CaseMiniFormFragment.class),
    DETAILS_GBV_FULL(R.string.gbv_case_details, CaseRegisterWrapperFragment.class),
    DELETE(R.string.delete, CaseListFragment.class),
    SEARCH(R.string.search, CaseSearchFragment.class),;

    private int titleId;
    private Class clz;

    CaseFeature(int titleId, Class clz) {
        this.titleId = titleId;
        this.clz = clz;
    }

    public int getTitleId() {
        return titleId;
    }

    public Fragment getFragment() throws FragmentSwitchException {
        try {
            return (Fragment) clz.newInstance();
        } catch (InstantiationException e) {
            throw new FragmentSwitchException("The constructor is not accessible", e);
        } catch (IllegalAccessException e) {
            throw new FragmentSwitchException("The method or field is not accessible", e);
        }
    }

    public boolean isEditMode() {
        return this == ADD_CP_MINI || this == ADD_CP_FULL ||
                this == ADD_GBV_MINI || this == ADD_GBV_FULL ||
                this == EDIT_MINI || this == EDIT_FULL;
    }

    public boolean isListMode() {
        return this == LIST;
    }

    public boolean isDetailMode() {
        return this == DETAILS_CP_FULL || this == DETAILS_CP_MINI ||
                this == DETAILS_GBV_FULL || this == DETAILS_GBV_MINI;
    }

    @Override
    public boolean isAddMode() {
        return this == ADD_CP_MINI || this == ADD_CP_FULL || this == ADD_GBV_MINI || this == ADD_GBV_FULL;
    }

    @Override
    public boolean isDeleteMode() {
        return this == DELETE;
    }

    public boolean isCPCase() {
        return this == ADD_CP_MINI || this == ADD_CP_FULL || this == DETAILS_CP_MINI || this == DETAILS_CP_FULL;
    }
}
