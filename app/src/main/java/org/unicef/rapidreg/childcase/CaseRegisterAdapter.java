package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.widgets.viewholder.AudioUploadViewHolder;
import org.unicef.rapidreg.widgets.viewholder.BaseViewHolder;
import org.unicef.rapidreg.widgets.viewholder.GenericViewHolder;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadViewHolder;
import org.unicef.rapidreg.widgets.viewholder.SeparatorViewHolder;
import org.unicef.rapidreg.widgets.viewholder.SubformViewHolder;
import org.unicef.rapidreg.widgets.viewholder.SingleLineRadioViewHolder;
import org.unicef.rapidreg.widgets.viewholder.TextViewHolder;
import org.unicef.rapidreg.widgets.viewholder.TickBoxViewHolder;

import java.util.List;

public class CaseRegisterAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String LAYOUT = "layout";
    private static final int VIEW_HOLDER_GENERIC = 0;
    private static final int VIEW_HOLDER_SEPARATOR = 1;
    private static final int VIEW_HOLDER_TICK_BOX = 2;
    private static final int VIEW_HOLDER_PHOTO_UPLOAD_BOX = 3;
    private static final int VIEW_HOLDER_AUDIO_UPLOAD_BOX = 4;
    private static final int VIEW_HOLDER_SUBFORM = 5;
    private static final int VIEW_HOLDER_TEXT = 6;
    private static final int VIEW_HOLDER_SELECT_SINGLE_LINE = 7;
    private static final int VIEW_HOLDER_RADIO_SINGLE_LINE = 8;

    private static final String PREFIX = "form_";

    private List<CaseField> fields;
    private CaseActivity activity;
    protected LayoutInflater inflater;
    protected Resources resources;
    protected String packageName;

    public CaseRegisterAdapter(Context activity, List<CaseField> fields) {
        this.fields = fields;

        this.activity = (CaseActivity) activity;
        inflater = LayoutInflater.from(activity);
        resources = activity.getResources();
        packageName = activity.getPackageName();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_TEXT:
                return new TextViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(PREFIX + CaseField.TYPE_TEXT_FIELD,
                                        LAYOUT, packageName), parent, false));
            case VIEW_HOLDER_RADIO_SINGLE_LINE:
                return new SingleLineRadioViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(PREFIX + CaseField.TYPE_SINGLE_LINE_RADIO,
                                        LAYOUT, packageName), parent, false));
            case VIEW_HOLDER_SELECT_SINGLE_LINE:
            case VIEW_HOLDER_GENERIC:

                return new GenericViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_TEXT_FIELD, LAYOUT, packageName),
                        parent, false));
            case VIEW_HOLDER_TICK_BOX:
                return new TickBoxViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_TICK_BOX, LAYOUT, packageName),
                        parent, false));
            case VIEW_HOLDER_PHOTO_UPLOAD_BOX:
                return new PhotoUploadViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_PHOTO_UPLOAD_LAYOUT, LAYOUT, packageName),
                        parent, false));
            case VIEW_HOLDER_AUDIO_UPLOAD_BOX:
                return new AudioUploadViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_AUDIO_UPLOAD_LAYOUT, LAYOUT, packageName),
                        parent, false));
            case VIEW_HOLDER_SUBFORM:
                return new SubformViewHolder(activity, inflater.inflate(resources
                                .getIdentifier(CaseField.TYPE_SUBFORM_FIELD, LAYOUT, packageName),
                        parent, false));

            default:
                return new SeparatorViewHolder(activity, new View(activity));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CaseField field = fields.get(position);
        holder.setValue(field);

        if (activity.getCurrentFeature() != CaseFeature.DETAILS) {
            holder.setOnClickListener(field);
        }
    }

    @Override
    public int getItemViewType(int position) {
        CaseField field = fields.get(position);
        if (field.isTextField() || field.isNumericField()) {
            return VIEW_HOLDER_TEXT;
        }
        if (field.isSelectField()) {
            if (!field.isManyOptions()) {
                return field.isMultiSelect() ?
                        VIEW_HOLDER_SELECT_SINGLE_LINE : VIEW_HOLDER_RADIO_SINGLE_LINE;
            }
        }
        if (field.isRaduiButton()) {
            return VIEW_HOLDER_RADIO_SINGLE_LINE;
        }
        if (field.isSeparator()) {
            return VIEW_HOLDER_SEPARATOR;
        }
        if (field.isTickBox()) {
            return VIEW_HOLDER_TICK_BOX;
        }
        if (field.isPhotoUploadBox()) {
            return VIEW_HOLDER_PHOTO_UPLOAD_BOX;
        }
        if (field.isAudioUploadBox()) {
            return VIEW_HOLDER_AUDIO_UPLOAD_BOX;
        }
        if (field.isSubform()) {
            return VIEW_HOLDER_SUBFORM;
        }

        return VIEW_HOLDER_GENERIC;
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public List<CaseField> getFields() {
        return fields;
    }
}
