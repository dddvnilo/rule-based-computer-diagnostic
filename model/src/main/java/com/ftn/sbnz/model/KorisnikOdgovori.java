package com.ftn.sbnz.model;

public class KorisnikOdgovori {

    private boolean sporRad;
    private boolean bsod;
    private String bsodKod;
    private boolean pregrevanje;
    private boolean neobicniZvukovi;
    private boolean ucestaliRestartovi;
    private boolean zamrzavanje;
    private boolean artefaktiNaEkranu;
    private boolean problemiSaMrezom;
    private boolean nestabilnostOS;

    public KorisnikOdgovori() {}

    public boolean isSporRad() { return sporRad; }
    public void setSporRad(boolean sporRad) { this.sporRad = sporRad; }

    public boolean isBsod() { return bsod; }
    public void setBsod(boolean bsod) { this.bsod = bsod; }

    public String getBsodKod() { return bsodKod; }
    public void setBsodKod(String bsodKod) { this.bsodKod = bsodKod; }

    public boolean isPregrevanje() { return pregrevanje; }
    public void setPregrevanje(boolean pregrevanje) { this.pregrevanje = pregrevanje; }

    public boolean isNeobicniZvukovi() { return neobicniZvukovi; }
    public void setNeobicniZvukovi(boolean neobicniZvukovi) { this.neobicniZvukovi = neobicniZvukovi; }

    public boolean isUcestaliRestartovi() { return ucestaliRestartovi; }
    public void setUcestaliRestartovi(boolean ucestaliRestartovi) { this.ucestaliRestartovi = ucestaliRestartovi; }

    public boolean isZamrzavanje() { return zamrzavanje; }
    public void setZamrzavanje(boolean zamrzavanje) { this.zamrzavanje = zamrzavanje; }

    public boolean isArtefaktiNaEkranu() { return artefaktiNaEkranu; }
    public void setArtefaktiNaEkranu(boolean artefaktiNaEkranu) { this.artefaktiNaEkranu = artefaktiNaEkranu; }

    public boolean isProblemiSaMrezom() { return problemiSaMrezom; }
    public void setProblemiSaMrezom(boolean problemiSaMrezom) { this.problemiSaMrezom = problemiSaMrezom; }

    public boolean isNestabilnostOS() { return nestabilnostOS; }
    public void setNestabilnostOS(boolean nestabilnostOS) { this.nestabilnostOS = nestabilnostOS; }
}
