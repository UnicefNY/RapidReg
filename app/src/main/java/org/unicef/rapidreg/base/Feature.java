package org.unicef.rapidreg.base;


import android.support.v4.app.Fragment;

import org.unicef.rapidreg.exception.FragmentSwitchException;

public interface Feature {
    int getTitleId();

    Fragment getFragment() throws FragmentSwitchException;

    boolean isEditMode();

    boolean isListMode();

    boolean isDetailMode();
}
