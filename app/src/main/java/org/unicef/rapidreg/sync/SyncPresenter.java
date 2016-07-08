package org.unicef.rapidreg.sync;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.service.CaseService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SyncPresenter extends MvpBasePresenter<SyncView> {

    private Context context;

    private SyncService syncService;

    public SyncPresenter() {
    }

    public SyncPresenter(Context context) {
        this.context = context;
        try {
            syncService = new SyncService(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private  void convert (JsonElement jsonElement) {

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
                if (entry.getValue().isJsonArray()) {
                    break;
                }
            }
        }

        final Type caseType = new TypeToken<Map<String, String>>() {
        }.getType();

        final Map<String, String> caseInfo = new Gson().fromJson(jsonElement, caseType);
        //caseInfo.put(CaseService.CASE_ID, caseItem.getUniqueId());

        final Type subformType = new TypeToken<Map<String, List<Map<String, String>>>>() {
        }.getType();

//                            final Map<String, List<Map<String, String>>> subformInfo
//                                    = new Gson().fromJson(subformJson, subformType);
    }
    public void doSync() {
        if (isViewAttached()) {
            getView().showSyncProgressDialog();

            syncService.getAllCasesRx(PrimeroConfiguration.getCookie(), "en", false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<List<JsonElement>>>() {
                @Override
                public void call(Response<List<JsonElement>> arrayListResponse) {
//                    if (arrayListResponse.isSuccessful() ){
//                        return;
//                    }
                    List<JsonElement> arrayList = arrayListResponse.body();

                    for (JsonElement element: arrayList) {



                            convert(element);

                    }
                    return;
                }
            });

            try {
                startUpLoadCases();
                startDownLoadCaseForms();
                startDownLoadCases();
                doSyncSuccessAction();
            } catch (Exception e) {
                doSyncFailureAction();
            }
        }
    }

    public void doAttemptCancelSync() {
        getView().showSyncCancelConfirmDialog();
    }

    public void doCancelSync() {

    }

    private void startUpLoadCases() {

    }

    private void startDownLoadCaseForms() {

    }

    private void startDownLoadCases() {

    }

    private void doSyncSuccessAction() {
        updateDataViews();
        getView().showSyncSuccessMessage();
        getView().hideSyncProgressDialog();
    }

    private void doSyncFailureAction() {
        updateDataViews();
        getView().showSyncErrorMessage();
        getView().hideSyncProgressDialog();
    }

    private void updateDataViews() {

    }
}
