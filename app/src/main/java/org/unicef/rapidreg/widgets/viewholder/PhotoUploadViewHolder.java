package org.unicef.rapidreg.widgets.viewholder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CasePhotoAdapter;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.service.CaseService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoUploadViewHolder extends BaseViewHolder<CaseField> {
    public static final String TAG = PhotoUploadViewHolder.class.getSimpleName();
    public final int IMAGE_OPEN = 1;

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
                if (position == photoGrid.getAdapter().getCount() - 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    caseActivity.startActivityForResult(intent, IMAGE_OPEN);
                }
            }
        });
        photoGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < photoGrid.getAdapter().getCount() - 1) {
                    dialog(i);
                }
                return true;
            }
        });
    }

    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(caseActivity);
        builder.setMessage("Are you sure to remove this photo?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CasePhotoAdapter casePhotoAdapter = (CasePhotoAdapter) photoGrid.getAdapter();
                CaseService.CaseValues.removePhoto(casePhotoAdapter.getAllItems().get(position));
                if (CaseService.CaseValues.getPhotoBitPaths().size() == 0) {
                    casePhotoAdapter.getAllItems().clear();
                    casePhotoAdapter.addItem(BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_camera));
                } else {
                    casePhotoAdapter.getAllItems().remove(position);
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
