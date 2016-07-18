package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordListAdapter;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;

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
            gender = Gender.valueOf(itemValues.getAsString("sex").toUpperCase());
        } catch (Exception e) {
            gender = Gender.UNKNOWN;
        }

        try {
            RecordPhoto headerPhoto = CasePhotoService.getInstance().getFirstThumbnail(record.getId());
            Glide.with(holder.image.getContext()).load((headerPhoto.getThumbnail().getBlob())).into(holder.image);
        } catch (Exception e) {
            holder.image.setImageDrawable(activity.getResources().getDrawable(gender.getAvatarId()));
        }

        final String shortUUID = RecordService.getShortUUID(record.getUniqueId());

        holder.idNormalState.setText(shortUUID);
        holder.idHiddenState.setText(shortUUID);
        holder.genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
        holder.genderName.setText(gender.getName());
        holder.genderName.setTextColor(ContextCompat.getColor(activity, gender.getColorId()));
        String age = itemValues.getAsString("age");
        holder.age.setText(isValidAge(age) ? age : "");
        holder.registrationDate.setText(dateFormat.format(record.getRegistrationDate()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.turnToDetailOrEditPage(CaseFeature.DETAILS, record.getId());
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
        MALE("BOY", R.drawable.avatar_placeholder, R.drawable.boy, R.color.boy_blue),
        FEMALE("GIRL", R.drawable.avatar_placeholder, R.drawable.girl, R.color.girl_red),
        UNKNOWN(null, R.drawable.avatar_placeholder, R.drawable.gender_default, R.color.transparent);

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
