package org.unicef.rapidreg.sync;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.data.Blob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.event.PullCasesEvent;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
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

    @Override
    public void attachView(SyncView view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        EventBus.getDefault().unregister(this);
    }

    public void doSync() {
        if (isViewAttached()) {

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pullCases(PullCasesEvent event) {
        getView().showSyncProgressDialog();
        getView().setProgressMax(100);
        syncService.getAllCasesRx(PrimeroConfiguration.getCookie(), "en", false)
                .filter(new Func1<Response<List<JsonElement>>, Boolean>() {
                    @Override
                    public Boolean call(Response<List<JsonElement>> listResponse) {
                        if (listResponse.isSuccessful()) {
                            return true;
                        }

                        return false;
                    }
                })
                .flatMap(new Func1<Response<List<JsonElement>>, Observable<JsonElement>>() {
                    @Override
                    public Observable<JsonElement> call(Response<List<JsonElement>> listResponse) {
                        return Observable.from(listResponse.body());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<JsonElement>() {
            @Override
            public void onCompleted() {
                getView().hideSyncProgressDialog();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JsonElement jsonElement) {

                getView().setProgressIncrease();

                JsonObject jsonObject = jsonElement.getAsJsonObject();

                try {
                    String caseId = jsonObject.get("case_id").getAsString();
                    Case item = CaseService.getInstance().getCaseByUniqueId(caseId);
                    if (item != null) {
                        item.setInternalId(jsonObject.get("_id").getAsString());
                        item.setInternalRev(jsonObject.get("_rev").getAsString());
                        item.setSynced(true);
                        item.setContent(new Blob(jsonObject.toString().getBytes()));
                        item.save();
                    } else {
                        item = new Case();
                        item.setUniqueId(CaseService.getInstance().createUniqueId());
                        item.setName(jsonObject.get("name").getAsString());
                        item.setAge(jsonObject.get("age").getAsInt());
                        item.setInternalId(jsonObject.get("_id").getAsString());
                        item.setInternalRev(jsonObject.get("_rev").getAsString());
                        item.setRegistrationDate(new SimpleDateFormat("yyyy/MM/dd")
                                .parse((jsonObject.get("registration_date").getAsString())));
                        item.setCreatedBy(jsonObject.get("created_by").getAsString());
                        item.setLastSyncedDate(Calendar.getInstance().getTime());
                        item.setLastUpdatedDate(Calendar.getInstance().getTime());
                        item.setSynced(true);
                        item.setContent(new Blob(jsonObject.toString().getBytes()));
                        item.save();

                    }
                } catch (Exception e) {
                    return;
                }

            }
        });
    }

    private void pullCases() {

    }

    public void doAttemptCancelSync() {
        getView().showSyncCancelConfirmDialog();
    }

    public void doCancelSync() {

    }

    private void startUpLoadCases() {
        final List<Case> caseList = CaseService.getInstance().getAll();

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
                        EventBus.getDefault().post(new PullCasesEvent());
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
