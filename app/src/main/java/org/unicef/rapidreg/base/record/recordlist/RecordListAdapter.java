package org.unicef.rapidreg.base.record.recordlist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    protected List<Long> recordList = new ArrayList<>();
    protected List<Long> recordWillBeDeletedList = new ArrayList<>();
    protected boolean isDetailShow = true;
    protected boolean isDeleteMode = false;
    protected boolean isSelectAll = false;
    protected int retainedPosition = 0;
    protected OnViewUpdateListener onViewUpdateListener;
    protected int syncedRecordsCount;

    public RecordListAdapter(Context context) {
        this.context = context;
    }

    public void setOnViewUpdateListener(OnViewUpdateListener onViewUpdateListener) {
        this.onViewUpdateListener = onViewUpdateListener;
    }

    public void setRecordList(List<Long> recordList) {
        this.recordList = recordList;
    }

    public void removeRecords() {
        for (Long recordId : recordWillBeDeletedList) {
            int position = recordList.indexOf(recordId);
            recordList.remove(recordId);
            notifyItemRemoved(position);
        }
        recordWillBeDeletedList.clear();
    }

    public List<Long> getRecordWillBeDeletedList() {
        return recordWillBeDeletedList;
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

    public void toggleDeleteViews(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();
    }

    public void toggleSelectAllItems(boolean isSelectAll) {
        this.isSelectAll = isSelectAll;
        if (isSelectAll) {
            for (Long recordId : recordList) {
                if (!recordWillBeDeletedList.contains(recordId)) {
                    recordWillBeDeletedList.add(recordId);
                }
            }
        } else {
            recordWillBeDeletedList.clear();
        }
        notifyDataSetChanged();
    }

    protected void toggleTextArea(RecordListViewHolder holder) {
        if (isDetailShow) {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_SHOWED_STATE);
        } else {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_HIDDEN_STATE);
        }
    }

    protected void toggleDeleteArea(RecordListViewHolder holder, boolean isDeletable) {
        if (isDeleteMode) {
            holder.toggleDeleteView(isDeletable);
        } else {
            holder.toggleNormalView();
        }
    }

    protected void toggleDeleteCheckBox(RecordListViewHolder holder) {
        if (!holder.getRecord().isSynced()) {
            recordWillBeDeletedList.remove(holder.getRecord().getId());
        }
        holder.deleteStateCheckBox.setChecked(recordWillBeDeletedList.contains(holder.deleteStateCheckBox.getTag()));
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

    public int caculateRetainedPosition() {
        int retainedPosition = 0;
        if (recordWillBeDeletedList.isEmpty()) {
            return retainedPosition;
        }
        retainedPosition = recordList.indexOf(recordWillBeDeletedList.get(0));
        for (Long recordId : recordWillBeDeletedList) {
            int position = recordList.indexOf(recordId);
            if (retainedPosition > position) {
                retainedPosition = position;
            }
        }
        return --retainedPosition;
    }

    public void setSyncedListCount(int syncedRecordsCount) {
        this.syncedRecordsCount = syncedRecordsCount;
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

        @BindView(R.id.item_delete_checkbox_content)
        public LinearLayout itemDeleteCheckboxContent;

        @BindView(R.id.delete_state)
        public CheckBox deleteStateCheckBox;

        private RecordModel record;

        public RecordListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setValues(Gender gender,
                              String shortUUID,
                              String ageContent,
                              RecordModel record) {
            this.record = record;
            int position = getAdapterPosition();
            deleteStateCheckBox.setTag(recordList.get(position));
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

            deleteStateCheckBox.setOnCheckedChangeListener(null);
            deleteStateCheckBox.setChecked(recordWillBeDeletedList.contains(deleteStateCheckBox.getTag()));
            deleteStateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Long recordId = recordList.get(position);
                    if (isChecked) {
                        if (!recordWillBeDeletedList.contains(recordId)) {
                            recordWillBeDeletedList.add(recordId);
                        }
                    } else {
                        if (recordWillBeDeletedList.contains(recordId)) {
                            recordWillBeDeletedList.remove(recordId);
                        }
                    }
                    if (onViewUpdateListener != null) {
                        onViewUpdateListener.onRecordsDeletable(!recordWillBeDeletedList.isEmpty());
                        onViewUpdateListener.onSelectedAllButtonCheckable(recordWillBeDeletedList.size() == syncedRecordsCount);
                    }
                }
            });
        }

        public RecordModel getRecord() {
            return record;
        }

        public void setViewOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }

        public void setViewOnLongClickListener(View.OnLongClickListener listener) {
            itemView.setOnLongClickListener(listener);
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

        public void toggleDeleteView(boolean isDeletable) {
            if (onViewUpdateListener != null) {
                onViewUpdateListener.onRecordsDeletable(!recordWillBeDeletedList.isEmpty());
            }
            deleteStateCheckBox.setVisibility(View.VISIBLE);
            deleteStateCheckBox.setEnabled(isDeletable);
            itemView.setEnabled(isDeletable);

            itemView.setOnClickListener(view -> deleteStateCheckBox.toggle());
            itemView.setBackgroundColor(isDeletable ? Color.WHITE : Color.LTGRAY);
        }

        public void toggleNormalView() {
            deleteStateCheckBox.setChecked(false);
            deleteStateCheckBox.setVisibility(View.GONE);

            itemView.setEnabled(true);
            itemView.setBackgroundColor(Color.WHITE);

            recordWillBeDeletedList.clear();
        }
    }

    public interface OnViewUpdateListener {
        void onRecordsDeletable(boolean isDeletable);

        void onSelectedAllButtonCheckable(boolean isChecked);
    }
}
