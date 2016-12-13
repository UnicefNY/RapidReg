package org.unicef.rapidreg.childcase.caselist;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Gender;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import javax.inject.Inject;

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

        final ItemValues itemValues = ItemValues.generateItemValues(recordJson);

        Gender gender;
        try {
            gender = Gender.valueOf(itemValues.getAsString(RecordService.SEX).toUpperCase());
        } catch (Exception e) {
            gender = Gender.PLACEHOLDER;
        }
        final String shortUUID = RecordService.getShortUUID(record.getUniqueId());
        String age = itemValues.getAsString(RecordService.AGE);
        holder.setValues(gender, shortUUID, age, record);
        holder.setViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(MODULE, itemValues.getAsString(MODULE));
                args.putLong(CaseService.CASE_PRIMARY_ID, recordId);
                ((RecordActivity)context).turnToFeature(CaseFeature.DETAILS_MINI, args, null);
                try {
                    RecordService.clearAudioFile();
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
