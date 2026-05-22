package com.ftn.sbnz.model;

public class DijagnozaFakt {

    public enum Ozbiljnost {
        INFO, UPOZORENJE, KRITICNO
    }

    private KvarFakt kvar;
    private Ozbiljnost ozbiljnost;
    private String preporuka;

    public DijagnozaFakt() {}

    public DijagnozaFakt(KvarFakt kvar, Ozbiljnost ozbiljnost, String preporuka) {
        this.kvar = kvar;
        this.ozbiljnost = ozbiljnost;
        this.preporuka = preporuka;
    }

    public KvarFakt getKvar() { return kvar; }
    public void setKvar(KvarFakt kvar) { this.kvar = kvar; }

    public Ozbiljnost getOzbiljnost() { return ozbiljnost; }
    public void setOzbiljnost(Ozbiljnost ozbiljnost) { this.ozbiljnost = ozbiljnost; }

    public String getPreporuka() { return preporuka; }
    public void setPreporuka(String preporuka) { this.preporuka = preporuka; }

    @Override
    public String toString() {
        return "[" + ozbiljnost + "] Komponenta: " + kvar.getKomponenta().getTipKomponente() +
               " | Kvar: " + kvar.getTipKvara() +
               " | Preporuka: " + preporuka;
    }
}
