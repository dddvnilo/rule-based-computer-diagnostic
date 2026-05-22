package com.ftn.sbnz.model;

public class DijagnozaFakt {

    public enum Ozbiljnost {
        INFO, UPOZORENJE, KRITICNO
    }

    private KomponentaFakt.TipKomponente komponenta;
    private KvarFakt.TipKvara tipKvara;
    private Ozbiljnost ozbiljnost;
    private String preporuka;

    public DijagnozaFakt() {}

    public DijagnozaFakt(KomponentaFakt.TipKomponente komponenta, KvarFakt.TipKvara tipKvara,
                         Ozbiljnost ozbiljnost, String preporuka) {
        this.komponenta = komponenta;
        this.tipKvara = tipKvara;
        this.ozbiljnost = ozbiljnost;
        this.preporuka = preporuka;
    }

    public KomponentaFakt.TipKomponente getKomponenta() { return komponenta; }
    public void setKomponenta(KomponentaFakt.TipKomponente komponenta) { this.komponenta = komponenta; }

    public KvarFakt.TipKvara getTipKvara() { return tipKvara; }
    public void setTipKvara(KvarFakt.TipKvara tipKvara) { this.tipKvara = tipKvara; }

    public Ozbiljnost getOzbiljnost() { return ozbiljnost; }
    public void setOzbiljnost(Ozbiljnost ozbiljnost) { this.ozbiljnost = ozbiljnost; }

    public String getPreporuka() { return preporuka; }
    public void setPreporuka(String preporuka) { this.preporuka = preporuka; }

    @Override
    public String toString() {
        return "[" + ozbiljnost + "] Komponenta: " + komponenta +
               " | Kvar: " + tipKvara +
               " | Preporuka: " + preporuka;
    }
}
