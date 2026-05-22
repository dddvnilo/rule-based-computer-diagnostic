package com.ftn.sbnz.model;

public class KvarFakt {

    public enum TipKvara {
        KVAR_VENTILATORA,
        ISTROSENA_TERMALNA_PASTA,
        PREGREVANJE_GPU,
        FIZICKI_KVAR_RAM,
        DRIVER_KONFLIKT,
        NESTABILAN_NAPON,
        LOSI_SEKTORI_DISK
    }

    private TipKvara tipKvara;
    private KomponentaFakt.TipKomponente komponenta;

    public KvarFakt() {}

    public KvarFakt(TipKvara tipKvara, KomponentaFakt.TipKomponente komponenta) {
        this.tipKvara = tipKvara;
        this.komponenta = komponenta;
    }

    public TipKvara getTipKvara() { return tipKvara; }
    public void setTipKvara(TipKvara tipKvara) { this.tipKvara = tipKvara; }

    public KomponentaFakt.TipKomponente getKomponenta() { return komponenta; }
    public void setKomponenta(KomponentaFakt.TipKomponente komponenta) { this.komponenta = komponenta; }
}
