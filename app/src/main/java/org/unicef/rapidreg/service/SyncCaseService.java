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
import org.unicef.rapidreg.repository.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.repository.remote.SyncCaseRepository;
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


 public interface SyncCaseService{
     Observable<Response<ResponseBody>> getCasePhoto(String id, String photoKey, int
            photoSize);
     Observable<Response<ResponseBody>> getCaseAudio(String id);
     Observable<Response<JsonElement>> getCase(String id, String locale, Boolean isMobile) ;

     Observable<Response<JsonElement>> getCasesIds(String lastUpdate, Boolean isMobile);

     Response<JsonElement> uploadCaseJsonProfile(RecordModel item) ;

     void uploadAudio(RecordModel item) ;

     Call<Response<JsonElement>> deleteCasePhotos(String id, JsonArray photoKeys) ;

     void uploadCasePhotos(final RecordModel record) ;
}


