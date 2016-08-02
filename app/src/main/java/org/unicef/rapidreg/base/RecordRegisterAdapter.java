package org.unicef.rapidreg.base;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.widgets.viewholder.AudioUploadViewHolder;
import org.unicef.rapidreg.widgets.viewholder.BaseViewHolder;
import org.unicef.rapidreg.widgets.viewholder.DefaultViewHolder;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;
import org.unicef.rapidreg.widgets.viewholder.MiniFormProfileViewHolder;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadMiniFormViewHolder;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadViewHolder;
import org.unicef.rapidreg.widgets.viewholder.SingleLineRadioViewHolder;
import org.unicef.rapidreg.widgets.viewholder.SubFormViewHolder;
import org.unicef.rapidreg.widgets.viewholder.TextViewHolder;
import org.unicef.rapidreg.widgets.viewholder.TickBoxViewHolder;

import java.util.List;

public class RecordRegisterAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String LAYOUT = "layout";
    private static final String PREFIX = "form_";
    private static final int VIEW_HOLDER_GENERIC = 0;
    private static final int VIEW_HOLDER_SEPARATOR = 1;
    private static final int VIEW_HOLDER_TICK_BOX = 2;
    private static final int VIEW_HOLDER_PHOTO_UPLOAD_BOX = 3;
    private static final int VIEW_HOLDER_AUDIO_UPLOAD_BOX = 4;
    private static final int VIEW_HOLDER_SUBFORM = 5;
    private static final int VIEW_HOLDER_TEXT = 6;
    private static final int VIEW_HOLDER_SELECT_SINGLE_LINE = 7;
    private static final int VIEW_HOLDER_RADIO_SINGLE_LINE = 8;
    private static final int VIEW_HOLDER_PHOTO_UPLOAD_BOX_MINI_FORM = 9;
    private static final int VIEW_HOLDER_MINI_FORM_PROFILE = 10;

    private boolean isMiniForm;

    private List<Field> fields;
    private RecordActivity activity;
    protected LayoutInflater inflater;
    protected Resources resources;
    protected String packageName;
    private RecordPhotoAdapter adapter;

    private ItemValuesMap itemValues;

    public RecordRegisterAdapter(Context context, List<Field> fields, ItemValuesMap itemValues, boolean isMiniForm) {
        this.fields = fields;
        this.activity = (RecordActivity) context;
        this.itemValues = itemValues;
        this.isMiniForm = isMiniForm;

        inflater = LayoutInflater.from(context);
        resources = context.getResources();
        packageName = context.getPackageName();
    }

    public void setPhotoAdapter(RecordPhotoAdapter adapter) {
        this.adapter = adapter;
    }

    public RecordPhotoAdapter getPhotoAdapter() {
        return adapter;
    }

    public void setItemValues(ItemValuesMap itemValues) {
        this.itemValues = itemValues;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_TEXT:
                return new TextViewHolder(activity, inflater.inflate(resources
                        .getIdentifier(PREFIX + Field.TYPE_TEXT_FIELD,
                                LAYOUT, packageName), parent, false), itemValues);

            case VIEW_HOLDER_RADIO_SINGLE_LINE:
                return new SingleLineRadioViewHolder(activity, inflater.inflate(resources
                        .getIdentifier(PREFIX + Field.TYPE_SINGLE_LINE_RADIO,
                                LAYOUT, packageName), parent, false), itemValues);

            case VIEW_HOLDER_SELECT_SINGLE_LINE:
            case VIEW_HOLDER_GENERIC:
                return new GenericViewHolder(activity, inflater.inflate(resources
                        .getIdentifier(PREFIX + Field.TYPE_TEXT_FIELD,
                                LAYOUT, packageName), parent, false), itemValues);

            case VIEW_HOLDER_TICK_BOX:
                return new TickBoxViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(PREFIX + Field.TYPE_TICK_BOX, LAYOUT, packageName),
                        parent, false), itemValues);

            case VIEW_HOLDER_PHOTO_UPLOAD_BOX:
                return new PhotoUploadViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(PREFIX + Field.TYPE_PHOTO_UPLOAD_LAYOUT, LAYOUT, packageName),
                        parent, false), itemValues, adapter);

            case VIEW_HOLDER_PHOTO_UPLOAD_BOX_MINI_FORM:
                return new PhotoUploadMiniFormViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(Field.TYPE_PHOTO_VIEW_SLIDER, LAYOUT, packageName),
                        parent, false), itemValues);

            case VIEW_HOLDER_AUDIO_UPLOAD_BOX:
                return new AudioUploadViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(PREFIX + Field.TYPE_AUDIO_UPLOAD_LAYOUT, LAYOUT, packageName),
                        parent, false), itemValues);

            case VIEW_HOLDER_SUBFORM:
                return new SubFormViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(PREFIX + Field.TYPE_SUBFORM_FIELD, LAYOUT, packageName),
                        parent, false), itemValues);

            case VIEW_HOLDER_MINI_FORM_PROFILE:
                return new MiniFormProfileViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(PREFIX + Field.TYPE_MINI_FORM_PROFILE, LAYOUT, packageName),
                        parent, false), itemValues);
            default:
                return new DefaultViewHolder(activity, new View(activity));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Field field = fields.get(position);
        holder.setValue(field);
        holder.setIsRecyclable(false);

        if (!activity.getCurrentFeature().isDetailMode()) {
            holder.setOnClickListener(field);
        } else {
            holder.setFieldEditable(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Field field = fields.get(position);
        if (field.isTextField() || field.isNumericField()) {
            return VIEW_HOLDER_TEXT;
        }
        if (field.isSelectField()) {
            if (!field.isManyOptions()) {
                return field.isMultiSelect() ?
                        VIEW_HOLDER_SELECT_SINGLE_LINE : VIEW_HOLDER_RADIO_SINGLE_LINE;
            }
        }
        if (field.isRadioButton()) {
            return VIEW_HOLDER_RADIO_SINGLE_LINE;
        }
        if (field.isTickBox()) {
            return VIEW_HOLDER_TICK_BOX;
        }
        if (field.isPhotoUploadBox()) {
            if (!activity.getCurrentFeature().isDetailMode()) {
                return VIEW_HOLDER_PHOTO_UPLOAD_BOX;
            }
            return isMiniForm ? VIEW_HOLDER_PHOTO_UPLOAD_BOX_MINI_FORM : VIEW_HOLDER_PHOTO_UPLOAD_BOX;
        }
        if (field.isAudioUploadBox()) {
            return VIEW_HOLDER_AUDIO_UPLOAD_BOX;
        }
        if (field.isSubform()) {
            return VIEW_HOLDER_SUBFORM;
        }
        if (field.isMiniFormProfile()) {
            return VIEW_HOLDER_MINI_FORM_PROFILE;
        }
        return VIEW_HOLDER_GENERIC;
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public List<Field> getFields() {
        return fields;
    }
}
