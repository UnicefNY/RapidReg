package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CaseDao;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.Case_Table;

import java.util.List;

public class CaseDaoImpl implements CaseDao {

    @Override
    public Case getCaseByUniqueId(String uniqueId) {
        return SQLite.select().from(Case.class).where(Case_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Case> getAllCases() {
        return SQLite.select().from(Case.class).orderBy(Case_Table.created_at, false).queryList();
    }

    @Override
    public List<Case> getAllCasesOrderByAge() {
        return SQLite.select().from(Case.class).orderBy(Case_Table.age, true).queryList();
    }

}
