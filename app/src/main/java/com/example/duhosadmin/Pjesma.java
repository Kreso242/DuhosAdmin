package com.example.duhosadmin;

import com.google.firebase.database.PropertyName;

public class Pjesma {

    String naslov;
    String bend;
    String tekstPjesme;
    String link;
    String youtubeLink;

    public Pjesma(String naslov, String bend, String tekstPjesme, String link, String youtubeLink) {
        this.naslov = naslov;
        this.bend = bend;
        this.tekstPjesme=tekstPjesme;
        this.link=link;
        this.youtubeLink=youtubeLink;
    }
    @PropertyName("YouTube")
    public String getYoutubeLink() {
        return youtubeLink;
    }
    @PropertyName("YouTube")
    public void setYoutubeLink(String link) {
        this.youtubeLink = youtubeLink;
    }

    @PropertyName("Link")
    public String getLink() {
        return link;
    }
    @PropertyName("Link")
    public void setLink(String link) {
        this.link = link;
    }

    @PropertyName("Tekst")
    public String getTekstPjesme() { return tekstPjesme; }
    @PropertyName("Tekst")
    public void setTekstPjesme(String tekstPjesme) { this.tekstPjesme = tekstPjesme; }

    @PropertyName("Naslov")
    public String getNaslov() {
        return naslov;
    }
    @PropertyName("Naslov")
    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    @PropertyName("Izvođač")
    public String getBend() {
        return bend;
    }
    @PropertyName("Izvođač")
    public void setBend(String bend) {
        this.bend = bend;
    }
}
