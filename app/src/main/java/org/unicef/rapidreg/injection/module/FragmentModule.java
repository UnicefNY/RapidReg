package org.unicef.rapidreg.injection.module;

import android.content.Context;
import android.support.v4.app.Fragment;

import org.unicef.rapidreg.injection.ActivityContext;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    private Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public Fragment provideFragment() {
        return fragment;
    }

    @ActivityContext
    @Provides
    public Context provideContext() {
        return fragment.getActivity();
    }
}
