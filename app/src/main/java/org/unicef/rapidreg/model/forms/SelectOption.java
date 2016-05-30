package org.unicef.rapidreg.model.forms;

import com.google.gson.annotations.SerializedName;

public class SelectOption {

    @SerializedName("id")
    private String id;
    @SerializedName("display_text")
    private String displayText;

    public SelectOption() {
    }

    public SelectOption(String same) {
        displayText = id = same;
    }

    public SelectOption(String id, String displayText) {
        this.id = id;
        this.displayText = displayText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}
