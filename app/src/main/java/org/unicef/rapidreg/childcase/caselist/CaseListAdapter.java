package org.unicef.rapidreg.childcase.caselist;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Gender;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import javax.inject.Inject;

import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_CP;
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;
import static org.unicef.rapidreg.service.RecordService.MODULE;

public class CaseListAdapter extends RecordListAdapter {

    @Inject
    CaseService caseService;

    @Inject
    public CaseListAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final RecordListViewHolder holder, int position) {
        final long recordId = recordList.get(position);
        final RecordModel record = caseService.getById(recordId);

        final String recordJson = new String(record.getContent().getBlob());
        final ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(new Gson().fromJson(recordJson, JsonObject.class)));


        Gender gender;
        try {
            gender = Gender.valueOf(itemValues.getAsString(RecordService.SEX).toUpperCase());
        } catch (Exception e) {
            gender = Gender.PLACEHOLDER;
        }
        final String shortUUID = caseService.getShortUUID(record.getUniqueId());
        int age = itemValues.getAsInt(RecordService.AGE);
        holder.setValues(gender, shortUUID, String.valueOf(age), record);
        holder.setViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                String moduleId = itemValues.getAsString(MODULE);
                Feature feature = moduleId.equals(MODULE_CASE_CP) ? CaseFeature.DETAILS_CP_MINI : CaseFeature.DETAILS_GBV_MINI;

                args.putString(MODULE, moduleId);
                args.putLong(CaseService.CASE_PRIMARY_ID, recordId);
                ((RecordActivity)context).turnToFeature(feature, args, null);
                try {
                    Utils.clearAudioFile(AUDIO_FILE_PATH);
                    if (record.getAudio() != null) {
                        StreamUtil.writeFile(record.getAudio().getBlob(), RecordService.AUDIO_FILE_PATH);
                    }
                } catch (IOException e) {
                }
            }
        });
        toggleTextArea(holder);
    }
}
