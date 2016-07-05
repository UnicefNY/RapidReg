package org.unicef.rapidreg.childcase.media;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;

import java.util.List;

public class CasePhotoViewAdapter extends PagerAdapter {

    private Context context;
    private List<String> photos;

    public CasePhotoViewAdapter(Context context, List<String> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Point size = new Point();
        ((CasePhotoViewActivity) context).getWindowManager().getDefaultDisplay().getSize(size);

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.case_photo_view_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.case_photo_item);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        String filePath = photos.get(position);
        Glide.with(context).load(filePath).into(imageView);

        container.addView(itemView);
        return itemView;
    }
}
