package org.unicef.rapidreg.injection.component;

import org.unicef.rapidreg.base.AudioRecorderActivity;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CasePhotoViewActivity;
import org.unicef.rapidreg.injection.PerActivity;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.login.LoginActivity;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoViewActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    
    void inject(BaseActivity baseActivity);

    void inject(RecordActivity recordActivity);

    void inject(CaseActivity caseActivity);

    void inject(TracingActivity tracingActivity);

    void inject(LoginActivity loginActivity);

    void inject(AudioRecorderActivity audioRecorderActivity);

    void inject(TracingPhotoViewActivity tracingPhotoViewActivity);

    void inject(CasePhotoViewActivity casePhotoViewActivity);
}
