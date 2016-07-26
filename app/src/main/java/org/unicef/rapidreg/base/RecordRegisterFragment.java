package org.unicef.rapidreg.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecordRegisterFragment extends MvpFragment<RecordRegisterView, RecordRegisterPresenter>
        implements RecordRegisterView {

    public static final String ITEM_VALUES = "item_values";

    protected static final int[] ANIM_IDS = {android.R.anim.slide_in_left, android.R.anim.slide_out_right};
    protected static final int INVALID_RECORD_ID = -100;

    @BindView(R.id.register_forms_content)
    RecyclerView fieldList;

    @BindView(R.id.form_switcher)
    protected Button formSwitcher;

    @BindView(R.id.edit)
    protected FloatingActionButton editButton;

    protected RecordPhotoAdapter photoAdapter;
    protected ItemValuesMap itemValues;
    protected long recordId;

    private RecordRegisterAdapter recordRegisterAdapter;

    @NonNull
    @Override
    public RecordRegisterPresenter createPresenter() {
        return new RecordRegisterPresenter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void initView(RecordRegisterAdapter adapter) {
        recordRegisterAdapter = adapter;
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
        layout.setAutoMeasureEnabled(true);
        fieldList.setLayoutManager(layout);
        fieldList.setAdapter(recordRegisterAdapter);
        recordRegisterAdapter.setPhotoAdapter(photoAdapter);
        recordRegisterAdapter.setItemValues(itemValues);

        formSwitcher.setText(R.string.show_short_form);
    }

    public RecordRegisterAdapter getRegisterAdapter() {
        return recordRegisterAdapter;
    }

    public void clearFocus() {
        View focusedChild = fieldList.getFocusedChild();
        if (focusedChild != null) {
            focusedChild.clearFocus();
        }
    }

    protected void addProfileFieldForDetailsPage(List<Field> fields) {
        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            Field field = new Field();
            field.setType(Field.TYPE_MINI_FORM_PROFILE);
            try {
                fields.add(1, field);
            } catch (Exception e) {
                fields.add(field);
            }
        }
    }

    protected abstract List<Field> getFields();
}
