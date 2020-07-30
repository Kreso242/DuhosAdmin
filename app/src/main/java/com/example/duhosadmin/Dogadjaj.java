package com.example.duhosadmin;

import com.google.firebase.database.PropertyName;

class Dogadjaj {

    String naslov;
    String opis;
    String datum;
    String lokacija;
    String vrijeme;
    public Dogadjaj(String naslov, String opis, String datum, String vrijeme, String lokacija) {
        this.naslov=naslov;
        this.opis=opis;
        this.datum=datum;
        this.lokacija=lokacija;
        this.vrijeme=vrijeme;

    }
    @PropertyName("Vrijeme")
    public String getVrijeme() {
        return vrijeme;
    }
    @PropertyName("Vrijeme")
    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    @PropertyName("Lokacija")
    public String getLokacija() {
        return lokacija;
    }
    @PropertyName("Lokacija")
    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    @PropertyName("Naslov")
    public String getNaslov() {
        return naslov;
    }
    @PropertyName("Naslov")
    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    @PropertyName("Opis")
    public String getOpis() {
        return opis;
    }
    @PropertyName("Opis")
    public void setOpis(String opis) {
        this.opis = opis;
    }

    @PropertyName("Datum")
    public String getDatum() {
        return datum;
    }
    @PropertyName("Datum")
    public void setDatum(String datum) {
        this.datum = datum;
    }
}
