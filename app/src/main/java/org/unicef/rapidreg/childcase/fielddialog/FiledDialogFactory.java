package org.unicef.rapidreg.childcase.fielddialog;

import android.content.Context;
import android.widget.TextView;

import org.unicef.rapidreg.exception.DialogException;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.model.Case;

public class FiledDialogFactory {

    public static BaseDialog createDialog(Case.FieldType fieldType, Context context,
                                          CaseField caseField,
                                          TextView resultView) throws DialogException {
        try {
            return fieldType.getClz().getConstructor(Context.class, CaseField.class, TextView.class)
                    .newInstance(context, caseField, resultView);
        } catch (Exception e) {
            throw new DialogException(String.format("fieldType: %s", fieldType), e);
        }
    }
}
