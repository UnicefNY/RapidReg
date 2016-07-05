package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
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
    public List<Case> getAllCasesOrderByDate(boolean isASC) {
        return isASC ? getCasesByDateASC() : getCasesByDateDES();
    }

    @Override
    public List<Case> getAllCasesOrderByAge(boolean isASC) {
        return isASC ? getCasesByAgeASC() : getCasesByAgeDES();

    }

    @Override
    public List<Case> getCaseListByConditionGroup(ConditionGroup conditionGroup) {
        return SQLite.select().from(Case.class)
                .where(conditionGroup)
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }

    private List<Case> getCasesByAgeASC() {
        return SQLite.select().from(Case.class).orderBy(Case_Table.age, true).queryList();
    }

    private List<Case> getCasesByAgeDES() {
        return SQLite.select().from(Case.class).orderBy(Case_Table.age, false).queryList();
    }

    private List<Case> getCasesByDateASC() {
        return SQLite.select().from(Case.class)
                .orderBy(Case_Table.registration_date, true).queryList();
    }

    private List<Case> getCasesByDateDES() {
        return SQLite.select().from(Case.class)
                .orderBy(Case_Table.registration_date, false).queryList();
    }
}
