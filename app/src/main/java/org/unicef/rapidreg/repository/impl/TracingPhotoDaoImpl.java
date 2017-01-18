package org.unicef.rapidreg.repository.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.model.TracingPhoto_Table;
import org.unicef.rapidreg.utils.ImageCompressUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TracingPhotoDaoImpl implements TracingPhotoDao {
    @Override
    public TracingPhoto getFirst(long tracingId) {
        return SQLite.select().from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .querySingle();
    }

    @Override
    public List<Long> getIdsByTracingId(long tracingId) {
        TracingPhoto_Table.index_indexTracingId.createIfNotExists();

        List<Long> result = new ArrayList<>();
        List<TracingPhoto> tracingPhotos = SQLite.select(TracingPhoto_Table.id)
                .from(TracingPhoto.class)
                .indexedBy(TracingPhoto_Table.index_indexTracingId)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .and(TracingPhoto_Table.photo.isNotNull())
                .queryList();
        for (TracingPhoto tracingPhoto : tracingPhotos) {
            result.add(tracingPhoto.getId());
        }
        return result;
    }

    @Override
    public TracingPhoto getById(long id) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public long countUnSynced(long tracingId) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .and(TracingPhoto_Table.photo.isNotNull())
                .and(TracingPhoto_Table.isSynced.is(false))
                .count();
    }

    @Override
    public void deleteByTracingId(long tracingId) {
        SQLite.delete().from(TracingPhoto.class).where(TracingPhoto_Table.tracingId.eq(tracingId)).execute();
    }

    @Override
    public Tracing save(Tracing tracing, List<String> photoPaths) throws IOException {
        for (int i = 0; i < photoPaths.size(); i++) {
            TracingPhoto tracingPhoto = generateSavePhoto(tracing, photoPaths, i);
            tracingPhoto.setKey(UUID.randomUUID().toString());
            tracingPhoto.save();
        }
        return tracing;
    }

    @Override
    public Tracing update(Tracing tracing, List<String> photoPaths) throws IOException {
        int previousCount = getIdsByTracingId(tracing.getId()).size();

        if (previousCount < photoPaths.size()) {
            for (int i = 0; i < previousCount; i++) {
                TracingPhoto tracingPhoto = generateUpdatePhoto(tracing, photoPaths, i);
                tracingPhoto.update();
            }
            for (int i = previousCount; i < photoPaths.size(); i++) {
                TracingPhoto tracingPhoto = generateSavePhoto(tracing, photoPaths, i);
                if (tracingPhoto.getId() == 0) {
                    tracingPhoto.save();
                } else {
                    tracingPhoto.update();
                }
            }
        } else {
            for (int i = 0; i < photoPaths.size(); i++) {
                TracingPhoto tracingPhoto = generateUpdatePhoto(tracing, photoPaths, i);
                tracingPhoto.update();
            }
            for (int i = photoPaths.size(); i < previousCount; i++) {
                TracingPhoto tracingPhoto =
                        getByTracingIdAndOrder(tracing.getId(), i + 1);
                tracingPhoto.setPhoto(null);
                tracingPhoto.update();
            }
        }
        return tracing;
    }

    @Override
    public TracingPhoto getByTracingIdAndOrder(long tracingId, int order) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracingId.eq(tracingId))
                .and(TracingPhoto_Table.order.eq(order))
                .querySingle();
    }

    private TracingPhoto generateSavePhoto(Tracing parent, List<String> photoPaths, int index) throws IOException {

        TracingPhoto tracingPhoto
                = getByTracingIdAndOrder(parent.getId(), index + 1);
        if (tracingPhoto == null) {
            tracingPhoto = new TracingPhoto();
        }
        String filePath = photoPaths.get(index);
        tracingPhoto.setPhoto(ImageCompressUtil.readImageFile(filePath));
        tracingPhoto.setTracingId(parent);
        tracingPhoto.setOrder(index + 1);
        tracingPhoto.setKey(UUID.randomUUID().toString());
        return tracingPhoto;
    }

    @NonNull
    private TracingPhoto generateUpdatePhoto(Tracing tracing, List<String> photoPaths, int index) throws IOException {
        TracingPhoto tracingPhoto;
        String filePath = photoPaths.get(index);
        try {
            long photoId = Long.parseLong(filePath);
            tracingPhoto = getById(photoId);
        } catch (NumberFormatException e) {
            tracingPhoto = new TracingPhoto();
            tracingPhoto.setTracingId(tracing);
            tracingPhoto.setPhoto(ImageCompressUtil.readImageFile(filePath));
        }
        tracingPhoto.setId(getByTracingIdAndOrder(tracing.getId(), index + 1).getId());
        tracingPhoto.setOrder(index + 1);
        tracingPhoto.setKey(UUID.randomUUID().toString());
        return tracingPhoto;
    }
}
