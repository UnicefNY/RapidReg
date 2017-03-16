package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoViewActivity;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoViewActivity;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;
import org.unicef.rapidreg.widgets.dialog.PhotoUploadDialog;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoUploadViewHolder extends BaseViewHolder<Field> {
    public static final String TAG = PhotoUploadViewHolder.class.getSimpleName();
    public static final int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_CODE_CAMERA = 2;

    @BindView(R.id.photo_grid)
    GridView photoGrid;

    @BindView(R.id.no_photo_promote_view)
    TextView noPhotoPromoteView;

    private Context context;

    private RecordPhotoAdapter adapter;

    public PhotoUploadViewHolder(Context context, View itemView, ItemValuesMap itemValues, RecordPhotoAdapter adapter) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.adapter = adapter;

        photoGrid.setAdapter(adapter);
        setViewPhotoListener();
    }

    @Override
    public void setValue(Field field) {
        setAddPhotoButtonIcon();
    }


    private void setAddPhotoButtonIcon() {
        if (((RecordActivity) context).getCurrentFeature().isDetailMode()) {
            if (adapter.isEmpty()) {
                noPhotoPromoteView.setVisibility(View.VISIBLE);
                photoGrid.setVisibility(View.GONE);
            }
            return;
        }
    }

    private void setViewPhotoListener() {
        photoGrid.setOnItemClickListener((parent, v, position, id) -> {
            if (((RecordActivity) context).getCurrentFeature().isDetailMode()
                    || adapter.isFull() || position < adapter.getCount() - 1) {
                showViewPhotoDialog(position);
            } else {
                showAddPhotoOptionDialog();
            }
        });
    }

    @Override
    public void setOnClickListener(Field field) {
        setViewPhotoListener();
        photoGrid.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if (adapter.isFull() || i != adapter.getCount() - 1) {
                showDeletionConfirmDialog(i);
            }
            return true;
        });
    }

    @Override
    protected Boolean getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    @Override
    public void setFieldClickable(boolean clickable) {

    }

    private void showViewPhotoDialog(final int position) {
        Class clz = context instanceof CaseActivity ? CasePhotoViewActivity.class : TracingPhotoViewActivity.class;
        Intent intent = new Intent(context, clz);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("photos", (ArrayList<String>) adapter.getAllItems());
        context.startActivity(intent);
    }

    private void showAddPhotoOptionDialog() {
        PhotoUploadDialog photoUploadDialog = new PhotoUploadDialog(context);
        photoUploadDialog.setItemCameraOnClickLisener(v -> {
            Uri saveUri = Uri.fromFile(new File(PhotoConfig.MEDIA_PATH_FOR_CAMERA));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
            ((RecordActivity) context).startActivityForResult(intent, REQUEST_CODE_CAMERA);
            photoUploadDialog.dismiss();
        });
        photoUploadDialog.setItemGalleryOnClickLisener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((RecordActivity) context).startActivityForResult(intent, REQUEST_CODE_GALLERY);
            photoUploadDialog.dismiss();
        });
        photoUploadDialog.setItemCancelOnClickLisener(v -> photoUploadDialog.dismiss());
        photoUploadDialog.show();
    }

    private void showDeletionConfirmDialog(final int position) {
        MessageDialog messageDialog = new MessageDialog(context);
        messageDialog.setMessage(R.string.remove_photo_confirmation);
        messageDialog.setPositiveButton(R.string.ok, v -> {
            messageDialog.dismiss();
            RecordPhotoAdapter casePhotoAdapter = (RecordPhotoAdapter) photoGrid.getAdapter();
            casePhotoAdapter.removeItem(position);
            casePhotoAdapter.notifyDataSetChanged();
        });
        messageDialog.setNegativeButton(R.string.cancel, v -> messageDialog.dismiss());
        messageDialog.show();
    }
}
