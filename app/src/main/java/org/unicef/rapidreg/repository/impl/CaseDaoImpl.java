package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.repository.CaseDao;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.Case_Table;
import org.unicef.rapidreg.model.RecordModel;

import java.util.ArrayList;
import java.util.List;

public class CaseDaoImpl implements CaseDao {
    @Override
    public Case getCaseByUniqueId(String uniqueId) {
        return SQLite.select().from(Case.class).where(Case_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Case> getAllCasesOrderByDate(boolean isASC, String createdBy) {
        return isASC ? getCasesByDateASC(createdBy) : getCasesByDateDES(createdBy);
    }

    @Override
    public List<Case> getAllCasesOrderByAge(boolean isASC, String createdBy) {
        return isASC ? getCasesByAgeASC(createdBy) : getCasesByAgeDES(createdBy);

    }

    @Override
    public List<Case> getCaseListByConditionGroup(ConditionGroup conditionGroup) {
        return SQLite.select().from(Case.class)
                .where(conditionGroup)
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
    public List<Long> getAllIds() {
        List<Long> result = new ArrayList<>();
        FlowQueryList<Case> cases = SQLite.select().from(Case.class).flowQueryList();
        for (Case aCase : cases) {
            result.add(aCase.getId());
        }
        return result;
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

    private List<Case> getCasesByAgeASC(String createdBy) {
        return SQLite
                .select()
                .from(Case.class)
                .where(ConditionGroup.clause().and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CREATED_BY).build())
                        .eq(createdBy)))
                .orderBy(Case_Table.age, true)
                .queryList();
    }

    private List<Case> getCasesByAgeDES(String createdBy) {
        return SQLite
                .select()
                .from(Case.class)
                .where(ConditionGroup.clause().and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CREATED_BY).build())
                        .eq(createdBy)))
                .orderBy(Case_Table.age, false)
                .queryList();
    }

    private List<Case> getCasesByDateASC(String createdBy) {
        return SQLite
                .select()
                .from(Case.class)
                .where(ConditionGroup.clause().and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CREATED_BY).build())
                        .eq(createdBy)))
                .orderBy(Case_Table.registration_date, true)
                .queryList();
    }

    private List<Case> getCasesByDateDES(String createdBy) {
        return SQLite
                .select()
                .from(Case.class)
                .where(ConditionGroup.clause().and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CREATED_BY).build())
                        .eq(createdBy)))
                .orderBy(Case_Table.registration_date, false)
                .queryList();
    }
}
