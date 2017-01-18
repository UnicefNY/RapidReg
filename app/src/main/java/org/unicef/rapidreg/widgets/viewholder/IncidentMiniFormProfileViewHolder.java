package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.view.View;

import org.unicef.rapidreg.service.cache.ItemValuesMap;

public class IncidentMiniFormProfileViewHolder extends MiniFormProfileViewHolder {
    public IncidentMiniFormProfileViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        disableRecordGenderView();
    }
}
