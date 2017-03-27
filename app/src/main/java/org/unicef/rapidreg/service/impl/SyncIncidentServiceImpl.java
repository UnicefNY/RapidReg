package org.unicef.rapidreg.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.repository.remote.SyncIncidentRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.SyncIncidentService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;

import retrofit2.Response;
import rx.Observable;


public class SyncIncidentServiceImpl extends BaseRetrofitService<SyncIncidentRepository> implements
        SyncIncidentService {
    @Override
    public Response<JsonElement> uploadIncidentJsonProfile(Incident item) {
        ItemValuesMap itemValuesMap = ItemValuesMap.fromJson(new String(item.getContent().getBlob()));
        String shortUUID = TextUtils.getLastSevenNumbers(item.getUniqueId());

        itemValuesMap.addStringItem("short_id", shortUUID);
        itemValuesMap.removeItem("_attachments");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("incident", new Gson().fromJson(new Gson().toJson(
                itemValuesMap.getValues()), JsonObject.class));

        Response<JsonElement> response;
        if (!TextUtils.isEmpty(item.getInternalId())) {
            response = getRepository(SyncIncidentRepository.class).putIncident(PrimeroAppConfiguration.getCookie(),
                    item.getInternalId(), jsonObject).toBlocking().first();
        } else {
            response = getRepository(SyncIncidentRepository.class).postIncident(PrimeroAppConfiguration.getCookie(),
                    jsonObject)
                    .toBlocking().first();
        }
        if (!response.isSuccessful()) {
            throw new RuntimeException(response.errorBody().toString());
        }

        JsonObject responseJsonObject = response.body().getAsJsonObject();

        item.setInternalId(responseJsonObject.get("_id").getAsString());
        item.setInternalRev(responseJsonObject.get("_rev").getAsString());
        item.setLocation(responseJsonObject.has(RecordService.LOCATION) ? responseJsonObject.get(RecordService
                .LOCATION).getAsString() : null);
        responseJsonObject.remove("histories");
        item.setContent(new Blob(responseJsonObject.toString().getBytes()));
        item.update();

        return response;
    }

    @Override
    public Observable<Response<JsonElement>> getIncidentIds(String lastUpdate, boolean isMobile) {
        return getRepository(SyncIncidentRepository.class).getIncidentIds(PrimeroAppConfiguration.getCookie(),
                lastUpdate,
                isMobile);
    }

    @Override
    public Observable<Response<JsonElement>> getIncident(String id, String locale, boolean isMobile) {
        return getRepository(SyncIncidentRepository.class).getIncident(PrimeroAppConfiguration.getCookie(), id,
                locale, isMobile);
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
