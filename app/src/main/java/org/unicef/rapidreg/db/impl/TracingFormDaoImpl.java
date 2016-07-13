package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.TracingFormDao;
import org.unicef.rapidreg.model.TracingForm;

public class TracingFormDaoImpl implements TracingFormDao {
    @Override
    public TracingForm getTracingForm() {
        return SQLite.select().from(TracingForm.class).querySingle();
    }

    @Override
    public Blob getTracingFormContent() {
        TracingForm tracingForm = SQLite.select().from(TracingForm.class).querySingle();

        return tracingForm == null ? null : tracingForm.getForm();
    }
}
