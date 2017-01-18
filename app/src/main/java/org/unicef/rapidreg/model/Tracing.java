package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@Table(database = PrimeroDatabaseConfiguration.class)
public class Tracing extends RecordModel {
    public Tracing() {
    }

    public Tracing(long id) {
        super(id);
    }
}
