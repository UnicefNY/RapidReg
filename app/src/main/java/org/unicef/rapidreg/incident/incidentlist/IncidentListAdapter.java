package org.unicef.rapidreg.incident.incidentlist;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Gender;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;

import javax.inject.Inject;

public class IncidentListAdapter extends RecordListAdapter {

    @Inject
    IncidentService incidentService;

    @Inject
    public IncidentListAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final RecordListAdapter.RecordListViewHolder holder, int
            position) {
        final long recordId = recordList.get(position);
        final RecordModel record = incidentService.getById(recordId);

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
                args.putLong(IncidentService.INCIDENT_PRIMARY_ID, recordId);
                ((RecordActivity) context).turnToFeature(IncidentFeature.DETAILS_MINI, args, null);
                try {
                    RecordService.clearAudioFile();
                    if (record.getAudio() != null) {
                        StreamUtil.writeFile(record.getAudio().getBlob(), RecordService
                                .AUDIO_FILE_PATH);
                    }
                } catch (IOException e) {
                }
            }
        });
        toggleTextArea(holder);
    }


}
