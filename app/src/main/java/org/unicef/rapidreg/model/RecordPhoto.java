package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseModel;

public class RecordPhoto extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    Blob photo;

    @Column
    int order;


    @Column
    Blob thumbnail;

    public RecordPhoto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Blob getPhoto() {
        return photo;
    }

    public void setPhoto(Blob photo) {
        this.photo = photo;
    }

    public Blob getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Blob thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "RecordPhoto{" +
                "id=" + id +
                ", photo=" + photo +
                ", order=" + order +
                ", thumbnail=" + thumbnail +
                '}';
    }
}

