package org.unicef.rapidreg.base.record.recordregister;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecordRegisterFragment extends MvpFragment<RecordRegisterView, RecordRegisterPresenter>
        implements RecordRegisterView {


    public static final String ITEM_VALUES = "item_values";

    protected static final int[] ANIM_TO_FULL = {R.anim.slide_in_right, R.anim.slide_out_left};
    protected static final int[] ANIM_TO_MINI = {android.R.anim.slide_in_left, android.R.anim.slide_out_right};
    public static final int INVALID_RECORD_ID = -100;

    @BindView(R.id.register_forms_content)
    RecyclerView fieldList;

    @BindView(R.id.form_switcher)
    protected TextView formSwitcher;

    @BindView(R.id.edit)
    protected FloatingActionButton editButton;

    protected long recordId;

    private RecordRegisterAdapter recordRegisterAdapter;

    @Override
    public void promoteRequiredFieldNotFilled() {
        Toast.makeText(getActivity(), R.string.required_field_is_not_filled, Toast.LENGTH_LONG).show();
    }

    @Override
    public void promoteSaveFail() {
        Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
    }

    public FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getActivity()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    public RecordPhotoAdapter getPhotoAdapter() {
        return recordRegisterAdapter.getPhotoAdapter();
    }

    public void setPhotoAdapter(RecordPhotoAdapter photoAdapter) {
        recordRegisterAdapter.setPhotoAdapter(photoAdapter);
    }

    public ItemValuesMap getItemValues() {
        return recordRegisterAdapter.getItemValues();
    }

    public void setItemValues(ItemValuesMap itemValues) {
        recordRegisterAdapter.setItemValues(itemValues);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
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
        recordRegisterAdapter.setItemValues(new ItemValuesMap());

        fieldList.setAdapter(recordRegisterAdapter);
        formSwitcher.setText(R.string.show_short_form);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void updateImageAdapter(UpdateImageEvent event) {
        getPhotoAdapter().addItem(event.getImagePath());
        getPhotoAdapter().notifyDataSetChanged();
        EventBus.getDefault().removeStickyEvent(event);
    }

    public RecordRegisterAdapter getRegisterAdapter() {
        return recordRegisterAdapter;
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

    @Override
    public List<String> getPhotos() {
        return getRegisterAdapter().getPhotoAdapter().getAllItems();
    }

    protected abstract List<Field> getFields();
}
