package org.unicef.rapidreg;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.module.GlideModule;

import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.TracingPhotoService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.inject.Inject;

public class PrimeroGlideModule implements GlideModule {

    @Inject
    CasePhotoService casePhotoService;

    @Inject
    TracingPhotoService tracingPhotoService;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {}

    @Override
    public void registerComponents(Context context, Glide glide) {

        PrimeroApplication.get(context).getComponent().inject(this);

        glide.register(RecordPhoto.class, InputStream.class, new ModelLoaderFactory<RecordPhoto, InputStream>() {
            @Override
            public ModelLoader<RecordPhoto, InputStream> build(Context context, GenericLoaderFactory factories) {
                return new ModelLoader<RecordPhoto, InputStream>() {
                    @Override
                    public DataFetcher<InputStream> getResourceFetcher(final RecordPhoto model, int width, int height) {
                        return new DataFetcher<InputStream>() {
                            @Override
                            public InputStream loadData(Priority priority) throws Exception {
                                byte[] blob = null;
                                if (model instanceof CasePhoto) {
                                    blob = casePhotoService.getById(model.getId()).getPhoto().getBlob();
                                } else if (model instanceof TracingPhoto) {
                                    blob = tracingPhotoService.getById(model.getId()).getPhoto().getBlob();
                                }
                                return new ByteArrayInputStream(blob);
                            }

                            @Override
                            public void cleanup() {

                            }

                            @Override
                            public String getId() {
                                return String.valueOf(model.getId());
                            }

                            @Override
                            public void cancel() {

                            }
                        };
                    }
                };
            }

            @Override
            public void teardown() {

            }
        });

        glide.register(RecordModel.class, InputStream.class, new ModelLoaderFactory<RecordModel, InputStream>() {
            @Override
            public ModelLoader<RecordModel, InputStream> build(Context context, GenericLoaderFactory factories) {
                return new ModelLoader<RecordModel, InputStream>() {
                    @Override
                    public DataFetcher<InputStream> getResourceFetcher(final RecordModel model, int width, int height) {
                        return new DataFetcher<InputStream>() {
                            @Override
                            public InputStream loadData(Priority priority) throws Exception {
                                byte[] blob = null;
                                if (model instanceof Case){
                                    blob = casePhotoService.getFirst(model.getId()).getPhoto().getBlob();
                                }else if (model instanceof Tracing){
                                    blob = tracingPhotoService.getFirst(model.getId()).getPhoto().getBlob();
                                }
                                return new ByteArrayInputStream(blob);
                            }

                            @Override
                            public void cleanup() {

                            }

                            @Override
                            public String getId() {
                                return String.valueOf(model.getId());
                            }

                            @Override
                            public void cancel() {

                            }
                        };
                    }
                };
            }

            @Override
            public void teardown() {

            }
        });

    }
}
