package com.ftn.sbnz.model;

public class MerenjeEvent {

    private double temperaturaCPU;
    private double temperaturaGPU;
    private double temperaturaChipseta;

    private int rpmCPUVentilator;
    private int rpmGPUVentilator;
    private int rpmCaseVentilator;

    private int memtestGreske;
    private double ramZauzetost;

    private int smartReallocatedSectors;
    private int smartPendingSectors;
    private int smartUncorrectableErrors;
    private int diskPowerOnHours;

    private double napon12V;
    private double napon5V;
    private double napon3V3;

    private double cpuUtilizacija;

    private double packetLoss;
    private double pingMs;
    private double mrezBrzinaMbps;

    private int eventLogGreske;

    public MerenjeEvent() {}

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
}
