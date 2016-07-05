package org.unicef.rapidreg.childcase.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;

import java.util.List;

public class CasePhotoAdapter extends BaseAdapter {
    private final static int MAX = 4;

    private Context context;
    private List<String> paths;


    public CasePhotoAdapter(Context context, List<String> paths) {
        this.context = context;
        this.paths = paths;
    }

    public void addItem(String path) {
        paths.add(path);
    }

    public void addItem(long photoId) {
        String dbPath = String.valueOf("photo_from_db_" + photoId);
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

        Glide.with(imageView.getContext()).load(paths.get(i)).into(imageView);
        return itemView;
    }

    public boolean isFull() {
        return getCount() >= MAX;
    }

    public List<String> getAllItems() {
        return paths;
    }
}
