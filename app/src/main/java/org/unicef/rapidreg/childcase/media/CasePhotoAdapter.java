package org.unicef.rapidreg.childcase.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.unicef.rapidreg.R;

import java.util.Arrays;
import java.util.List;

public class CasePhotoAdapter extends BaseAdapter {
    private Context context;
    private List<Bitmap> photos;

    public CasePhotoAdapter(Context context, List<Bitmap> photos) {
        this.context = context;
        this.photos = photos;
    }

    public void addItem(Bitmap item) {
        photos.add(item);
    }

    public void addItems(List<Bitmap> items) {
        for (Bitmap item : items) {
            addItem(item);
        }
    }

    public List<Bitmap> getAllItems() {
        return photos;
    }

    public void removeItem(int index) {
        photos.remove(index);
    }

    public void clearItems() {
        photos.clear();
    }

    public void addItems(Bitmap... items) {
        addItems(Arrays.asList(items));
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int i) {
        return photos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.form_photo_item, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.photo_item);
        imageView.setImageBitmap(photos.get(i));
        return itemView;
    }
}
