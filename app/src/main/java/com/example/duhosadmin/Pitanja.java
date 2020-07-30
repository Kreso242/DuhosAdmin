package com.example.duhosadmin;

import com.google.firebase.database.PropertyName;

public class Pitanja {

    String pitanje;
    String odgovor;

    public Pitanja(String pitanje, String odgovor) {
        this.pitanje = pitanje;
        this.odgovor = odgovor;
    }

    @PropertyName("Pitanje")
    public String getPitanje() {
        return pitanje;
    }
    @PropertyName("Pitanje")
    public void setPitanje(String pitanje) {
        this.pitanje = pitanje;
    }

    @PropertyName("Odgovor")
    public String getOdgovor() {
        return odgovor;
    }
    @PropertyName("Odgovor")
    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }
}
