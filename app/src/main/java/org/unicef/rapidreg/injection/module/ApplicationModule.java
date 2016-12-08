package org.unicef.rapidreg.injection.module;

import android.app.Application;
import android.content.Context;

import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.db.impl.TracingDaoImpl;
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
import org.unicef.rapidreg.service.impl.CaseFormServiceImpl;
import org.unicef.rapidreg.service.impl.TracingFormServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
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

    @Provides
    @Singleton
    public UserService provideUserService() {
        return new UserService(new UserDaoImpl());
    }

    @Provides
    @Singleton
    public CaseService provideCaseService(UserService userService) {
        return new CaseService(userService, new CaseDaoImpl());
    }

    @Provides
    @Singleton
    public CaseFormService provideCaseFormService() {
        return new CaseFormServiceImpl(new CaseFormDaoImpl());
    }

    @Provides
    @Singleton
    public CasePhotoService provideCasePhotoService() {
        return new CasePhotoService(new CasePhotoDaoImpl());
    }

    @Provides
    @Singleton
    public RecordService provideRecordService() {
        return new RecordService();
    }

    @Provides
    @Singleton
    public TracingService provideTracingService() {
        return new TracingService(new TracingDaoImpl(), new TracingPhotoDaoImpl());
    }

    @Provides
    @Singleton
    public TracingFormService provideTracingFormService() {
        return new TracingFormServiceImpl(new TracingFormDaoImpl());
    }

    @Provides
    @Singleton
    public TracingPhotoService provideTracingPhotoService() {
        return new TracingPhotoService(new TracingPhotoDaoImpl());
    }

    @Provides
    @Singleton
    public AuthService provideAuthService() {
        return new AuthService();
    }

    @Provides
    public SyncService provideSyncService(@ApplicationContext Context context, CasePhotoService casePhotoService) {
        return new SyncService(context, casePhotoService);
    }

    @Provides
    public SyncTracingService provideSyncTracingService(@ApplicationContext Context context, TracingPhotoService
            tracingPhotoService) {
        return new SyncTracingService(context, tracingPhotoService);
    }
}
