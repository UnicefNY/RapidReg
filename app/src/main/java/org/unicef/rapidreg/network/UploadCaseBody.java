package org.unicef.rapidreg.network;


import java.util.HashMap;
import java.util.Map;


public class UploadCaseBody {


    public static Map<String, Map<String, String>> body = new HashMap<>();

    static {
        Map<String, String> value = new HashMap<>();
        value.put("module_id", "primeromodule-cp");
        value.put("date_of_birth", "2006/01/10");
        value.put("case_id", "56798b3e-c5b8-44d9-a8c1-2593b2b127c9");
        body.put("child", value);
    }
}
