package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@Table(database = PrimeroDatabaseConfiguration.class)
public class Case extends RecordModel {
    public Case() {
    }

    public Case(long id) {
        super(id);
    }
}
