package org.unicef.rapidreg.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.RecordPhoto;

import java.util.ArrayList;
import java.util.List;

public abstract class RecordPhotoAdapter extends BaseAdapter {
    private final static int MAX = 10;
    private Context context;
    private List<String> paths;

    public RecordPhotoAdapter(Context context, List<String> paths) {
        this.context = context;
        this.paths = paths;
    }

    public void addItem(String path) {
        paths.add(path);
    }

    public void removeItem(int position) {
        paths.remove(position);
    }

    @Override
    public int getCount() {
        if (((RecordActivity) context).getCurrentFeature().isDetailMode()) {
            return paths.size();
        }
        if (paths.size() >= MAX){
            return paths.size();
        }
        return paths.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return paths.get(i);
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
        int width = 80;
        if (paths.size() == 0) {
            Glide.with(context).load(R.drawable.photo_camera).override(width, width).centerCrop().into(imageView);
            return itemView;
        }
        boolean isAddImageButtonIndex = paths.size() < MAX && i == paths.size();
        if (isAddImageButtonIndex){
            Glide.with(context).load(R.drawable.photo_add).override(width, width).centerCrop().into(imageView);
            return itemView;
        }
        String path = paths.get(i);
        try {
            long photoId = Long.parseLong(path);
            Glide.with(context).load(getPhotoById(photoId).getPhoto().getBlob())
                    .override(width, width).centerCrop().into(imageView);
        } catch (NumberFormatException e) {
            Glide.with(imageView.getContext()).load(path).override(width, width).centerCrop().into(imageView);
        }
        return itemView;
    }

    public boolean isFull() {
        return paths.size() >= MAX;
    }

    public List<String> getAllItems() {
        return paths;
    }

    protected abstract RecordPhoto getPhotoById(long id);
}
