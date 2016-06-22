package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.Case;

import java.util.List;

public interface CaseDao {
    Case getCaseByUniqueId(String id);

    List<Case> getAllCasesOrderByDate(boolean isASC);

    List<Case> getAllCasesOrderByAge(boolean isASC);
}
