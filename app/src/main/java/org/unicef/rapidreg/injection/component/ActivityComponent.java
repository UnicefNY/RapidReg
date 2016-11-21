package org.unicef.rapidreg.injection.component;

import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.injection.PerActivity;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.login.LoginActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity baseActivity);

    void inject(LoginActivity loginActivity);
}
