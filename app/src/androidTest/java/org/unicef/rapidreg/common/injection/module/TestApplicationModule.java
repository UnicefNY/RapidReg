package org.unicef.rapidreg.common.injection.module;

import android.app.Application;
import android.content.Context;

import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.db.impl.TracingFormDaoImpl;
import org.unicef.rapidreg.db.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.db.impl.UserDaoImpl;
import org.unicef.rapidreg.injection.ApplicationContext;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.network.SyncTracingService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
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

    @Provides
    @Singleton
    public CaseFormService provideCaseFormService() {
        return mock(CaseFormService.class);
    }

    @Provides
    @Singleton
    public RecordService provideRecordService() {
        return mock(RecordService.class);
    }

    @Provides
    @Singleton
    public CaseService provideCaseService(UserService userService) {
        return mock(CaseService.class);
    }

    @Provides
    @Singleton
    public CasePhotoService provideCasePhotoService() {
        return mock(CasePhotoService.class);
    }

    @Provides
    @Singleton
    public TracingService provideTracingService(UserService userService) {
        return mock(TracingService.class);
    }

    @Provides
    @Singleton
    public TracingFormService provideTracingFormService() {
        return mock(TracingFormService.class);
    }

    @Provides
    @Singleton
    public TracingPhotoService provideTracingPhotoService() {
        return mock(TracingPhotoService.class);
    }

    @Provides
    @Singleton
    public AuthService provideAuthService() {
        return mock(AuthService.class);
    }

    @Provides
    public SyncService provideSyncService() {
        return mock(SyncService.class);
    }

    @Provides
    public SyncTracingService provideSyncTracingService() {
        return mock(SyncTracingService.class);
    }
}
