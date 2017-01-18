package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.repository.TracingFormDao;
import org.unicef.rapidreg.model.TracingForm;

public class TracingFormDaoImpl implements TracingFormDao {
    @Override
    public TracingForm getTracingForm() {
        return SQLite.select().from(TracingForm.class).querySingle();
    }
}
