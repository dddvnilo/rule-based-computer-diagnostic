package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo2TemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        // Cooling - KVAR_VENTILATORA je u nivo2-manual.drl (zahteva range uslov)
        // Cooling - VENTILATOR_STAO (fan prakticno stao)
        rows.add(row("COOLING",     "rpmCPUVentilator",          "<",  "100",   "VENTILATOR_STAO"));
        rows.add(row("COOLING",     "rpmCaseVentilator",         "<",  "50",    "VENTILATOR_STAO"));
        // GPU
        rows.add(row("GPU",         "rpmGPUVentilator",          "<",  "100",   "VENTILATOR_STAO"));
        rows.add(row("GPU",         "temperaturaGPU",            ">",  "95",    "PREGREVANJE_GPU"));
        // RAM
        rows.add(row("RAM",         "memtestGreske",             ">",  "0",     "FIZICKI_KVAR_RAM"));
        // Disk
        rows.add(row("DISK",        "smartReallocatedSectors",   ">",  "0",     "LOSI_SEKTORI_DISK"));
        rows.add(row("DISK",        "smartPendingSectors",       ">",  "0",     "LOSI_SEKTORI_DISK"));
        rows.add(row("DISK",        "smartUncorrectableErrors",  ">",  "0",     "LOSI_SEKTORI_DISK"));
        rows.add(row("DISK",        "diskPowerOnHours",          ">",  "30000", "ISTROSENOST_DISKA"));
        // PSU
        rows.add(row("PSU",         "napon12V",                "<",  "11.4",  "NESTABILAN_NAPON"));
        rows.add(row("PSU",         "napon5V",                 "<",  "4.75",  "NESTABILAN_NAPON"));
        rows.add(row("PSU",         "napon3V3",                "<",  "3.1",   "NESTABILAN_NAPON"));
        // Motherboard
        rows.add(row("MOTHERBOARD", "temperaturaChipseta",     ">",  "85",    "PREGREVANJE_CHIPSETA"));
        // Network
        rows.add(row("NETWORK",     "packetLoss",              ">",  "30",    "FIZICKI_KVAR_MREZE"));
        rows.add(row("NETWORK",     "pingMs",                  ">",  "500",   "DRIVER_KONFLIKT"));
        rows.add(row("NETWORK",     "mrezBrzinaMbps",          "<",  "100",   "DRIVER_KONFLIKT"));
        // OS
        rows.add(row("OS",          "eventLogGreske",          ">",  "10",    "CORRUPT_FAJLOVI"));
        rows.add(row("OS",          "eventLogGreske",          ">",  "5",     "ZASTARELI_DRAJVERI"));

        return rows;
    }

    private static Map<String, Object> row(String komponenta, String atribut, String operator, String prag, String tipKvara) {
        Map<String, Object> map = new HashMap<>();
        map.put("komponenta", komponenta);
        map.put("atribut", atribut);
        map.put("operator", operator);
        map.put("prag", prag);
        map.put("tipKvara", tipKvara);
        return map;
    }
}
