package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.IndexGroup;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@Table(database = PrimeroDatabaseConfiguration.class, indexGroups = {
        @IndexGroup(number = 1, name = "indexCaseId")
})
@ModelContainer
public class CasePhoto extends RecordPhoto {

    @Index(indexGroups = 1)
    @Column
    long caseId;

    public CasePhoto() {
    }

    public CasePhoto(long id) {
        super(id);
    }

    public void setCase(Case childCase) {
        this.caseId = childCase.id;
    }

    @Override
    public String toString() {
        return "CasePhoto{" +
                "childCase=" + caseId +
                "} " + super.toString();
    }
}
