package org.unicef.rapidreg.base.record.recordsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.unicef.rapidreg.R;

import java.util.List;

public class StringSpinnerAdapter extends ArrayAdapter<String> {

    private final List<String> resources;
    private final Context context;

    public StringSpinnerAdapter(Context context, int resource, List<String> resources) {
        super(context, resource, resources);
        this.resources = resources;
        this.context = context;
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
        String resource = resources.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutId = isDropDownView ? R.layout.string_list_spinner_opened : R.layout.string_list_spinner_closed;
        View view = inflater.inflate(layoutId, parent, false);

        TextView orderName = (TextView) view.findViewById(R.id.string_value);

        orderName.setText(resource);

        return view;
    }
}
