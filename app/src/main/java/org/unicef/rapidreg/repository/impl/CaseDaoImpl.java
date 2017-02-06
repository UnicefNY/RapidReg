package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.Case_Table;
import org.unicef.rapidreg.repository.CaseDao;

import java.util.List;

public class CaseDaoImpl implements CaseDao {
    @Override
    public Case getCaseByUniqueId(String uniqueId) {
        return SQLite.select().from(Case.class).where(Case_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Case> getAllCasesOrderByDate(boolean isASC, String ownedBy, String url) {
        return isASC ? getCasesByDateASC(ownedBy, url) : getCasesByDateDES(ownedBy, url);
    }

    @Override
    public List<Case> getAllCasesOrderByAge(boolean isASC, String ownedBy, String url) {
        return isASC ? getCasesByAgeASC(ownedBy, url) : getCasesByAgeDES(ownedBy, url);
    }

    @Override
    public List<Case> getCaseListByConditionGroup(String ownedBy, String url, ConditionGroup conditionGroup) {
        return SQLite.select().from(Case.class)
                .where(conditionGroup)
                .and(Case_Table.owned_by.eq(ownedBy))
                .and(Case_Table.url.eq(url))
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Case getCaseById(long caseId) {
        return SQLite.select().from(Case.class).where(Case_Table.id.eq(caseId)).querySingle();
    }

    @Override
    public Case getByInternalId(String id) {
        return SQLite.select().from(Case.class).where(Case_Table._id.eq(id)).querySingle();
    }

    @Override
    public Case getFirst() {
        return SQLite.select().from(Case.class).querySingle();
    }

    @Override
    public Case save(Case childCase) {
        childCase.save();
        return childCase;
    }

    @Override
    public Case update(Case childCase) {
        childCase.update();
        return childCase;
    }

    private List<Case> getCasesByAgeASC(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.age, true)
                .queryList();
    }

    private List<Case> getCasesByAgeDES(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.age, false)
                .queryList();
    }

    private List<Case> getCasesByDateASC(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.registration_date, true)
                .queryList();
    }

    private List<Case> getCasesByDateDES(String ownedBy, String url) {
        return getCurrentServerUserCondition(ownedBy, url)
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }

    private Where<Case> getCurrentServerUserCondition(String ownedBy, String url) {
        return SQLite
                .select()
                .from(Case.class)
                .where(Case_Table.owned_by.eq(ownedBy))
                .and(Case_Table.url.eq(url));
    }
}
