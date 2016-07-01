package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.CasePhotoCache;
import org.unicef.rapidreg.utils.ImageCompressUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoUploadMiniFormViewHolder extends BaseViewHolder<CaseField> implements ViewPager.OnPageChangeListener {
    public static final String TAG = PhotoUploadMiniFormViewHolder.class.getSimpleName();

    @BindView(R.id.case_photo_view_slider)
    ViewPager viewPager;

    @BindView(R.id.dot_view)
    ViewGroup dotViewGroup;

    private CaseActivity caseActivity;

    private ImageView[] tips;

    private boolean isPhotosPrepared;

    public PhotoUploadMiniFormViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        caseActivity = (CaseActivity) context;
    }

    @Override
    public void setValue(CaseField field) {
        viewPager.setAdapter(new CasePhotoViewPagerAdapter());
        viewPager.addOnPageChangeListener(this);
        if (!isPhotosPrepared) {
            initDots();
        }
    }


    private void initDots() {
        tips = new ImageView[CasePhotoCache.size()];
        for (int i = 0; i < tips.length; i++) {
            tips[i] = new ImageView(context);
            tips[i].setLayoutParams(new LayoutParams(10, 10));
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            dotViewGroup.addView(tips[i], layoutParams);
        }
    }


    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < tips.length; i++) {
            if (i == position) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void setOnClickListener(CaseField field) {

    }

    @Override
    protected String getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class CasePhotoViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return CasePhotoCache.size() == 0 ? 1 : CasePhotoCache.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i("sjyuan", "isPhotosPrepared = " + isPhotosPrepared + ", position = " + position);
            View itemView = LayoutInflater.from(context).inflate(R.layout.case_photo_view_item, container, false);
            container.addView(itemView);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.case_photo_item);

            if (CasePhotoCache.size() == 0){
                imageView.setImageResource(R.drawable.photo_placeholder);
                return itemView;
            }

            if (isPhotosPrepared) {
                renderPhoto(imageView, position);
            } else {
                new UpdateImageViewTask(imageView, position).execute();
            }
            return itemView;
        }


        private void renderPhoto(ImageView imageView, int position) {
            Point size = new Point();
            caseActivity.getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = (int) context.getResources()
                    .getDimension(R.dimen.case_photo_view_pager_height_mini_form);
            List<String> previousPhotoPaths = CasePhotoCache.getPhotosPaths();
            Bitmap image = ImageCompressUtil.getThumbnail(previousPhotoPaths.get(position), width, height);
            imageView.setImageBitmap(image);
        }

        private class UpdateImageViewTask extends AsyncTask<String, Integer, Integer> {
            private final ImageView imageView;
            private final int position;

            public UpdateImageViewTask(ImageView imageView, int position) {
                this.imageView = imageView;
                this.position = position;
            }

            @Override
            protected Integer doInBackground(String... params) {
                if (!isPhotosPrepared) {
                    synchronized (CasePhotoViewPagerAdapter.this) {
                        if (!isPhotosPrepared) {
                            String caseIdStr = CaseFieldValueCache.getProfileValue(CaseFieldValueCache.CaseProfile.ID);
                            long caseId = Long.parseLong(caseIdStr);
                            List<CasePhoto> allCasePhotos = CasePhotoService.getInstance().getAllCasePhotos(caseId);
                            CasePhotoCache.syncCachingPhotos(allCasePhotos);
                            isPhotosPrepared = true;
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                renderPhoto(imageView, position);
                CasePhotoViewPagerAdapter.this.notifyDataSetChanged();
            }
        }
    }

}
