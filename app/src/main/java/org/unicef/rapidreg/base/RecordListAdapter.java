package org.unicef.rapidreg.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.RecordModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordListHolder> {
    public static final String TAG = RecordListAdapter.class.getSimpleName();
    private static final int TEXT_AREA_SHOWED_STATE = 0;
    private static final int TEXT_AREA_HIDDEN_STATE = 1;

    protected RecordActivity activity;
    protected DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
    protected List<Long>  recordList = new ArrayList<>();
    protected boolean isDetailShow = true;

    public RecordListAdapter(Context context) {
        this.activity = (RecordActivity) context;
    }

    public void setRecordList(List<Long>  recordList) {
        this.recordList = recordList;
    }

    @Override
    public RecordListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_list, parent, false);

        return new RecordListHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void toggleViews(boolean isDetailShow) {
        this.isDetailShow = isDetailShow;
        this.notifyDataSetChanged();
    }

    protected void toggleTextArea(RecordListHolder holder) {
        if (isDetailShow) {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_SHOWED_STATE);
        } else {
            holder.viewSwitcher.setDisplayedChild(TEXT_AREA_HIDDEN_STATE);
        }
    }

    protected Drawable getDefaultGenderBadge(int genderId) {
        return ResourcesCompat.getDrawable(activity.getResources(), genderId, null);
    }

    protected boolean isValidAge(String value) {
        if (value == null) {
            return false;
        }
        return Integer.valueOf(value) > 0;
    }

    public class RecordListHolder extends RecyclerView.ViewHolder {
        public TextView idNormalState;
        public TextView idHiddenState;
        public ImageView genderBadge;
        public TextView genderName;
        public TextView age;
        public TextView registrationDate;
        public ImageView image;
        public View view;
        public ViewSwitcher viewSwitcher;

        public RecordListHolder(View itemView) {
            super(itemView);
            view = itemView;
            idNormalState = (TextView) itemView.findViewById(R.id.id_normal_state);
            idHiddenState = (TextView) itemView.findViewById(R.id.id_on_hidden_state);
            genderBadge = (ImageView) itemView.findViewById(R.id.gender_badge);
            genderName = (TextView) itemView.findViewById(R.id.gender_name);
            age = (TextView) itemView.findViewById(R.id.age);
            registrationDate = (TextView) itemView.findViewById(R.id.registration_date);
            image = (CircleImageView) itemView.findViewById(R.id.record_image);
            viewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.view_switcher);
        }
    }
}
