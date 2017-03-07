package org.unicef.rapidreg.injection.module;

import org.unicef.rapidreg.repository.CaseDao;
import org.unicef.rapidreg.repository.CaseFormDao;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.IncidentDao;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.repository.SystemSettingsDao;
import org.unicef.rapidreg.repository.TracingDao;
import org.unicef.rapidreg.repository.TracingFormDao;
import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.repository.impl.CaseDaoImpl;
import org.unicef.rapidreg.repository.impl.CaseFormDaoImpl;
import org.unicef.rapidreg.repository.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.repository.impl.IncidentDaoImpl;
import org.unicef.rapidreg.repository.impl.IncidentFormDaoImpl;
import org.unicef.rapidreg.repository.impl.SystemSettingsDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingFormDaoImpl;
import org.unicef.rapidreg.repository.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.repository.impl.UserDaoImpl;

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
    public IncidentDao provideIncidentDao() {
        return new IncidentDaoImpl();
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

    @Provides
    @Singleton
    public SystemSettingsDao provideSystemSettingsDao() {
        return new SystemSettingsDaoImpl();
    }

}
