package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.model.Gender;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static org.unicef.rapidreg.service.TracingService.TRACING_ID;

public class MiniFormProfileViewHolder extends BaseViewHolder<Field> {

    public static final String TAG = MiniFormProfileViewHolder.class.getSimpleName();

    @BindView(R.id.id_normal_state)
    TextView idView;

    @BindView(R.id.gender_badge)
    ImageView genderBadge;

    @BindView(R.id.gender_name)
    TextView genderName;

    @BindView(R.id.age)
    TextView age;

    @BindView(R.id.registration_date)
    TextView registrationDate;

    public MiniFormProfileViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(Field field) {
        idView.setText(itemValues.getAsString(ItemValuesMap.RecordProfile.ID_NORMAL_STATE));
        Gender gender;
        if (itemValues.getAsString(TracingService.SEX) != null) {
            gender = Gender.valueOf(itemValues.getAsString(TracingService.SEX).toUpperCase());
        } else {
            gender = itemValues.has(TRACING_ID)
                    ? Gender.EMPTY
                    : Gender.PLACEHOLDER;
        }
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), gender
                .getGenderId(), null);
        genderBadge.setImageDrawable(drawable);
        genderName.setText(gender.getName());
        genderName.setTextColor(ContextCompat.getColor(context, gender.getColorId()));
        registrationDate.setText(itemValues.getAsString(ItemValuesMap.RecordProfile
                .REGISTRATION_DATE));

        String age;
        if(itemValues.has(RecordService.RELATION_AGE)){
            age = itemValues.getAsString(RecordService.RELATION_AGE);
        } else {
            age = itemValues.getAsString(RecordService.AGE);
        }
        this.age.setText(age == null ? "---" :age);
    }

    @Override
    public void setOnClickListener(final Field field) {

    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    public void disableRecordGenderView() {
        genderBadge.setVisibility(GONE);
        genderName.setVisibility(GONE);
    }
}
