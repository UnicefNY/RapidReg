package org.unicef.rapidreg.widgets.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CasePhotoAdapter;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.CaseService;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoUploadViewHolder extends BaseViewHolder<CaseField> {
    public static final String TAG = PhotoUploadViewHolder.class.getSimpleName();
    public static final int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_CODE_CAMERA = 2;

    @BindView(R.id.photo_grid)
    GridView photoGrid;
    private CaseActivity caseActivity;

    public PhotoUploadViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        caseActivity = (CaseActivity) context;
    }


    @Override
    public void setValue(CaseField field) {
        List<Bitmap> previousPhotos = CaseService.CaseValues.getPhotosBits();

        Bitmap addPhotoIcon;
        if (previousPhotos.size() >= 4) {
            photoGrid.setAdapter(new CasePhotoAdapter(context, previousPhotos));
            return;
        }

        if (previousPhotos.size() > 0) {
            addPhotoIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_add);
        } else {
            addPhotoIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_camera);
        }
        previousPhotos.add(addPhotoIcon);
        photoGrid.setAdapter(new CasePhotoAdapter(context, previousPhotos));

    }

    @Override
    public void setOnClickListener(CaseField field) {
        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                boolean isMax = CaseService.CaseValues.getPhotoBitPaths().size() == 4;
                boolean isAddPhotoGridClicked = (position == photoGrid.getAdapter().getCount() - 1);

                if (isMax || !isAddPhotoGridClicked) {
                    showViewPhotoDialog(position);
                } else {
                    showAddPhotoOptionDialog(parent);
                }
            }
        });

        photoGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean isMax = CaseService.CaseValues.getPhotoBitPaths().size() == 4;
                boolean isPhotoGridClicked = (i < photoGrid.getAdapter().getCount() - (isMax ? 0 : 1));

                if (isPhotoGridClicked) {
                    showDeletionConfirmDialog(i);
                }
                return true;
            }
        });
    }

    protected void showAddPhotoOptionDialog(AdapterView<?> parent) {
        final CharSequence[] items = {"From Camera", "From Gallery",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("From Camera")) {
                    Uri saveUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
                    caseActivity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } else if (items[item].equals("From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    caseActivity.startActivityForResult(intent, REQUEST_CODE_GALLERY);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void showViewPhotoDialog(final int position) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(
                "file://" + CaseService.CaseValues.getPhotosPaths().get(position)), "image/*");
        context.startActivity(intent);
    }

    protected void showDeletionConfirmDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(caseActivity);
        builder.setMessage("Are you sure to remove this photo?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CasePhotoAdapter casePhotoAdapter = (CasePhotoAdapter) photoGrid.getAdapter();
                CaseService.CaseValues.removePhoto(casePhotoAdapter.getAllItems().get(position));

                if (CaseService.CaseValues.getPhotoBitPaths().size() == 0) {
                    casePhotoAdapter.clearItems();
                    Bitmap addPhotoIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_camera);
                    casePhotoAdapter.addItem(addPhotoIcon);
                } else if (CaseService.CaseValues.getPhotoBitPaths().size() == 3) {
                    casePhotoAdapter.removeItem(position);
                    Bitmap addPhotoIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_add);
                    casePhotoAdapter.addItem(addPhotoIcon);
                } else {
                    casePhotoAdapter.removeItem(position);
                }

                casePhotoAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
