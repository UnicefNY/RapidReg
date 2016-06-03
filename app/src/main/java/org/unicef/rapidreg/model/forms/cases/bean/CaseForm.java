package org.unicef.rapidreg.model.forms.cases.bean;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.unicef.rapidreg.db.PrimeroDB;

import java.util.ArrayList;
import java.util.List;

@Table(database = PrimeroDB.class)
public class CaseForm extends BaseModel {
    @PrimaryKey(autoincrement = true)
    long id;

    List<CaseSection> sections = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSections(List<CaseSection> sections) {
        this.sections = sections;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "sections")
    public List<CaseSection> getSections() {
        if (sections == null || sections.isEmpty()) {
            sections = SQLite.select()
                    .from(CaseSection.class)
                    .where(CaseSection_Table.form_id.eq(id))
                    .queryList();
        }
        return sections;
    }
}
