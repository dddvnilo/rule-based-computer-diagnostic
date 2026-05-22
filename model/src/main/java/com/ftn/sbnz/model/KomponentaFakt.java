package com.ftn.sbnz.model;

public class KomponentaFakt {

    public enum TipKomponente {
        CPU, GPU, RAM, DISK, PSU, MOTHERBOARD, COOLING, NETWORK, OS
    }

    private TipKomponente tipKomponente;

    public KomponentaFakt() {}

    public KomponentaFakt(TipKomponente tipKomponente) {
        this.tipKomponente = tipKomponente;
    }

    public TipKomponente getTipKomponente() { return tipKomponente; }
    public void setTipKomponente(TipKomponente tipKomponente) { this.tipKomponente = tipKomponente; }
}
