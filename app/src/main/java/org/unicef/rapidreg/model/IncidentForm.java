package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@Table(database = PrimeroDatabaseConfiguration.class)
public class IncidentForm extends BaseForm {
    public IncidentForm() {
    }

    public IncidentForm(Blob form) {
        super(form);
    }

}
