package org.unicef.rapidreg.base.record.recordlist.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.rapidreg.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<SpinnerState> {

    private final List<SpinnerState> states;
    private final Context context;

    public SpinnerAdapter(Context context, int resource, List<SpinnerState> states) {
        super(context, resource, states);
        this.context = context;
        this.states = states;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent, false);
    }

    public View getCustomView(int position, ViewGroup parent, boolean isDropDownView) {
        SpinnerState state = states.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutId = isDropDownView ? R.layout.record_list_spinner_opened : R.layout.record_list_spinner_closed;
        View view = inflater.inflate(layoutId, parent, false);

        ImageView indicator = (ImageView) view.findViewById(R.id.indicator);
        TextView orderName = (TextView) view.findViewById(R.id.order_name);

        indicator.setImageResource(state.getResId());
        orderName.setText(isDropDownView ? state.getLongName() : state.getShortName());

        return view;
    }

}