package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.model.TracingForm;

public interface TracingFormDao {
    TracingForm getTracingForm();

    Blob getTracingFormContent();
}
