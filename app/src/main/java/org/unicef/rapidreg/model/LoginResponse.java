package org.unicef.rapidreg.model;

public class LoginResponse {
    private Session session;

    private String db_key;

    private String organization;

    private String language;

    private boolean verified;

    public void setSession(Session session){
        this.session = session;
    }
    public Session getSession(){
        return this.session;
    }
    public void setDb_key(String db_key){
        this.db_key = db_key;
    }
    public String getDb_key(){
        return this.db_key;
    }
    public void setOrganization(String organization){
        this.organization = organization;
    }
    public String getOrganization(){
        return this.organization;
    }
    public void setLanguage(String language){
        this.language = language;
    }
    public String getLanguage(){
        return this.language;
    }
    public void setVerified(boolean verified){
        this.verified = verified;
    }
    public boolean getVerified(){
        return this.verified;
    }

}
