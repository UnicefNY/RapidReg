package org.unicef.rapidreg.widgets.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.PhotoConfig;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordPhotoAdapter;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CasePhotoViewActivity;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoViewActivity;

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
        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (((RecordActivity) context).getCurrentFeature().isDetailMode()
                        || adapter.isFull() || position < adapter.getCount() - 1) {
                    showViewPhotoDialog(position);
                } else {
                    showAddPhotoOptionDialog();
                }
            }
        });
    }

    @Override
    public void setOnClickListener(Field field) {
        setViewPhotoListener();
        photoGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter.isFull() || i != adapter.getCount() - 1) {
                    showDeletionConfirmDialog(i);
                }
                return true;
            }
        });
    }

    @Override
    protected Boolean getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    private void showViewPhotoDialog(final int position) {
        Class clz = context instanceof CaseActivity ? CasePhotoViewActivity.class : TracingPhotoViewActivity.class;
        Intent intent = new Intent(context, clz);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("photos", (ArrayList<String>) adapter.getAllItems());
        context.startActivity(intent);
    }

    private void showAddPhotoOptionDialog() {
        final String fromCameraItem = "From Camera";
        final String fromGalleryItem = "From Gallery";
        final String cancelItem = "Cancel";
        final String[] items = {fromCameraItem, fromGalleryItem, cancelItem};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (fromCameraItem.equals(items[item])) {
                    Uri saveUri = Uri.fromFile(new File(PhotoConfig.MEDIA_PATH_FOR_CAMERA));
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
                    ((RecordActivity) context).startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } else if (fromGalleryItem.equals(items[item])) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    ((RecordActivity) context).startActivityForResult(intent, REQUEST_CODE_GALLERY);
                } else if (cancelItem.equals(items[item])) {
                    dialog.dismiss();
                }
            }
        }).show();
    }

    private void showDeletionConfirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.remove_photo_confirmation);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                RecordPhotoAdapter casePhotoAdapter = (RecordPhotoAdapter) photoGrid.getAdapter();
                casePhotoAdapter.removeItem(position);
                casePhotoAdapter.notifyDataSetChanged();

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
