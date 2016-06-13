package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.Case;

public interface CaseDao {
    Case getCaseByUniqueId(String id);
}
