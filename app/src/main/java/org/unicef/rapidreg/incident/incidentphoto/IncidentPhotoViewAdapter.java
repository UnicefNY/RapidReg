package org.unicef.rapidreg.incident.incidentphoto;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoViewAdapter;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.service.IncidentPhotoService;

import javax.inject.Inject;

public class IncidentPhotoViewAdapter extends RecordPhotoViewAdapter {
    @Inject
    IncidentPhotoService incidentPhotoService;

    @Inject
    public IncidentPhotoViewAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Point size = new Point();
        ((IncidentPhotoViewActivity) context).getWindowManager().getDefaultDisplay().getSize(size);

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.record_photo_view_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.record_photo_item);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        String path = paths.get(position);
        try {
            long recordId = Long.parseLong(path);
            Glide.with(context).load(incidentPhotoService.getById(recordId)
                    .getPhoto().getBlob()).into(imageView);
        } catch (NumberFormatException e) {
            Glide.with(context).load(path).into(imageView);
        }
        container.addView(itemView);
        return itemView;
    }
}
