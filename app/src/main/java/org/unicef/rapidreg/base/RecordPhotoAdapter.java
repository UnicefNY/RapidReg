package org.unicef.rapidreg.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.service.CasePhotoService;

import java.util.List;

public class RecordPhotoAdapter extends BaseAdapter {
    private final static int MAX = 4;
    private Context context;
    private List<String> paths;


    public RecordPhotoAdapter(Context context, List<String> paths) {
        this.context = context;
        this.paths = paths;
    }

    public void addItem(String path) {
        paths.add(path);
    }

    public void addItem(long photoId) {
        String dbPath = String.valueOf(photoId);
        paths.add(dbPath);
    }

    public void removeItem(int position) {
        paths.remove(position);
    }

    @Override
    public int getCount() {
        return paths.size();
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
        String path = paths.get(i);

        int width = 80;
        try {
            long photoId = Long.parseLong(path);
            Glide.with(context).load(CasePhotoService.getInstance().getById(photoId)
                    .getPhoto().getBlob()).override(width, width).centerCrop().into(imageView);
        } catch (NumberFormatException e) {
            Glide.with(imageView.getContext()).load(path).override(width, width).centerCrop().into(imageView);
        }
        return itemView;
    }

    public boolean isFull() {
        return getCount() >= MAX;
    }

    public List<String> getAllItems() {
        return paths;
    }
}
