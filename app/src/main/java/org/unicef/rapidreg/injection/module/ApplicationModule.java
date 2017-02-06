package org.unicef.rapidreg.injection.module;

import android.app.Application;
import android.content.Context;

import org.unicef.rapidreg.injection.ApplicationContext;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ApplicationServiceModule.class})
public class ApplicationModule {
    protected Application application;

    public ApplicationModule(Application application) {
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
}
