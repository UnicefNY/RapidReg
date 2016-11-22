package org.unicef.rapidreg.common.injection.module;

import android.app.Application;
import android.content.Context;

import org.unicef.rapidreg.injection.ApplicationContext;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.powermock.api.mockito.PowerMockito.mock;

@Module
public class TestApplicationModule {

    private final Application application;

    public TestApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    public Application provideApplication() {
        return application;
    }

    @Provides
    @ApplicationContext
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public UserService provideUserService() {
        return mock(UserService.class);
    }
}