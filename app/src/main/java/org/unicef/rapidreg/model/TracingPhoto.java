package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.db.PrimeroDB;

@Table(database = PrimeroDB.class)
@ModelContainer
public class TracingPhoto extends RecordPhoto {

    @ForeignKey(references = {@ForeignKeyReference(
            columnName = "tracing_id",
            columnType = long.class,
            foreignKeyColumnName = "id"
    )})
    Tracing tracing;

    public Tracing getTracing() {
        return tracing;
    }

    public void setTracing(Tracing tracing) {
        this.tracing = tracing;
    }

    @Override
    public String toString() {
        return "TracingPhoto{" +
                "tracing=" + tracing +
                "} " + super.toString();
    }
}
