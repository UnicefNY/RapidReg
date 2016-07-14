package org.unicef.rapidreg.sync;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SyncPresenter extends MvpBasePresenter<SyncView> {
    private Context context;

    private SyncService syncService;
    private CaseService caseService;
    private static final String TAG = SyncPresenter.class.getSimpleName();

    public SyncPresenter() {
    }

    public SyncPresenter(Context context) {
        this.context = context;
        try {
            syncService = new SyncService(context);
            caseService = CaseService.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void convert(JsonElement jsonElement) {

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
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

//            pullCases();

            try {
                startUpLoadCases();
//                startDownLoadCaseForms();
//                startDownLoadCases();
//                doSyncSuccessAction();
            } catch (Exception e) {
                doSyncFailureAction();
            }
        }
    }

    private void pullCases() {
        syncService.getAllCasesRx(PrimeroConfiguration.getCookie(), "en", false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<List<JsonElement>>>() {
            @Override
            public void call(Response<List<JsonElement>> arrayListResponse) {
                List<JsonElement> arrayList = arrayListResponse.body();

                for (JsonElement element : arrayList) {
                    convert(element);

                }
                return;
            }
        });
    }

    public void doAttemptCancelSync() {
        getView().showSyncCancelConfirmDialog();
    }

    public void doCancelSync() {

    }

    private Observable<Response<JsonElement>> getItems(int index, int max, JsonObject jsonObject) {

        return syncService.postCase(PrimeroConfiguration.getCookie(), true, jsonObject);

    }

    private void startUpLoadCases() {

        final List<Case> caseList = CaseService.getInstance().getCaseList();
        getView().setProgressMax(caseList.size());
        Observable.just(caseList)
                .flatMap(new Func1<List<Case>, Observable<Case>>() {
                    @Override
                    public Observable<Case> call(List<Case> cases) {
                        return Observable.from(cases);
                    }
                })
                .filter(new Func1<Case, Boolean>() {
                    @Override
                    public Boolean call(Case item) {
                        return !item.isSynced();
                    }
                })
                .concatMap(new Func1<Case, Observable<Response<JsonElement>>>() {
                    @Override
                    public Observable<Response<JsonElement>> call(Case item) {
                        ItemValues values = ItemValues.fromJson(new String(item.getContent().getBlob()));
                        values.addStringItem("case_id", item.getUniqueId());
                        values.addStringItem("short_id",
                                item.getUniqueId().substring(item.getUniqueId().length() - 7, item.getUniqueId().length()));

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("child", values.getValues());

                        if (!TextUtils.isEmpty(item.getInternalId())) {
                            return syncService.putCase(PrimeroConfiguration.getCookie(), true, jsonObject);
                        } else {
                            return syncService.postCase(PrimeroConfiguration.getCookie(), true, jsonObject);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<JsonElement>>() {
                    @Override
                    public void call(Response<JsonElement> response) {
                        Log.i(TAG, response.toString());
                        getView().setProgressIncrease();

                        JsonObject jsonObject = response.body().getAsJsonObject();

                        try {
                            String caseId = jsonObject.get("case_id").getAsString();
                            Case item = CaseService.getInstance().getCaseByUniqueId(caseId);
                            if (item != null) {
                                item.setInternalId(jsonObject.get("_id").getAsString());
                                item.setInternalRev(jsonObject.get("_rev").getAsString());
                                item.setSynced(true);
                                item.setContent(new Blob(jsonObject.toString().getBytes()));
                                item.save();
                            }
                        } catch (Exception e) {
                            return;
                        }


                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, throwable.toString());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        getView().hideSyncProgressDialog();
                    }
                });
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
