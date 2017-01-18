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
import org.unicef.rapidreg.repository.impl.CaseDaoImpl;
import org.unicef.rapidreg.repository.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.repository.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.repository.impl.IncidentFormDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingFormDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.repository.impl.UserDaoImpl;
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

@Module
public class ApplicationDaoModule {
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
