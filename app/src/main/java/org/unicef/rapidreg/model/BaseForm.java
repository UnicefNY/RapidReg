package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseModel;

public class BaseForm extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column(name = "form_json")
    private Blob form;

    @Column(name = "module_id")
    private String moduleId;

    @Column
    private String url;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }


    public BaseForm() {
    }

    public BaseForm(Blob form) {
        this.form = form;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Blob getForm() {
        return form;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setForm(Blob form) {
        this.form = form;
    }
}
