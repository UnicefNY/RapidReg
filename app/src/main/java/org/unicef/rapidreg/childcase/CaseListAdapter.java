package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.CasePhotoCache;
import org.unicef.rapidreg.service.cache.SubformCache;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CaseListAdapter extends RecyclerView.Adapter<CaseListAdapter.CaseListHolder> {
    public static final String TAG = CaseListAdapter.class.getSimpleName();
    private static final int TEXT_AREA_SHOWED_STATE = 0;
    private static final int TEXT_AREA_HIDDEN_STATE = 1;

    private List<Case> caseList = new ArrayList<>();
    private CaseActivity activity;
    private DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
    private boolean isDetailShow = true;

    public CaseListAdapter(Context activity) {
        this.activity = (CaseActivity) activity;
    }

    public void setCaseList(List<Case> caseList) {
        this.caseList = caseList;
    }

    @Override
    public CaseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.case_list, parent, false);

        return new CaseListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CaseListHolder holder, int position) {
        final Case caseItem = caseList.get(position);

        final String caseJson = new String(caseItem.getContent().getBlob());
        final String subformJson = new String(caseItem.getSubform().getBlob());
        final Type caseType = new TypeToken<Map<String, String>>() {
        }.getType();

        final Map<String, String> caseInfo = new Gson().fromJson(caseJson, caseType);
        caseInfo.put(CaseService.CASE_ID, caseItem.getUniqueId());

        final Type subformType = new TypeToken<Map<String, List<Map<String, String>>>>() {
        }.getType();

        final Map<String, List<Map<String, String>>> subformInfo
                = new Gson().fromJson(subformJson, subformType);

        Gender gender;

        if (caseInfo.get("Sex") != null) {
            gender = Gender.valueOf(caseInfo.get("Sex").toUpperCase());
        } else {
            gender = Gender.UNKNOWN;
        }
        try {
            CasePhoto casePhoto =  CasePhotoService.getInstance().getAllCasePhotos(caseItem.getId()).get(0);
            Bitmap thumbnail = CasePhotoCache.syncAvatarPhotoBitmap(casePhoto);
            holder.caseImage.setImageDrawable(getDefaultAvatar(thumbnail));
        } catch (Exception e) {
            holder.caseImage.setImageDrawable(getDefaultAvatar(gender.getAvatarId()));
        }
        final String shortUUID = getShortUUID(caseItem.getUniqueId());

        holder.idNormalState.setText(shortUUID);
        holder.idHiddenState.setText(shortUUID);
        holder.genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
        holder.genderName.setText(gender.getName());
        holder.genderName.setTextColor(ContextCompat.getColor(activity, gender.getColorId()));
        String age = caseInfo.get("Age");
        holder.age.setText(isValidAge(age) ? age : "");
        holder.registrationDate.setText(dateFormat.format(caseItem.getRegistrationDate()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseService.getInstance().clearCaseCache();

                setProfileForMiniForm(caseItem, caseInfo, shortUUID);
                CaseFieldValueCache.setValues(caseInfo);
                SubformCache.setValues(subformInfo);
                List<CasePhoto> casePhotos = CasePhotoService.getInstance().getAllCasePhotos(caseItem.getId());

                CasePhotoCache.syncPhotosPaths(casePhotos);

                activity.turnToFeature(CaseFeature.DETAILS);

                try {
                    CaseFieldValueCache.clearAudioFile();
                    if (caseItem.getAudio() != null) {
                        StreamUtil.writeFile(caseItem.getAudio().getBlob(), CaseFieldValueCache.AUDIO_FILE_PATH);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        toggleTextArea(holder);
    }

    private void setProfileForMiniForm(Case caseItem, Map<String, String> caseInfo, String shortUUID) {
        CaseFieldValueCache.addProfileItem(CaseFieldValueCache.CaseProfile.ID_NORMAL_STATE, shortUUID);
        CaseFieldValueCache.addProfileItem(CaseFieldValueCache.CaseProfile.SEX, caseInfo.get("Sex"));
        CaseFieldValueCache.addProfileItem(CaseFieldValueCache.CaseProfile.REGISTRATION_DATE,
                dateFormat.format(caseItem.getRegistrationDate()));
        CaseFieldValueCache.addProfileItem(CaseFieldValueCache.CaseProfile.GENDER_NAME, shortUUID);
        CaseFieldValueCache.addProfileItem(CaseFieldValueCache.CaseProfile.AGE, caseInfo.get("Age"));
        CaseFieldValueCache.addProfileItem(CaseFieldValueCache.CaseProfile.ID, String.valueOf(caseItem.getId()));
    }

    @Override
    public int getItemCount() {
        return caseList.size();
    }

    public void toggleViews(boolean isDetailShow) {
        this.isDetailShow = isDetailShow;
        this.notifyDataSetChanged();
    }

    private void toggleTextArea(CaseListHolder holder) {
        if (isDetailShow) {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_SHOWED_STATE);
        } else {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_HIDDEN_STATE);
        }
    }

    private Drawable getDefaultGenderBadge(int genderId) {
        return ResourcesCompat.getDrawable(activity.getResources(), genderId, null);
    }

    private Drawable getDefaultAvatar(int resId) {
        return getRoundedDrawable(resId);
    }

    private Drawable getDefaultAvatar(Bitmap photoBit) {
        return getRoundedDrawable(photoBit);
    }


    private Drawable getRoundedDrawable(int resId) {
        Resources resources = activity.getResources();
        Bitmap img = BitmapFactory.decodeResource(resources, resId);
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(resources, img);
        dr.setCornerRadius(Math.max(img.getWidth(), img.getHeight()) / 2.0f);
        return dr;
    }

    private Drawable getRoundedDrawable(Bitmap photoBit) {
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(activity.getResources(), photoBit);
        dr.setCornerRadius(Math.max(photoBit.getWidth(), photoBit.getHeight()) / 2.0f);
        return dr;
    }

    private String getShortUUID(String uuid) {
        int length = uuid.length();
        return length > 7 ? uuid.substring(length - 7) : uuid;
    }

    private boolean isValidAge(String value) {
        if (value == null) {
            return false;
        }

        return Integer.valueOf(value) > 0;
    }

    public static class CaseListHolder extends RecyclerView.ViewHolder {
        protected TextView idNormalState;
        protected TextView idHiddenState;
        protected ImageView genderBadge;
        protected TextView genderName;
        protected TextView age;
        protected TextView registrationDate;
        protected ImageView caseImage;
        protected View view;
        protected ViewSwitcher viewSwitcher;

        public CaseListHolder(View itemView) {
            super(itemView);
            view = itemView;
            idNormalState = (TextView) itemView.findViewById(R.id.id_normal_state);
            idHiddenState = (TextView) itemView.findViewById(R.id.id_on_hidden_state);
            genderBadge = (ImageView) itemView.findViewById(R.id.gender_badge);
            genderName = (TextView) itemView.findViewById(R.id.gender_name);
            age = (TextView) itemView.findViewById(R.id.age);
            registrationDate = (TextView) itemView.findViewById(R.id.registration_date);
            caseImage = (ImageView) itemView.findViewById(R.id.case_image);

            viewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.view_switcher);
        }
    }

    public enum Gender {
        MALE("BOY", R.drawable.avatar_boy, R.drawable.boy, R.color.boy_blue),
        FEMALE("GIRL", R.drawable.avatar_girl, R.drawable.girl, R.color.girl_red),
        UNKNOWN(null, R.drawable.photo_placeholder, R.drawable.gender_default, R.color.transparent);

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
