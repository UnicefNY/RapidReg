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
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.utils.ImageCompressUtil;

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
    private Context context;
    private DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
    private boolean isDetailShow = true;

    public CaseListAdapter(Context context) {
        this.context = context;
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

        final Case caseItem = caseList.get(position);

        final String caseJson = new String(caseItem.getContent().getBlob());
        final Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        final Map<String, String> caseInfo = new Gson().fromJson(caseJson, type);
        caseInfo.put(CaseService.CASE_ID, caseItem.getUniqueId());

        Gender gender = Gender.valueOf(caseInfo.get("Sex").toUpperCase());
        holder.caseImage.setImageDrawable(getDefaultAvatar(gender.getAvatarId()));

        String shortUUID = getShortUUID(caseItem.getUniqueId());
        holder.id_normal_state.setText(shortUUID);
        holder.id_hidden_state.setText(shortUUID);
        holder.genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
        holder.genderName.setText(gender.getName());
        holder.genderName.setTextColor(ContextCompat.getColor(context, gender.getColorId()));
        holder.age.setText(caseInfo.get("Age"));
        holder.registrationDate.setText(dateFormat.format(caseItem.getRegistrationDate()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<CasePhoto> casePhotos = CasePhotoService.getInstance().getAllCasePhotos(caseItem.getId());
                for (CasePhoto casePhoto : casePhotos) {
                    Bitmap thumbnail = ImageCompressUtil.convertByteArrayToImage(casePhoto.getThumbnail().getBlob());
                    CaseService.CaseValues.addPhoto(thumbnail, casePhoto.getPath());
                }
                CaseService.CaseValues.setValues(caseInfo);
                CaseActivity caseActivity = (CaseActivity) context;
                setViewMode(caseActivity);

                caseActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, new CaseRegisterWrapperFragment(),
                                CaseRegisterWrapperFragment.class.getSimpleName())
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
        protected TextView registrationDate;
        protected ImageView caseImage;
        protected View view;
        protected ViewSwitcher viewSwitcher;

        public CaseListHolder(View itemView) {
            super(itemView);
            view = itemView;

            id_normal_state = (TextView) itemView.findViewById(R.id.id_normal_state);
            id_hidden_state = (TextView) itemView.findViewById(R.id.id_on_hidden_state);
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
