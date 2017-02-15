package org.unicef.rapidreg.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.unicef.rapidreg.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoUploadDialog extends Dialog {
    @BindView(R.id.dialog_item_camera)
    TextView dialogItemCameraTV;

    @BindView(R.id.dialog_item_gallery)
    TextView dialogItemGalleryTV;

    @BindView(R.id.dialog_item_cancel)
    TextView dialogItemCancelTV;

    public PhotoUploadDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_photo_upload);
        ButterKnife.bind(this);
    }

    public void setItemCameraOnClickLisener(View.OnClickListener lisener){
        dialogItemCameraTV.setOnClickListener(lisener);
    }

    public void setItemGalleryOnClickLisener(View.OnClickListener lisener){
        dialogItemGalleryTV.setOnClickListener(lisener);
    }

    public void setItemCancelOnClickLisener(View.OnClickListener lisener){
        dialogItemCancelTV.setOnClickListener(lisener);
    }
}
