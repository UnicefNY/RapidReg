package org.unicef.rapidreg.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.unicef.rapidreg.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static String toStringResult(List<String> result) {
        String res = "";

        if (result == null) {
            return res;
        }

        for (int i = 0; i < result.size(); i++) {
            String item = result.get(i);
            if (i == 0) {
                res += item;
            } else {
                res += "," + item;
            }
        }

        return res;
    }

    public static Map convert(String mapString) {
        Map res = new HashMap<>();

        String[] pairs = mapString.substring(1, mapString.length() - 1).split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.trim().split("=");

            checkFormat(keyValue.length);
            res.put(keyValue[0], keyValue[1]);
        }
        return res;
    }

    public static void changeDialogDividerColor(Context context, AlertDialog dialog) {
        int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(ContextCompat.getColor(context, R.color.ftn_blue));
        }
    }

    private static void checkFormat(int len) {
        if (len < 2) {
            throw new IllegalArgumentException();
        }
    }
}
