package org.unicef.rapidreg.service;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.model.Incident;

import retrofit2.Response;
import rx.Observable;

public interface SyncIncidentService {
    Response<JsonElement> uploadJsonProfile(Incident item);

    Observable<Response<JsonElement>> getIds(String lastUpdate, boolean isMobile);

    Observable<Response<JsonElement>> get(String id, String locale, boolean isMobile);
}
