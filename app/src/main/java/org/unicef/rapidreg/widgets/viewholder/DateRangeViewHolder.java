package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.dialog.FiledDialogFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateRangeViewHolder extends BaseViewHolder<Field> {
    private static final String TAG = DateRangeViewHolder.class.getSimpleName();

    private static final String _TYPE_SUFFIX = "_date_or_date_range";
    private static final String _TYPE_RANGE_FROM_SUFFIX = "_from";
    private static final String _TYPE_RANGE_TO_SUFFIX = "_to";
    private static final String TYPE_DATE = "date";
    private static final String TYPE_DATE_RANGE = "date_range";

    @BindView(R.id.label)
    TextView labelView;

    @BindView(R.id.date_type_option_group)
    RadioGroup dateTypeOptionGroup;

    @BindView(R.id.date_radio_button)
    RadioButton dateRadioButton;

    @BindView(R.id.date_range_radio_button)
    RadioButton dateRangeRadioButton;

    @BindView(R.id.date_layout)
    LinearLayout dateLayout;

    @BindView(R.id.date_value)
    TextView dateValView;

    @BindView(R.id.date_range_layout)
    LinearLayout dateRangeLayout;

    @BindView(R.id.date_from_value)
    TextView dateFromValView;

    @BindView(R.id.date_to_value)
    TextView dateToValView;

    private String dateValKey;
    private String dateValFromKey;
    private String dateValToKey;
    private String fieldDateTypeKey;

    public DateRangeViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setValue(Field field) {
        String labelText = getLabel(field);

        if (isRequired(field)) {
            labelText += " (Required)";
        }
        labelView.setHint(labelText);

        dateValKey = field.getName();
        dateValFromKey = field.getName() + _TYPE_RANGE_FROM_SUFFIX;
        dateValToKey = field.getName() + _TYPE_RANGE_TO_SUFFIX;
        fieldDateTypeKey = field.getName() + _TYPE_SUFFIX;

        initValueViewData(field);

        setEditableBackgroundStyle(isEditable(field));
    }

    private void initValueViewData(Field field) {
        if (!itemValues.has(field.getName() + _TYPE_SUFFIX)) {
            return;
        }

        String dateType = itemValues.getAsString(field.getName() + _TYPE_SUFFIX);
        switch (dateType) {
            case TYPE_DATE:
                enableDateView();
                dateValView.setText(itemValues.getAsString(dateValKey));
                dateRadioButton.toggle();
                break;
            case TYPE_DATE_RANGE:
                enableDateRangeView();
                dateFromValView.setText(itemValues.getAsString(dateValFromKey));
                dateToValView.setText(itemValues.getAsString(dateValToKey));
                dateRangeRadioButton.toggle();
                break;
        }

    }

    @Override
    public void setOnClickListener(Field field) {
        dateValView.setOnClickListener(view -> {
            try {
                FiledDialogFactory.createDialog(Field.FieldType.DATE_FIELD, context, field, itemValues, dateValView, null).show();
            } catch (DialogException e) {
                Log.e(TAG, e.getMessage());
            }
        });

        dateFromValView.setOnClickListener(view -> {
            try {
                Field fakeField = new Field();
                fakeField.setName(dateValFromKey);
                fakeField.setDisplayName(field.getDisplayName());
                FiledDialogFactory.createDialog(Field.FieldType.DATE_FIELD, context, fakeField, itemValues, dateFromValView, null).show();
            } catch (DialogException e) {
                Log.e(TAG, e.getMessage());
            }
        });

        dateToValView.setOnClickListener(view -> {
            try {
                Field fakeField = new Field();
                fakeField.setName(dateValToKey);
                fakeField.setDisplayName(field.getDisplayName());
                FiledDialogFactory.createDialog(Field.FieldType.DATE_FIELD, context, fakeField, itemValues, dateToValView, null).show();
            } catch (DialogException e) {
                Log.e(TAG, e.getMessage());
            }
        });

        dateTypeOptionGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (dateRadioButton.isChecked()) {
                enableDateView();
                itemValues.addStringItem(fieldDateTypeKey, TYPE_DATE);
                itemValues.removeItem(dateValFromKey);
                itemValues.removeItem(dateValToKey);
            }
            if (dateRangeRadioButton.isChecked()) {
                enableDateRangeView();
                itemValues.addStringItem(fieldDateTypeKey, TYPE_DATE_RANGE);
                itemValues.removeItem(dateValKey);
            }
        });
    }

    private void enableDateRangeView() {
        dateRangeLayout.setVisibility(View.VISIBLE);
        dateLayout.setVisibility(View.GONE);
        dateValView.setText("");
    }

    private void enableDateView() {
        dateLayout.setVisibility(View.VISIBLE);
        dateRangeLayout.setVisibility(View.GONE);
        dateFromValView.setText("");
        dateToValView.setText("");
    }



    @Override
    public void setFieldEditable(boolean editable) {
        disableUneditableField(editable, dateRadioButton);
        disableUneditableField(editable, dateRangeRadioButton);
    }

    @Override
    public void setFieldClickable(boolean clickable) {}
}
