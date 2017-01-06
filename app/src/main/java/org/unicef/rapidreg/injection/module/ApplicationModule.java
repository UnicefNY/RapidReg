package org.unicef.rapidreg.injection.module;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import org.unicef.rapidreg.db.impl.CaseDaoImpl;
import org.unicef.rapidreg.db.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.db.impl.IncidentFormDaoImpl;
import org.unicef.rapidreg.db.impl.TracingDaoImpl;
import org.unicef.rapidreg.db.impl.TracingFormDaoImpl;
import org.unicef.rapidreg.db.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.db.impl.UserDaoImpl;
import org.unicef.rapidreg.injection.ApplicationContext;
import org.unicef.rapidreg.login.LoginServiceImpl;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.network.SyncTracingService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.service.impl.CaseFormServiceImpl;
import org.unicef.rapidreg.service.impl.IncidentFormServiceImpl;
import org.unicef.rapidreg.service.impl.TracingFormServiceImpl;
import org.unicef.rapidreg.service.impl.UserServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {AndroidServiceModule.class})
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
        return new UserServiceImpl(new UserDaoImpl());
    }

    @Provides
    @Singleton
    public CaseService provideCaseService() {
        return new CaseService(new CaseDaoImpl(), new CasePhotoDaoImpl());
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
    public IncidentService provideIncidentService() {
        return new IncidentService();
    }

    @Provides
    @Singleton
    public IncidentFormService provideIncidentFormService() {
        return new IncidentFormServiceImpl(new IncidentFormDaoImpl());
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
    @Singleton
    public SyncService provideSyncService(RecordService
            recordService, CasePhotoService casePhotoService) {
        return new SyncService(casePhotoService, recordService);
    }

    @Provides
    @Singleton
    public SyncTracingService provideSyncTracingService(RecordService recordService,
                                                        TracingPhotoService tracingPhotoService) {
        return new SyncTracingService(recordService, tracingPhotoService);
    }

    @Provides
    @Singleton
    public LoginService provideLoginService(ConnectivityManager connectivityManager,
                                            TelephonyManager telephonyManager,
                                            UserService userService,
                                            AuthService authService) {
        return new LoginServiceImpl(connectivityManager, telephonyManager, userService, authService);
    }
}
