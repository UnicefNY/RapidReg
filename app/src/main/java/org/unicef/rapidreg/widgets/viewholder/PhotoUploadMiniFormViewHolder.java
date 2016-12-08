package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class PhotoUploadMiniFormViewHolder extends BaseViewHolder<Field> {
    public static final String TAG = PhotoUploadMiniFormViewHolder.class.getSimpleName();

    @BindView(R.id.record_photo_view_slider)
    ViewPager viewPager;

    @BindView(R.id.indicator)
    CircleIndicator indicator;

    private Context context;

    public PhotoUploadMiniFormViewHolder(Context context, View itemView, ItemValuesMap itemValues) {
        super(context, itemView, itemValues);
        ButterKnife.bind(this, itemView);
        this.context = context;
    }

    @Override
    public void setValue(Field field) {
        viewPager.setAdapter(new RecordPhotoViewPagerAdapter());
        indicator.setViewPager(viewPager);
    }

    @Override
    public void setOnClickListener(Field field) {

    }

    @Override
    protected Boolean getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {
    }

    public class RecordPhotoViewPagerAdapter extends PagerAdapter {
        private List<RecordPhoto> photos = new ArrayList<>();

        public RecordPhotoViewPagerAdapter() {
            Long recordId = itemValues.getAsLong(ItemValues.RecordProfile.ID);

            if (recordId == null) {
                return;
            }

            if (context instanceof CaseActivity) {
                //TODO inject CasePhotoService
                for (Long id : CasePhotoService.getInstance().getIdsByCaseId(recordId)) {
                    photos.add(new CasePhoto(id));
                }
            } else {
                //TODO inject TracingPhotoService
                for (Long id : TracingPhotoService.getInstance().getIdsByTracingId(recordId)) {
                    photos.add(new TracingPhoto(id));
                }
            }
        }

        @Override
        public int getCount() {
            return photos.isEmpty() ? 1 : photos.size();
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
            View itemView = LayoutInflater.from(context).inflate(R.layout.record_photo_view_item,
                    container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.record_photo_item);

            if (photos.isEmpty()) {
                Glide.with(context).load(R.drawable.photo_placeholder).centerCrop().into(imageView);
            } else {
                Glide.with(context).load(photos.get(position)).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).centerCrop().into(imageView);
            }
            container.addView(itemView);
            return itemView;
        }
    }
}
