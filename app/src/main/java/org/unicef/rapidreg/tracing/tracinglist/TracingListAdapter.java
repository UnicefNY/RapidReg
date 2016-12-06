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
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;

import javax.inject.Inject;

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
        final ItemValues itemValues = ItemValues.generateItemValues(recordJson);

        Gender gender = Gender.EMPTY;
        if (itemValues.has(RecordService.SEX)) {
            gender = Gender.valueOf(itemValues.getAsString(RecordService.SEX).toUpperCase());
        }

        final String shortUUID = RecordService.getShortUUID(record.getUniqueId());
        String age = itemValues.getAsString(RecordService.RELATION_AGE);
        holder.setValues(gender, shortUUID, age, record);
        holder.setViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong(TracingService.TRACING_PRIMARY_ID, record.getId());
                ((RecordActivity) context).turnToFeature(TracingFeature.DETAILS_MINI, args, null);
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
