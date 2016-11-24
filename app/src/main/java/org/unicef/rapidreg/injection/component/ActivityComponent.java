package org.unicef.rapidreg.injection.component;

import android.app.Activity;

import org.unicef.rapidreg.base.AudioRecorderActivity;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.childcase.CasePhotoViewActivity;
import org.unicef.rapidreg.injection.PerActivity;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.login.LoginActivity;
import org.unicef.rapidreg.tracing.TracingPhotoViewActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    
    void inject(BaseActivity baseActivity);

    void inject(LoginActivity loginActivity);

    void inject(AudioRecorderActivity audioRecorderActivity);

    void inject(TracingPhotoViewActivity tracingPhotoViewActivity);

    void inject(CasePhotoViewActivity casePhotoViewActivity);
}
