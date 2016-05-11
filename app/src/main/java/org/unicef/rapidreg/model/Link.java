package org.unicef.rapidreg.model;

public class Link {
    private String rel;

    private String uri;

    public void setRel(String rel){
        this.rel = rel;
    }
    public String getRel(){
        return this.rel;
    }
    public void setUri(String uri){
        this.uri = uri;
    }
    public String getUri(){
        return this.uri;
    }

}
