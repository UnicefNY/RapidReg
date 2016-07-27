package org.unicef.rapidreg.widgets.dialog;


import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.unicef.rapidreg.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchAbleDialog extends Dialog {

    private static final String TAG = "SearchAbleDialog";

    @BindView(R.id.List)
    ListView list;
    @BindView(R.id.EditBox)
    EditText filterText;
    @BindView(R.id.okButton)
    Button okButton;
    @BindView(R.id.cancelButton)
    Button cancelButton;

    private MyAdapter adapter = null;


    public SearchAbleDialog(Context context, String title, String[] items, int selectIndex) {
        super(context);
        setContentView(R.layout.form_alert_dialog);
        ButterKnife.bind(this);

        this.setTitle(title);


        filterText.addTextChangedListener(filterTextWatcher);

        adapter = new MyAdapter(context, new ArrayList<>(Arrays.asList(items)));

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Log.d(TAG, "Selected Item is = " + list.getItemAtPosition(position));
            }
        });

        adapter.index = selectIndex;
        //adapter.listener.onClick(adapter.arrayList.get(adapter.index));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.clearButton)
    void onClickCleanButton() {
        adapter.index = -1;
        adapter.listener.onClick("");
        adapter.notifyDataSetChanged();
    }

    public void setOkButton(final View.OnClickListener listener) {

        okButton.setOnClickListener(listener);
    }

    public void setCancelButton(final View.OnClickListener listener) {

        cancelButton.setOnClickListener(listener);
    }

    public int getCurrentSelectIndex() {
        return adapter.index;
    }

    public void setOnClick(SearchAbleDialogOnClickListener listener) {
        adapter.listener = listener;
    }

    interface SearchAbleDialogOnClickListener {
        void onClick(String result);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }
    };

    @Override
    public void onStop() {
        filterText.removeTextChangedListener(filterTextWatcher);
    }

    public class MyAdapter extends BaseAdapter implements Filterable {

        List<String> arrayList;
        List<String> mOriginalValues; // Original Values
        LayoutInflater inflater;
        SearchAbleDialogOnClickListener listener = null;

        public MyAdapter(Context context, List<String> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView textView;
            RadioButton radioButton;
        }

        private ViewHolder holder;
        int index = -1;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            holder = new ViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.form_alert_dialog_row, null);
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.textView.setClickable(true);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(arrayList.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = position;
                    notifyDataSetChanged();
                }
            });

            holder.radioButton
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            if (isChecked) {

                                index = position;
                                listener.onClick(arrayList.get(index));
                                notifyDataSetChanged();
                            }
                        }
                    });

            if (index == position) {
                holder.radioButton.setChecked(true);
            } else {
                holder.radioButton.setChecked(false);
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    arrayList = (List<String>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    List<String> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(arrayList); // saves the original data in mOriginalValues
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i);
                            if (data.toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(data);
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }
    }

}