package org.unicef.rapidreg.childcase.media;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.service.cache.CasePhotoCache;
import org.unicef.rapidreg.utils.ImageCompressUtil;

import java.util.List;

public class CasePhotoViewActivity extends FragmentActivity {
    private ViewPager viewPager;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.case_photo_view_slider);
        viewPager = (ViewPager) findViewById(R.id.case_photo_view_slider);
        viewPager.setAdapter(new MyAdapter());
//        viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }

    public class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return CasePhotoCache.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            Log.i("sjyuan","size = " + size.toString());
            int width = 400;
            int height = 600;

            List<String> previousPhotoPaths = CasePhotoCache.getPhotosPaths();

            View swipeView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.case_photo_view_item, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.case_photo_item);

            Bitmap image = ImageCompressUtil.getThumbnail(previousPhotoPaths.get(position), width, height);
            imageView.setImageBitmap(image);

            container.addView(imageView);
            return imageView;
        }
    }

}
