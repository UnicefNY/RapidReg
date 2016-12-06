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
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        idView.setText(itemValues.getAsString(ItemValues.RecordProfile.ID_NORMAL_STATE));
        Gender gender;
        if (itemValues.getAsString(TracingService.SEX) != null) {
            gender = Gender.valueOf(itemValues.getAsString(TracingService.SEX).toUpperCase());
        } else {
            gender = itemValues.has(TracingService.TRACING_ID)
                    ? Gender.EMPTY
                    : Gender.PLACEHOLDER;
        }
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), gender.getGenderId(), null);
        genderBadge.setImageDrawable(drawable);
        genderName.setText(gender.getName());
        genderName.setTextColor(ContextCompat.getColor(context, gender.getColorId()));
        String age;
        if (itemValues.has(RecordService.RELATION_AGE)) {
            age = itemValues.getAsString(RecordService.RELATION_AGE);
        } else {
            age = itemValues.getAsString(RecordService.AGE);
        }
        this.age.setText(isValidAge(age) ? age : "---");
        registrationDate.setText(itemValues.getAsString(ItemValues.RecordProfile.REGISTRATION_DATE));
    }

    @Override
    public void setOnClickListener(final Field field) {

    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    protected boolean isValidAge(String value) {
        if (value == null) {
            return false;
        }
        return Integer.valueOf(value) > 0;
    }
}
