package org.unicef.rapidreg.injection.component;

import android.app.Application;
import android.content.Context;

import org.unicef.rapidreg.injection.ApplicationContext;
import org.unicef.rapidreg.injection.module.ApplicationModule;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext
    Context context();

    Application application();

    UserService userService();
}
