package org.unicef.rapidreg.base.record.recordphoto;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class RecordPhotoViewAdapter extends PagerAdapter {
    protected Context context;
    protected List<String> paths;

    public RecordPhotoViewAdapter(Context context, List<String> photos) {
        this.context = context;
        this.paths = photos;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
