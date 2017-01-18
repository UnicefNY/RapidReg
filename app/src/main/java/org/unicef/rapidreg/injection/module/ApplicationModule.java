package org.unicef.rapidreg.injection.module;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import org.unicef.rapidreg.injection.ApplicationContext;
import org.unicef.rapidreg.login.LoginServiceImpl;
import org.unicef.rapidreg.repository.CaseDao;
import org.unicef.rapidreg.repository.CaseFormDao;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.repository.TracingDao;
import org.unicef.rapidreg.repository.TracingFormDao;
import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.repository.impl.CaseDaoImpl;
import org.unicef.rapidreg.repository.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.repository.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.repository.impl.IncidentFormDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingFormDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.repository.impl.UserDaoImpl;
import org.unicef.rapidreg.service.AuthService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.SyncCaseService;
import org.unicef.rapidreg.service.SyncTracingService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.service.impl.AuthServiceImpl;
import org.unicef.rapidreg.service.impl.CaseFormServiceImpl;
import org.unicef.rapidreg.service.impl.IncidentFormServiceImpl;
import org.unicef.rapidreg.service.impl.SyncCaseServiceImpl;
import org.unicef.rapidreg.service.impl.SyncTracingServiceImpl;
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
    public UserService provideUserService(UserDao userDao) {
        return new UserServiceImpl(userDao);
    }

    @Provides
    @Singleton
    public CaseService provideCaseService(CaseDao caseDao, CasePhotoDao casePhotoDao) {
        return new CaseService(caseDao, casePhotoDao);
    }

    @Provides
    @Singleton
    public CaseFormService provideCaseFormService(CaseFormDao caseFormDao) {
        return new CaseFormServiceImpl(caseFormDao);
    }


    @Provides
    @Singleton
    public CasePhotoService provideCasePhotoService(CasePhotoDao casePhotoDao) {
        return new CasePhotoService(casePhotoDao);
    }

    @Provides
    @Singleton
    public IncidentService provideIncidentService() {
        return new IncidentService();
    }

    @Provides
    @Singleton
    public IncidentFormService provideIncidentFormService(IncidentFormDao incidentFormDao) {
        return new IncidentFormServiceImpl(incidentFormDao);
    }

    @Provides
    @Singleton
    public RecordService provideRecordService() {
        return new RecordService();
    }

    @Provides
    @Singleton
    public TracingService provideTracingService(TracingDao tracingDao, TracingPhotoDao tracingPhotoDao) {
        return new TracingService(tracingDao, tracingPhotoDao);
    }

    @Provides
    @Singleton
    public TracingFormService provideTracingFormService(TracingFormDao tracingFormDao) {
        return new TracingFormServiceImpl(tracingFormDao);
    }

    @Provides
    @Singleton
    public TracingPhotoService provideTracingPhotoService(TracingPhotoDao tracingPhotoDao) {
        return new TracingPhotoService(tracingPhotoDao);
    }

    @Provides
    @Singleton
    public AuthService provideAuthService() {
        return new AuthServiceImpl();
    }

    @Provides
    @Singleton
    public SyncCaseService provideSyncService(RecordService
                                                      recordService, CasePhotoDao casePhotoDao) {
        return new SyncCaseServiceImpl(casePhotoDao, recordService);
    }

    @Provides
    @Singleton
    public SyncTracingService provideSyncTracingService(RecordService recordService,
                                                        TracingPhotoDao tracingPhotoDao) {
        return new SyncTracingServiceImpl(recordService, tracingPhotoDao);
    }

    @Provides
    @Singleton
    public LoginService provideLoginService(ConnectivityManager connectivityManager,
                                            TelephonyManager telephonyManager,
                                            UserDao userDao,
                                            AuthService authService) {
        return new LoginServiceImpl(connectivityManager, telephonyManager, userDao, authService);
    }

    @Provides
    @Singleton
    public UserDao provideUserDao() {
        return new UserDaoImpl();
    }

    @Provides
    @Singleton
    public CaseFormDao provideCaseFormDao() {
        return new CaseFormDaoImpl();
    }

    @Provides
    @Singleton
    public CaseDao provideCaseDao() {
        return new CaseDaoImpl();
    }

    @Provides
    @Singleton
    public CasePhotoDao provideCasePhotoDao() {
        return new CasePhotoDaoImpl();
    }

    @Provides
    @Singleton
    public IncidentFormDao provideIncidentFormDao() {
        return new IncidentFormDaoImpl();
    }

    @Provides
    @Singleton
    public TracingPhotoDao provideTracingPhotoDao() {
        return new TracingPhotoDaoImpl();
    }

    @Provides
    @Singleton
    public TracingDao provideTracingDao() {
        return new TracingDaoImpl();
    }

    @Provides
    @Singleton
    public TracingFormDao provideTracingFormDao() {
        return new TracingFormDaoImpl();
    }

}
