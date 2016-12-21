package org.unicef.rapidreg.tracing.tracinglist;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Gender;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.utils.StreamUtil;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;

import javax.inject.Inject;

import static org.unicef.rapidreg.db.impl.TracingDaoImpl.TRACING_PRIMARY_ID;
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public class TracingListAdapter extends RecordListAdapter {

    @Inject
    TracingService tracingService;

    @Inject
    public TracingListAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(final RecordListViewHolder holder, int position) {
        final long recordId = recordList.get(position);
        final RecordModel record = tracingService.getById(recordId);
        final String recordJson = new String(record.getContent().getBlob());
        final ItemValuesMap itemValues = ItemValuesMap.fromJson(recordJson);
        Gender gender = Gender.EMPTY;
        if (itemValues.has(RecordService.SEX)) {
            gender = Gender.valueOf(itemValues.getAsString(RecordService.SEX).toUpperCase());
        }

        final String shortUUID = tracingService.getShortUUID(record.getUniqueId());
        String age = itemValues.getAsString(RecordService.RELATION_AGE);
        holder.setValues(gender, shortUUID, age, record);
        holder.setViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong(TRACING_PRIMARY_ID, record.getId());
                ((RecordActivity) context).turnToFeature(TracingFeature.DETAILS_MINI, args, null);
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
