package org.unicef.rapidreg.tracing.tracinglist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordListAdapter;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TracingListAdapter extends RecordListAdapter {

    private final TracingService tracingService;

    public TracingListAdapter(Context context, TracingService tracingService) {
        super(context);
        this.tracingService = tracingService;
    }

    @Override
    public void onBindViewHolder(final RecordListHolder holder, int position) {
        final long recordId = recordList.get(position);
        final RecordModel record = tracingService.getById(recordId);
        final String recordJson = new String(record.getContent().getBlob());
        final ItemValues itemValues = ItemValues.generateItemValues(recordJson);

        Gender gender = Gender.EMPTY;
        if (itemValues.has(RecordService.SEX)) {
            gender = Gender.valueOf(itemValues.getAsString(RecordService.SEX).toUpperCase());
        }

        Glide.with(holder.image.getContext()).load(new Tracing(recordId))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .error(activity.getResources().getDrawable(gender.getAvatarId())).into(holder.image);

        final String shortUUID = RecordService.getShortUUID(record.getUniqueId());

        holder.idNormalState.setText(shortUUID);
        holder.idHiddenState.setText(shortUUID);
        holder.genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
        holder.genderName.setText(gender.getName());
        holder.genderName.setTextColor(ContextCompat.getColor(activity, gender.getColorId()));
        String age = itemValues.getAsString(RecordService.RELATION_AGE);
        holder.age.setText(isValidAge(age) ? age : "---");
        Date registrationDate = record.getRegistrationDate();
        holder.registrationDate.setText(dateFormat.format(registrationDate));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong(TracingService.TRACING_PRIMARY_ID, record.getId());
                activity.turnToFeature(TracingFeature.DETAILS_MINI, args, null);
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

    public enum Gender {
        MALE("MALE", R.drawable.avatar_placeholder, R.drawable.boy, R.color.primero_blue),
        FEMALE("FEMALE", R.drawable.avatar_placeholder, R.drawable.girl, R.color.primero_red),
        EMPTY("", R.drawable.avatar_placeholder, R.drawable.gender_default, R.color.primero_font_light);

        private String name;
        private int avatarId;
        private int genderId;
        private int colorId;

        Gender(String name, int avatarId, int genderId, int colorId) {
            this.name = name;
            this.avatarId = avatarId;
            this.genderId = genderId;
            this.colorId = colorId;
        }

        public String getName() {
            return name;
        }

        public int getAvatarId() {
            return avatarId;
        }

        public int getGenderId() {
            return genderId;
        }

        public int getColorId() {
            return colorId;
        }
    }
}
