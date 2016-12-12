package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.PrimeroDB;

@Table(database = PrimeroDB.class)
public class IncidentForm extends BaseForm {
    public IncidentForm() {
    }

    public IncidentForm(Blob form) {
        super(form);
    }

}
