package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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
        final String subFormJson = new String(caseItem.getSubform().getBlob());

        final ItemValues itemValues = CaseService.getInstance().generateItemValues(caseJson, subFormJson);

        Gender gender;
        try {
            gender = Gender.valueOf(itemValues.getAsString("Sex").toUpperCase());
        } catch (Exception e) {
            gender = Gender.UNKNOWN;
        }

        try {
            CasePhoto headerPhoto = CasePhotoService.getInstance().getCaseFirstThumbnail(caseItem.getId());
            Glide.with(holder.caseImage.getContext()).
                    load((headerPhoto.getThumbnail().getBlob())).into(holder.caseImage);
        } catch (Exception e) {
            holder.caseImage.setImageDrawable(activity.getResources().getDrawable(gender.getAvatarId()));
        }

        final String shortUUID = CaseService.getInstance().getShortUUID(caseItem.getUniqueId());

        holder.idNormalState.setText(shortUUID);
        holder.idHiddenState.setText(shortUUID);
        holder.genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
        holder.genderName.setText(gender.getName());
        holder.genderName.setTextColor(ContextCompat.getColor(activity, gender.getColorId()));
        String age = itemValues.getAsString("age");
        holder.age.setText(isValidAge(age) ? age : "");
        holder.registrationDate.setText(dateFormat.format(caseItem.getRegistrationDate()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.turnToDetailOrEditPage(CaseFeature.DETAILS, caseItem.getId());
                try {
                    CaseService.clearAudioFile();
                    if (caseItem.getAudio() != null) {
                        StreamUtil.writeFile(caseItem.getAudio().getBlob(), CaseService.AUDIO_FILE_PATH);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        toggleTextArea(holder);
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
            caseImage = (CircleImageView) itemView.findViewById(R.id.case_image);

            viewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.view_switcher);
        }
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
