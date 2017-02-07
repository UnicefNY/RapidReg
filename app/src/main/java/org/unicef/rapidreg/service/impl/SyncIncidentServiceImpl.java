package org.unicef.rapidreg.service.impl;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.repository.remote.SyncTracingsRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.SyncIncidentService;

import retrofit2.Response;
import rx.Observable;

public class SyncIncidentServiceImpl extends BaseRetrofitService implements SyncIncidentService {
    public SyncIncidentServiceImpl() {
        super(SyncTracingsRepository.class);
    }

    @Override
    public Response<JsonElement> uploadJsonProfile(Incident item) {
        return null;
    }

    @Override
    public Observable<Response<JsonElement>> getIds(String lastUpdate, boolean isMobile) {
        return null;
    }

    @Override
    public Observable<Response<JsonElement>> get(String id, String locale, boolean isMobile) {
        return null;
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
