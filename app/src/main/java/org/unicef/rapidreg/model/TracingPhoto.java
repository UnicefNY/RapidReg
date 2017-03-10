package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.IndexGroup;
import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@Table(database = PrimeroDatabaseConfiguration.class, indexGroups = {
        @IndexGroup(number = 1, name = "indexTracingId")
})
public class TracingPhoto extends RecordPhoto {

    @Index(indexGroups = 1)
    @Column
    long tracingId;

    public TracingPhoto() {
    }

    public TracingPhoto(long id) {
        super(id);
    }

    public void setTracing(Tracing tracing) {
        this.tracingId = tracing.getId();
    }

    @Override
    public String toString() {
        return "TracingPhoto{" +
                "tracingId=" + tracingId +
                "} " + super.toString();
    }
}
