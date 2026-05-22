package com.ftn.sbnz.model;

public class SimptomFakt {

    // Hardverski podaci iz simulatora
    private double temperaturaCPU;
    private double temperaturaGPU;
    private int rpmVentilator;
    private int memtestGreske;
    private double napon12V;
    private double packetLoss;

    // Simptomi koje korisnik bira
    private boolean artefaktiNaEkranu;
    private boolean bsod;
    private String bsodKod;
    private boolean neobicniZvukovi;
    private boolean zamrzavanje;

    public SimptomFakt() {}

    public double getTemperaturaCPU() { return temperaturaCPU; }
    public void setTemperaturaCPU(double temperaturaCPU) { this.temperaturaCPU = temperaturaCPU; }

    public double getTemperaturaGPU() { return temperaturaGPU; }
    public void setTemperaturaGPU(double temperaturaGPU) { this.temperaturaGPU = temperaturaGPU; }

    public int getRpmVentilator() { return rpmVentilator; }
    public void setRpmVentilator(int rpmVentilator) { this.rpmVentilator = rpmVentilator; }

    public int getMemtestGreske() { return memtestGreske; }
    public void setMemtestGreske(int memtestGreske) { this.memtestGreske = memtestGreske; }

    public double getNapon12V() { return napon12V; }
    public void setNapon12V(double napon12V) { this.napon12V = napon12V; }

    public double getPacketLoss() { return packetLoss; }
    public void setPacketLoss(double packetLoss) { this.packetLoss = packetLoss; }

    public boolean isArtefaktiNaEkranu() { return artefaktiNaEkranu; }
    public void setArtefaktiNaEkranu(boolean artefaktiNaEkranu) { this.artefaktiNaEkranu = artefaktiNaEkranu; }

    public boolean isBsod() { return bsod; }
    public void setBsod(boolean bsod) { this.bsod = bsod; }

    public String getBsodKod() { return bsodKod; }
    public void setBsodKod(String bsodKod) { this.bsodKod = bsodKod; }

    public boolean isNeobicniZvukovi() { return neobicniZvukovi; }
    public void setNeobicniZvukovi(boolean neobicniZvukovi) { this.neobicniZvukovi = neobicniZvukovi; }

    public boolean isZamrzavanje() { return zamrzavanje; }
    public void setZamrzavanje(boolean zamrzavanje) { this.zamrzavanje = zamrzavanje; }
}
