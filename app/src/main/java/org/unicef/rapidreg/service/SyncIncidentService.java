package org.unicef.rapidreg.service;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.model.Incident;

import retrofit2.Response;
import rx.Observable;

public interface SyncIncidentService {
    Response<JsonElement> uploadIncidentJsonProfile(Incident item);

    Observable<Response<JsonElement>> getIncidentIds(String lastUpdate, boolean isMobile);

    Observable<Response<JsonElement>> getIncident(String id, String locale, boolean isMobile);
}
