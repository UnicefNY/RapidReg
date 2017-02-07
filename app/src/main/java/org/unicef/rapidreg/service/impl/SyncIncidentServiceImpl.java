package org.unicef.rapidreg.service.impl;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.repository.remote.SyncIncidentRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.SyncIncidentService;

import retrofit2.Response;
import rx.Observable;


public class SyncIncidentServiceImpl extends BaseRetrofitService<SyncIncidentRepository> implements SyncIncidentService {
    public SyncIncidentServiceImpl() {
        super(SyncIncidentRepository.class);
    }

    @Override
    public Response<JsonElement> uploadJsonProfile(Incident item) {
        return null;
    }

    @Override
    public Observable<Response<JsonElement>> getIds(String lastUpdate, boolean isMobile) {
        return getRepository().getIncidentIds(PrimeroAppConfiguration.getCookie(), lastUpdate, isMobile);
    }

    @Override
    public Observable<Response<JsonElement>> get(String id, String locale, boolean isMobile) {
        return getRepository().getIncident(PrimeroAppConfiguration.getCookie(), id, locale, isMobile);
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
