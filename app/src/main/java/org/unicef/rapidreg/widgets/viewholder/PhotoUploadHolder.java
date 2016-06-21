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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoUploadHolder extends BaseViewHolder<CaseField> {
    public static final String TAG = PhotoUploadHolder.class.getSimpleName();

    @BindView(R.id.photo_grid)
    GridView photoGrid;

    private final int IMAGE_OPEN = 1;
    private Bitmap bmp;
    private ArrayList<HashMap<String, Object>> imageItem;
    private CasePhotoAdapter casePhotoAdapter;
    private CaseActivity caseActivity;

    public PhotoUploadHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        caseActivity = (CaseActivity) context;
    }


    @Override
    public void setValue(CaseField field) {
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo_camera);
        casePhotoAdapter = new CasePhotoAdapter(context, Arrays.asList(bmp));
        photoGrid.setAdapter(casePhotoAdapter);
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
    }

    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(caseActivity);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                casePhotoAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
