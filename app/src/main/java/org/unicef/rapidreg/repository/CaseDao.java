package org.unicef.rapidreg.repository;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Case;

import java.util.List;

public interface CaseDao {
    Case getCaseByUniqueId(String id);

    List<Case> getAllCasesOrderByDate(boolean isASC, String ownedBy, String url);

    List<Case> getAllCasesOrderByAge(boolean isASC, String ownedBy, String url);

    List<Case> getCaseListByConditionGroup(String ownedBy, String url, ConditionGroup conditionGroup);

    Case getCaseById(long caseId);

    Case getByInternalId(String id);

    Case save(Case childCase);

    Case update(Case childCase);

    Case deleteByRecordId(Long recordId);

    Case delete(Case deleteCase);

    List<Case> getALLSyncedRecords();
}
