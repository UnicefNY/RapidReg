package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Case;

import java.util.List;

public interface CaseDao {
    Case getCaseByUniqueId(String id);

    List<Case> getAllCasesOrderByDate(boolean isASC, String createdBy);

    List<Case> getAllCasesOrderByAge(boolean isASC, String createdBy);

    List<Case> getCaseListByConditionGroup(ConditionGroup conditionGroup);

    Case getCaseById(long caseId);

    Case getByInternalId(String id);

    Case getFirst();

    List<Long> getAllIds();

    Case save(Case childCase);

    Case update(Case childCase);
}
