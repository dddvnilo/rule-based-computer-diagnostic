package com.ftn.sbnz.model;

public class CepAlarmFakt {

    private String tip;
    private String poruka;

    public CepAlarmFakt() {}

    public CepAlarmFakt(String tip, String poruka) {
        this.tip = tip;
        this.poruka = poruka;
    }

    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }

    public String getPoruka() { return poruka; }
    public void setPoruka(String poruka) { this.poruka = poruka; }

    @Override
    public String toString() {
        return "[CEP] " + tip + ": " + poruka;
    }
}
