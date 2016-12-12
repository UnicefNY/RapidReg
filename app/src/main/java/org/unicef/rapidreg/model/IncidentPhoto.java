package org.unicef.rapidreg.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.IndexGroup;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.db.PrimeroDB;

@Table(database = PrimeroDB.class, indexGroups = {
        @IndexGroup(number = 1, name = "indexIncidentId")
})
@ModelContainer
public class IncidentPhoto extends RecordPhoto {
    @Index(indexGroups = 1)
    @Column
    long incidentId;

    public IncidentPhoto() {
    }

    public IncidentPhoto(long id) {
        super(id);
    }

    public void setIncidentId(Incident incident) {
        this.incidentId = incident.id;
    }

    @Override
    public String toString() {
        return "CasePhoto{" + "childCase=" + incidentId + "} " + super.toString();
    }
}
