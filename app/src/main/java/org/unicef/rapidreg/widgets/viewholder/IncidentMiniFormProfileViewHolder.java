package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;

import butterknife.BindView;

public class IncidentMiniFormProfileViewHolder extends MiniFormProfileViewHolder {

    @BindView(R.id.incident_id_normal_state)
    TextView incidentIdView;

    @BindView(R.id.incident_location)
    TextView incidentLocation;

    @BindView(R.id.incident_age)
    public TextView incidentAge;

    @BindView(R.id.incident_registration_date)
    public TextView incidentRegistrationDate;


    public IncidentMiniFormProfileViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        disableRecordGenderView();
    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }

    @Override
    public void setValue(Field field) {
        super.setValue(field);
        containerRecordListItem.setVisibility(View.GONE);
        containerIncidentListItem.setVisibility(View.VISIBLE);
        String locationText = TextUtils.truncateByDoubleColons(itemValues.getAsString(RecordService.INCIDENT_LOCATION),
                PrimeroAppConfiguration.getCurrentSystemSettings().getDistrictLevel());
        incidentLocation.setText(TextUtils.isEmpty(locationText) ? "---" : locationText);
        incidentRegistrationDate.setText(itemValues.getAsString(ItemValuesMap.RecordProfile
                .REGISTRATION_DATE));
        incidentIdView.setText(itemValues.getAsString(ItemValuesMap.RecordProfile.ID_NORMAL_STATE));
        String age = extractAge();
        incidentAge.setText(age == null ? "---" : age);
    }
}
