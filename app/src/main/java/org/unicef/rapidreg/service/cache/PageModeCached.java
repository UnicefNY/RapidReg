package org.unicef.rapidreg.service.cache;

public class PageModeCached {

    private static CaseMode caseMode;

    public enum CaseMode {
        EDIT, ADD, LIST, DETAIL, SEARCH;


    }
    public static void setEditMode() {
        caseMode = CaseMode.EDIT;
    }
    public static void setDetailMode() {
        caseMode = CaseMode.DETAIL;
    }

    public static void setListMode() {
        caseMode = CaseMode.LIST;
    }

    public static void setAddMode() {
        caseMode = CaseMode.ADD;
    }

    public static void setSearchMode() {
        caseMode = CaseMode.SEARCH;
    }

    public static boolean isDetailMode() {
        return caseMode == CaseMode.DETAIL;
    }

    public static boolean isAddMode() {
        return caseMode == CaseMode.ADD;
    }

    public static boolean isEditMode() {
        return caseMode == CaseMode.EDIT;
    }

    public static boolean isListMode() {
        return caseMode == CaseMode.LIST;
    }
}
