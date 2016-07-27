package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordListAdapter;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.util.Date;

public class CaseListAdapter extends RecordListAdapter {

    public CaseListAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(RecordListHolder holder, int position) {
        final RecordModel record = recordList.get(position);

        final String recordJson = new String(record.getContent().getBlob());

        final ItemValues itemValues = ItemValues.generateItemValues(recordJson);

        Gender gender;
        try {
            gender = Gender.valueOf(itemValues.getAsString(RecordService.SEX).toUpperCase());
        } catch (Exception e) {
            gender = Gender.UNKNOWN;
        }

        try {
            RecordPhoto headerPhoto = CasePhotoService.getInstance().getFirst(record.getId());
            Glide.with(holder.image.getContext()).load(headerPhoto.getPhoto().getBlob()).into(holder.image);
        } catch (Exception e) {
            holder.image.setImageDrawable(activity.getResources().getDrawable(gender.getAvatarId()));
        }

        final String shortUUID = RecordService.getShortUUID(record.getUniqueId());

        holder.idNormalState.setText(shortUUID);
        holder.idHiddenState.setText(shortUUID);
        holder.genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
        holder.genderName.setText(gender.getName());
        holder.genderName.setTextColor(ContextCompat.getColor(activity, gender.getColorId()));
        String age = itemValues.getAsString(RecordService.AGE);
        holder.age.setText(isValidAge(age) ? age : "---");
        Date registrationDate = record.getRegistrationDate();
        holder.registrationDate.setText(dateFormat.format(registrationDate));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putLong(CaseService.CASE_ID, record.getId());
                activity.turnToFeature(CaseFeature.DETAILS_MINI, args, null);
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
        MALE("BOY", R.drawable.avatar_placeholder, R.drawable.boy, R.color.primero_blue),
        FEMALE("GIRL", R.drawable.avatar_placeholder, R.drawable.girl, R.color.primero_red),
        UNKNOWN("------", R.drawable.avatar_placeholder, R.drawable.gender_default, R.color.primero_font_light);

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
