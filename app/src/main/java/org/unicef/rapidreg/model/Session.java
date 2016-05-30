package org.unicef.rapidreg.model;

public class Session {
    private String token;

    private Link link;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return this.link;
    }

}
