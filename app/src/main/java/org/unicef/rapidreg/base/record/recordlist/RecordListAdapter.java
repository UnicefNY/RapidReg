package org.unicef.rapidreg.base.record.recordlist;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Gender;
import org.unicef.rapidreg.model.RecordModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordListViewHolder> {
    public static final String TAG = RecordListAdapter.class.getSimpleName();
    private static final int TEXT_AREA_SHOWED_STATE = 0;
    private static final int TEXT_AREA_HIDDEN_STATE = 1;

    protected Context context;
    protected DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
    protected List<Long>  recordList = new ArrayList<>();
    protected boolean isDetailShow = true;

    public RecordListAdapter(Context context) {
        this.context = context;
    }

    public void setRecordList(List<Long>  recordList) {
        this.recordList = recordList;
    }

    @Override
    public RecordListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_list, parent, false);

        return new RecordListViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void toggleViews(boolean isDetailShow) {
        this.isDetailShow = isDetailShow;
        notifyDataSetChanged();
    }

    protected void toggleTextArea(RecordListViewHolder holder) {
        if (isDetailShow) {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_SHOWED_STATE);
        } else {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_HIDDEN_STATE);
        }
    }

    protected Drawable getDefaultGenderBadge(int genderId) {
        return ResourcesCompat.getDrawable(context.getResources(), genderId, null);
    }

    protected boolean isValidAge(String value) {
        if (value == null) {
            return false;
        }
        return Double.valueOf(value).intValue() >= 0;
    }

    protected boolean isValidDate(Date date) {
        if (date == null) {
            return false;
        }
        return true;
    }

    public class RecordListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_normal_state)
        public TextView idNormalState;

        @BindView(R.id.id_on_hidden_state)
        public TextView idHiddenState;

        @BindView(R.id.gender_badge)
        public ImageView genderBadge;

        @BindView(R.id.gender_name)
        public TextView genderName;

        @BindView(R.id.age)
        public TextView age;

        @BindView(R.id.registration_date)
        public TextView registrationDate;

        @BindView(R.id.record_image)
        public ImageView image;

        @BindView(R.id.view_switcher)
        public ViewSwitcher viewSwitcher;

        public RecordListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setValues(Gender gender,
                              String shortUUID,
                              String ageContent,
                              RecordModel record) {
            Glide
                    .with(image.getContext())
                    .load(record)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(context.getResources().getDrawable(gender.getAvatarId()))
                    .into(image);

            idNormalState.setText(shortUUID);
            idHiddenState.setText(shortUUID);
            genderBadge.setImageDrawable(getDefaultGenderBadge(gender.getGenderId()));
            genderName.setText(gender.getName());
            genderName.setTextColor(ContextCompat.getColor(context, gender.getColorId()));
            age.setText(isValidAge(ageContent) ? ageContent : "---");

            Date registrationDateText = record.getRegistrationDate();
            registrationDate.setText(isValidDate(registrationDateText) ? dateFormat.format(registrationDateText) : "---");
        }

        public void setViewOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }

        public void disableRecordImageView() {
            image.setVisibility(View.GONE);
        }

        public void disableRecordAgeView() {
            age.setVisibility(View.GONE);
        }

        public void disableRecordGenderView() {
            genderBadge.setVisibility(View.GONE);
            genderName.setVisibility(View.GONE);
        }
    }
}
