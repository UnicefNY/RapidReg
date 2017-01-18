package org.unicef.rapidreg.injection.module;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import org.unicef.rapidreg.injection.ApplicationContext;
import org.unicef.rapidreg.repository.CaseDao;
import org.unicef.rapidreg.repository.CaseFormDao;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.repository.TracingDao;
import org.unicef.rapidreg.repository.TracingFormDao;
import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.FormRemoteService;
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
import org.unicef.rapidreg.service.impl.CaseFormServiceImpl;
import org.unicef.rapidreg.service.impl.FormRemoteServiceImpl;
import org.unicef.rapidreg.service.impl.IncidentFormServiceImpl;
import org.unicef.rapidreg.service.impl.LoginServiceImpl;
import org.unicef.rapidreg.service.impl.SyncCaseServiceImpl;
import org.unicef.rapidreg.service.impl.SyncTracingServiceImpl;
import org.unicef.rapidreg.service.impl.TracingFormServiceImpl;
import org.unicef.rapidreg.service.impl.UserServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {AndroidServiceModule.class, ApplicationDaoModule.class})
public class ApplicationServiceModule {
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
    public FormRemoteService provideAuthService() {
        return new FormRemoteServiceImpl();
    }

    @Provides
    @Singleton
    public SyncCaseService provideSyncService(CasePhotoDao casePhotoDao) {
        return new SyncCaseServiceImpl(casePhotoDao);
    }

    @Provides
    @Singleton
    public SyncTracingService provideSyncTracingService(TracingPhotoDao tracingPhotoDao) {
        return new SyncTracingServiceImpl(tracingPhotoDao);
    }

    @Provides
    @Singleton
    public LoginService provideLoginService(ConnectivityManager connectivityManager,
                                            TelephonyManager telephonyManager,
                                            UserDao userDao) {
        return new LoginServiceImpl(connectivityManager, telephonyManager, userDao);
    }
}
