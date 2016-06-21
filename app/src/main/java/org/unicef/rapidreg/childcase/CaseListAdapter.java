package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseService;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CaseListAdapter extends RecyclerView.Adapter<CaseListAdapter.CaseListHolder> {
    public static final String TAG = CaseListAdapter.class.getSimpleName();

    private List<Case> caseList;
    private Context context;
    private DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
    private boolean isDetailShow = true;

    public CaseListAdapter(Context context) {
        this.context = context;
        caseList = new ArrayList<>();
        caseList = getAllCaseData();
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
        CaseService.CaseValues.clear();

        Case caseItem = caseList.get(position);
        final String caseJson = new String(caseItem.getContent().getBlob());
        final Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        final Map<String, String> caseInfo = new Gson().fromJson(caseJson, type);

        caseInfo.put(CaseService.UNIQUE_ID, caseItem.getUniqueId());
        Gender gender = Gender.valueOf(caseInfo.get("Sex").toUpperCase());

        holder.caseImage.setImageDrawable(getDefaultAvatar(gender.getAvatarId()));
        String shortUUID = getShortUUID(caseItem.getUniqueId());
        holder.id_normal_state.setText(shortUUID);
        holder.id_hidden_state.setText(shortUUID);
        holder.genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
        holder.genderName.setText(gender.getName());
        holder.genderName.setTextColor(ContextCompat.getColor(context, gender.getColorId()));
        holder.age.setText(caseInfo.get("Age"));
        holder.createdTime.setText(dateFormat.format(caseItem.getCreateAt()));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseService.CaseValues.setValues(caseInfo);
                CaseActivity caseActivity = (CaseActivity) context;
                setViewMode(caseActivity);

                caseActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, new CaseRegisterWrapperFragment())
                        .addToBackStack(null)
                        .commit();
            }

            private void setViewMode(CaseActivity caseActivity) {
                Intent intent = new Intent();
                intent.putExtra(CaseActivity.INTENT_KEY_CASE_MODE, CaseActivity.CaseMode.DETAIL);
                caseActivity.setIntent(intent);
                caseActivity.setTopMenuItemsInCaseDetailPage();
            }
        });

        toggleTextArea(holder);
    }

    @Override
    public int getItemCount() {
        return caseList.size();
    }


    public boolean isListEmpty() {
        return getItemCount() == 0;
    }

    public void toggleViews(boolean isDetailShow) {
        this.isDetailShow = isDetailShow;
        this.notifyDataSetChanged();
    }

    private void toggleTextArea(CaseListHolder holder) {
        holder.textAreaNormalState.setVisibility(isDetailShow ? View.VISIBLE : View.GONE);
        holder.textAreaHiddenState.setVisibility(isDetailShow ? View.GONE : View.VISIBLE);
    }

    private List<Case> getAllCaseData() {
        CaseService caseService = CaseService.getInstance();
        return caseService.getCaseList();
    }

    private Drawable getDefaultGenderBadge(int genderId) {
        return ResourcesCompat.getDrawable(context.getResources(), genderId, null);
    }

    private Drawable getDefaultAvatar(int resId) {
        return getRoundedDrawable(resId);
    }

    private Drawable getRoundedDrawable(int resId) {
        Resources resources = this.context.getResources();
        Bitmap img = BitmapFactory.decodeResource(resources, resId);
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(resources, img);
        dr.setCornerRadius(Math.max(img.getWidth(), img.getHeight()) / 2.0f);

        return dr;
    }

    private String getShortUUID(String uuid) {
        int length = uuid.length();
        return length > 7 ? uuid.substring(length - 7) : uuid;
    }

    public static class CaseListHolder extends RecyclerView.ViewHolder {
        protected TextView id_normal_state;
        protected TextView id_hidden_state;
        protected ImageView genderBadge;
        protected TextView genderName;
        protected TextView age;
        protected TextView createdTime;
        protected ImageView caseImage;
        protected View view;
        protected View textAreaNormalState;
        protected View textAreaHiddenState;

        public CaseListHolder(View itemView) {
            super(itemView);
            view = itemView;

            id_normal_state = (TextView) itemView.findViewById(R.id.id_normal_state);
            id_hidden_state = (TextView) itemView.findViewById(R.id.id_on_hidden_state);
            genderBadge = (ImageView) itemView.findViewById(R.id.gender_badge);
            genderName = (TextView) itemView.findViewById(R.id.gender_name);
            age = (TextView) itemView.findViewById(R.id.age);
            createdTime = (TextView) itemView.findViewById(R.id.created_time);
            caseImage = (ImageView) itemView.findViewById(R.id.case_image);

            textAreaNormalState = itemView.findViewById(R.id.text_area_normal_state);
            textAreaHiddenState = itemView.findViewById(R.id.text_area_hidden_state);
        }
    }

    public enum Gender {
        MALE("BOY", R.drawable.avatar_boy, R.drawable.boy, R.color.boy_blue),
        FEMALE("GIRL", R.drawable.avatar_girl, R.drawable.girl, R.color.girl_red);

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
