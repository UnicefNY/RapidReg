package org.unicef.rapidreg.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.Field;
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void updateImageAdapter(UpdateImageEvent event) {
        photoAdapter.addItem(event.getImagePath());
        ImageView view = (ImageView) getActivity().findViewById(R.id.add_image_button);

        if (!photoAdapter.isEmpty()) {
            view.setImageResource(R.drawable.photo_add);
        }
        if (photoAdapter.isFull()) {
            view.setVisibility(View.GONE);
        }
        photoAdapter.notifyDataSetChanged();
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

    protected void clearProfileItems() {
        itemValues.removeItem(ItemValues.RecordProfile.ID_NORMAL_STATE);
        itemValues.removeItem(ItemValues.RecordProfile.REGISTRATION_DATE);
        itemValues.removeItem(ItemValues.RecordProfile.ID);
    }

    protected abstract List<Field> getFields();
}
