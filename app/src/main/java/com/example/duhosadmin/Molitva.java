package com.example.duhosadmin;

import com.google.firebase.database.PropertyName;

public class Molitva {

    String naziv;
    String datum;
    String tekst;

    public Molitva(String naziv, String datum, String tekst) {
        this.naziv = naziv;
        this.datum = datum;
        this.tekst = tekst;
    }
    @PropertyName("Naziv")
    public String getNaziv() {
        return naziv;
    }
    @PropertyName("Naziv")
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    @PropertyName("Datum")
    public String getDatum() {
        return datum;
    }
    @PropertyName("Datum")
    public void setDatum(String datum) {
        this.datum = datum;
    }
    @PropertyName("Tekst")
    public String getTekst() {
        return tekst;
    }
    @PropertyName("Tekst")
    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
}
