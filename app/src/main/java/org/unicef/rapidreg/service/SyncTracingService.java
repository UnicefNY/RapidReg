package org.unicef.rapidreg.service;


import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.repository.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.repository.remote.SyncTracingsRepository;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static org.unicef.rapidreg.service.TracingService.TRACING_ID;


public interface SyncTracingService {
    Observable<Response<ResponseBody>> getPhoto(String id, String photoKey, String
            photoSize) ;

    Observable<Response<ResponseBody>> getAudio(String id) ;

    Observable<Response<JsonElement>> get(String id, String locale, Boolean isMobile) ;

    Observable<Response<JsonElement>> getIds(String lastUpdate, Boolean isMobile) ;

    Response<JsonElement> uploadJsonProfile(RecordModel item) ;

    void uploadAudio(RecordModel item);

    Call<Response<JsonElement>> deletePhotos(String id, JsonArray photoKeys) ;

    void uploadPhotos(final RecordModel record);
}


