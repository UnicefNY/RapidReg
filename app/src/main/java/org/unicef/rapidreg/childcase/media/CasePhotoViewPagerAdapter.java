package org.unicef.rapidreg.childcase.media;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.service.cache.CasePhotoCache;
import org.unicef.rapidreg.utils.ImageCompressUtil;

import java.util.List;

public class CasePhotoViewPagerAdapter extends FragmentPagerAdapter {


    private Context context;

    public CasePhotoViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public int getCount() {
        return CasePhotoCache.size();
    }


    @Override
    public Fragment getItem(int position) {
        Log.i("sjyuan", "getItem = " + position);
        return new SwipeFragment(context, position);
    }

    public class SwipeFragment extends Fragment {
        private Context context;
        private int position;

        public SwipeFragment(Context context, int position) {
            this.context = context;
            this.position = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i("sjyuan", "onCreateView position = " + position);
            View swipeView = inflater.inflate(R.layout.case_photo_view_item, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.case_photo_item);

            Point size = new Point();
            ((CaseActivity) context).getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int) context.getResources()
                    .getDimension(R.dimen.case_photo_view_pager_height_mini_form);

//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            List<String> previousPhotoPaths = CasePhotoCache.getPhotosPaths();
            Bitmap image = ImageCompressUtil.getThumbnail(previousPhotoPaths.get(position), width, height);
            imageView.setImageBitmap(image);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(
                            "file://" + CasePhotoCache.getPhotosPaths().get(position)), "image/*");
                    context.startActivity(intent);
                }
            });
            return swipeView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }
    }
}
