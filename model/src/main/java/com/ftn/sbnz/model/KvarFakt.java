package com.ftn.sbnz.model;

public class KvarFakt {

    public enum TipKvara {
        // Cooling
        KVAR_VENTILATORA,
        ISTROSENA_TERMALNA_PASTA,
        // CPU
        PREGREVANJE_CPU,
        // GPU
        PREGREVANJE_GPU,
        VRAM_KVAR,
        // RAM
        FIZICKI_KVAR_RAM,
        // Disk
        LOSI_SEKTORI_DISK,
        ISTROSENOST_DISKA,
        // PSU
        NESTABILAN_NAPON,
        // Motherboard
        PREGREVANJE_CHIPSETA,
        // Network
        DRIVER_KONFLIKT,
        FIZICKI_KVAR_MREZE,
        // OS
        ZASTARELI_DRAJVERI,
        CORRUPT_FAJLOVI
    }

    private TipKvara tipKvara;
    private KomponentaFakt komponenta;

    public KvarFakt() {}

    public KvarFakt(TipKvara tipKvara, KomponentaFakt komponenta) {
        this.tipKvara = tipKvara;
        this.komponenta = komponenta;
    }

    public TipKvara getTipKvara() { return tipKvara; }
    public void setTipKvara(TipKvara tipKvara) { this.tipKvara = tipKvara; }

    public KomponentaFakt getKomponenta() { return komponenta; }
    public void setKomponenta(KomponentaFakt komponenta) { this.komponenta = komponenta; }
}
