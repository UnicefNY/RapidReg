package org.unicef.rapidreg.sync;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.network.UploadCaseBody;
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
        String requestBody = "{\"child\":{\n" +
                "   \"owned_by\": \"primero\",\n" +
                "   \"owned_by_full_name\": \"System Superuser\",\n" +
                "   \"owned_by_agency\": \"agency-unicef\",\n" +
                "   \"previously_owned_by\": \"primero\",\n" +
                "   \"previously_owned_by_full_name\": \"System Superuser\",\n" +
                "   \"previously_owned_by_agency\": \"agency-unicef\",\n" +
                "   \"module_id\": \"primeromodule-cp\",\n" +
                "   \"created_organization\": \"agency-unicef\",\n" +
                "   \"created_by\": \"primero\",\n" +
                "   \"created_by_full_name\": \"System Superuser\",\n" +
                "   \"created_at\": \"2016/06/17 07:27:28 +0000\",\n" +
                "   \"last_updated_at\": \"2016/06/18 07:27:28 +0000\",\n" +
                "   \"posted_at\": \"2016/06/18 07:27:28 +0000\",\n" +
                "   \"short_id\": \"2b127c9\",\n" +
                "   \"record_state\": true,\n" +
                "   \"marked_for_mobile\": false,\n" +
                "   \"consent_for_services\": false,\n" +
                "   \"child_status\": \"Open\",\n" +
                "   \"name\": \"Yan Jiayang2\",\n" +
                "   \"name_first\": \"Jiayang2\",\n" +
                "   \"name_last\": \"Yan\",\n" +
                "   \"name_nickname\": \"Jiayang2\",\n" +
                "   \"name_given_post_separation\": \"No\",\n" +
                "   \"registration_date\": \"2016/06/17\",\n" +
                "   \"sex\": \"Male\",\n" +
                "   \"age\": 10,\n" +
                "   \"date_of_birth\": \"2006/01/10\",\n" +
                "   \"estimated\": false,\n" +
                "   \"address_is_permanent\": false,\n" +
                "   \"system_generated_followup\": false,\n" +
                "   \"family_details_section\": [\n" +
                "       {\n" +
                "           \"relation_name\": \"Feng Bo2\",\n" +
                "           \"relation\": \"Father\",\n" +
                "           \"relation_is_caregiver\": false,\n" +
                "           \"relation_child_lived_with_pre_separation\": \"Yes\",\n" +
                "           \"relation_child_is_in_contact\": \"No\",\n" +
                "           \"relation_child_is_separated_from\": \"Yes\",\n" +
                "           \"relation_nickname\": \"Bobo\",\n" +
                "           \"relation_is_alive\": \"Unknown\",\n" +
                "           \"relation_age\": 40,\n" +
                "           \"relation_date_of_birth\": \"1976/01/01\"\n" +
                "       }\n" +
                "   ],\n" +
                "   \"case_id\": \"56798b3e-c5b8-44d9-a8c1-2593b2b127c9\",\n" +
                "   \"hidden_name\": false,\n" +
                "   \"posted_from\": \"Mobile\",\n" +
                "   \"couchrest-type\": \"Child\"\n" +
                "}}";
        syncService.postCase(PrimeroConfiguration.getCookie(), true, UploadCaseBody.body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<String>>() {
            @Override
            public void call(Response<String> response) {
                Log.i(TAG, response.toString());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.i(TAG, throwable.toString());
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
