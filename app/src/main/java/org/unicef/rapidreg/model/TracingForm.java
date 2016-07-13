package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.PrimeroDB;

@Table(database = PrimeroDB.class)
public class TracingForm extends BaseForm {
    public TracingForm() {
    }

    public TracingForm(Blob form) {
        super(form);
    }
}
