package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.model.TracingForm_Table;
import org.unicef.rapidreg.model.Tracing_Table;
import org.unicef.rapidreg.repository.TracingFormDao;

public class TracingFormDaoImpl implements TracingFormDao {
    @Override
    public TracingForm getTracingForm(String apiBaseUrl) {
        return SQLite.select().from(TracingForm.class).where(TracingForm_Table.server_url.eq(apiBaseUrl)).querySingle();
    }
}
