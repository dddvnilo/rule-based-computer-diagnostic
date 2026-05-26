package com.ftn.sbnz.model;

public class SimptomFakt {

    // Temperatura
    private double temperaturaCPU;
    private double temperaturaGPU;
    private double temperaturaChipseta;

    // Ventilatori (RPM)
    private int rpmCPUVentilator;
    private int rpmGPUVentilator;
    private int rpmCaseVentilator;

    // RAM
    private int memtestGreske;
    private double ramZauzetost;

    // Disk - SMART parametri
    private int smartReallocatedSectors;
    private int smartPendingSectors;
    private int smartUncorrectableErrors;
    private int diskPowerOnHours;

    // PSU naponi
    private double napon12V;
    private double napon5V;
    private double napon3V3;

    // CPU
    private double cpuUtilizacija;

    // Mreža
    private double packetLoss;
    private double pingMs;
    private double mrezBrzinaMbps;

    // OS
    private int eventLogGreske;

    // Korisnicki simptomi
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

    public SimptomFakt() {}

    public double getTemperaturaCPU() { return temperaturaCPU; }
    public void setTemperaturaCPU(double temperaturaCPU) { this.temperaturaCPU = temperaturaCPU; }

    public double getTemperaturaGPU() { return temperaturaGPU; }
    public void setTemperaturaGPU(double temperaturaGPU) { this.temperaturaGPU = temperaturaGPU; }

    public double getTemperaturaChipseta() { return temperaturaChipseta; }
    public void setTemperaturaChipseta(double temperaturaChipseta) { this.temperaturaChipseta = temperaturaChipseta; }

    public int getRpmCPUVentilator() { return rpmCPUVentilator; }
    public void setRpmCPUVentilator(int rpmCPUVentilator) { this.rpmCPUVentilator = rpmCPUVentilator; }

    public int getRpmGPUVentilator() { return rpmGPUVentilator; }
    public void setRpmGPUVentilator(int rpmGPUVentilator) { this.rpmGPUVentilator = rpmGPUVentilator; }

    public int getRpmCaseVentilator() { return rpmCaseVentilator; }
    public void setRpmCaseVentilator(int rpmCaseVentilator) { this.rpmCaseVentilator = rpmCaseVentilator; }

    public int getMemtestGreske() { return memtestGreske; }
    public void setMemtestGreske(int memtestGreske) { this.memtestGreske = memtestGreske; }

    public double getRamZauzetost() { return ramZauzetost; }
    public void setRamZauzetost(double ramZauzetost) { this.ramZauzetost = ramZauzetost; }

    public int getSmartReallocatedSectors() { return smartReallocatedSectors; }
    public void setSmartReallocatedSectors(int smartReallocatedSectors) { this.smartReallocatedSectors = smartReallocatedSectors; }

    public int getSmartPendingSectors() { return smartPendingSectors; }
    public void setSmartPendingSectors(int smartPendingSectors) { this.smartPendingSectors = smartPendingSectors; }

    public int getSmartUncorrectableErrors() { return smartUncorrectableErrors; }
    public void setSmartUncorrectableErrors(int smartUncorrectableErrors) { this.smartUncorrectableErrors = smartUncorrectableErrors; }

    public int getDiskPowerOnHours() { return diskPowerOnHours; }
    public void setDiskPowerOnHours(int diskPowerOnHours) { this.diskPowerOnHours = diskPowerOnHours; }

    public double getNapon12V() { return napon12V; }
    public void setNapon12V(double napon12V) { this.napon12V = napon12V; }

    public double getNapon5V() { return napon5V; }
    public void setNapon5V(double napon5V) { this.napon5V = napon5V; }

    public double getNapon3V3() { return napon3V3; }
    public void setNapon3V3(double napon3V3) { this.napon3V3 = napon3V3; }

    public double getCpuUtilizacija() { return cpuUtilizacija; }
    public void setCpuUtilizacija(double cpuUtilizacija) { this.cpuUtilizacija = cpuUtilizacija; }

    public double getPacketLoss() { return packetLoss; }
    public void setPacketLoss(double packetLoss) { this.packetLoss = packetLoss; }

    public double getPingMs() { return pingMs; }
    public void setPingMs(double pingMs) { this.pingMs = pingMs; }

    public double getMrezBrzinaMbps() { return mrezBrzinaMbps; }
    public void setMrezBrzinaMbps(double mrezBrzinaMbps) { this.mrezBrzinaMbps = mrezBrzinaMbps; }

    public int getEventLogGreske() { return eventLogGreske; }
    public void setEventLogGreske(int eventLogGreske) { this.eventLogGreske = eventLogGreske; }

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
