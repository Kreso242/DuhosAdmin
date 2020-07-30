package com.example.duhosadmin;

import com.google.firebase.database.PropertyName;

public class Medij {

    String naslov;
    String sadržaj;
    String medij;
    String link;

    public Medij(String naslov, String sadržaj, String medij, String link) {
        this.naslov = naslov;
        this.sadržaj = sadržaj;
        this.medij = medij;
        this.link =link;
    }
    @PropertyName("Link")
    public String getLink() {
        return link;
    }
    @PropertyName("Link")
    public void setLink(String link) {
        this.link = link;
    }

    @PropertyName("Naslov")
    public String getNaslov() {
        return naslov;
    }
    @PropertyName("Naslov")
    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    @PropertyName("Sadržaj")
    public String getSadržaj() {
        return sadržaj;
    }
    @PropertyName("Sadržaj")
    public void setSadržaj(String sadržaj) {
        this.sadržaj = sadržaj;
    }

    @PropertyName("Medij")
    public String getMedij() {
        return medij;
    }
    @PropertyName("Medij")
    public void setMedij(String medij) {
        this.medij = medij;
    }
}
