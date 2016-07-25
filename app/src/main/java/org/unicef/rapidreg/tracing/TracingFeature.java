package org.unicef.rapidreg.tracing;

import android.support.v4.app.Fragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.exception.FragmentSwitchException;

public enum TracingFeature implements Feature {
    LIST(R.string.tracing, TracingListFragment.class),
    ADD(R.string.new_tracing, TracingMiniFormFragment.class),
    EDIT(R.string.edit, TracingMiniFormFragment.class),
    DETAILS_MINI(R.string.tracing_details, TracingMiniFormFragment.class),
    DETAILS_FULL(R.string.tracing_details, TracingRegisterWrapperFragment.class),
    SEARCH(R.string.search, TracingSearchFragment.class);

    private int titleId;
    private Class clz;

    TracingFeature(int titleId, Class clz) {
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
        return this == ADD || this == EDIT;
    }

    public boolean isListMode() {
        return this == LIST;
    }

    public boolean isDetailMode() {
        return this == DETAILS_FULL || this == DETAILS_MINI;
    }
}
