package org.unicef.rapidreg.injection.component;

import android.app.Application;
import android.content.Context;

import org.unicef.rapidreg.PrimeroGlideModule;
import org.unicef.rapidreg.injection.ApplicationContext;
import org.unicef.rapidreg.injection.module.ApplicationModule;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.network.SyncTracingService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentPhotoService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(PrimeroGlideModule primeroGlideModule);

    @ApplicationContext
    Context context();

    Application application();

    UserService userService();

    CaseFormService caseFormService();

    CasePhotoService casePhotoService();

    CaseService caseService();

    RecordService recordService();

    TracingFormService tracingFormService();

    TracingPhotoService tracingPhotoService();

    TracingService tracingService();

    IncidentService incidentService();

    IncidentFormService incidentFormService();

    IncidentPhotoService incidentPhotoService();

    AuthService authService();

    SyncService syncService();

    SyncTracingService syncTracingService();
}
