package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Case;

import java.util.List;

public interface CaseDao {
    Case getCaseByUniqueId(String id);

    List<Case> getAllCasesOrderByDate(boolean isASC);

    List<Case> getAllCasesOrderByAge(boolean isASC);

    List<Case> getCaseListByConditionGroup(ConditionGroup conditionGroup);

    Case getCaseById(long caseId);
}
