package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.db.PrimeroDB;

@Table(database = PrimeroDB.class)
public class Incident extends RecordModel {

    public Incident() {
    }

    public Incident(long id) {
        super(id);
    }

}
