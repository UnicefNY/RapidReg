package org.unicef.rapidreg.model;

import org.unicef.rapidreg.R;

public enum Gender {
    MALE("BOY", R.drawable.avatar_placeholder, R.drawable.boy, R.color.primero_blue),
    FEMALE("GIRL", R.drawable.avatar_placeholder, R.drawable.girl, R.color.primero_red),
    PLACEHOLDER("---", R.drawable.avatar_placeholder, R.drawable.gender_default, R.color.primero_font_light),
    EMPTY("", R.drawable.avatar_placeholder, R.drawable.gender_default, R.color.primero_font_light);

    private String name;
    private int avatarId;
    private int genderId;
    private int colorId;

    Gender(String name, int avatarId, int genderId, int colorId) {
        this.name = name;
        this.avatarId = avatarId;
        this.genderId = genderId;
        this.colorId = colorId;
    }

    public String getName() {
        return name;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public int getGenderId() {
        return genderId;
    }

    public int getColorId() {
        return colorId;
    }
}
