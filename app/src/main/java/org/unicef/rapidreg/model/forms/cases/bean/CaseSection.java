package org.unicef.rapidreg.model.forms.cases.bean;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.unicef.rapidreg.db.MapConverter;
import org.unicef.rapidreg.db.PrimeroDB;

import java.util.Map;

@Table(database = PrimeroDB.class)
public class CaseSection extends BaseModel {
    @PrimaryKey
    @Column(name = "name", typeConverter = MapConverter.class)
    Map name;
    @Column(name = "order")
    int order;
    @Column(name = "help_text", typeConverter = MapConverter.class)
    Map helpText;
    @Column(name = "base_language")
    String baseLanguage;
    @ForeignKey(
            tableClass = CaseForm.class,
            references = {@ForeignKeyReference(columnName = "form_id", columnType = Long.class,
                    foreignKeyColumnName = "id")})
    long form_id;

    public Map getName() {
        return name;
    }

    public void setName(Map name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Map getHelpText() {
        return helpText;
    }

    public void setHelpText(Map helpText) {
        this.helpText = helpText;
    }

    public String getBaseLanguage() {
        return baseLanguage;
    }

    public void setBaseLanguage(String baseLanguage) {
        this.baseLanguage = baseLanguage;
    }

    public long getForm_id() {
        return form_id;
    }

    public void setForm_id(long form_id) {
        this.form_id = form_id;
    }
}
