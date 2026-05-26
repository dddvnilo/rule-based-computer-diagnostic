package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo1NumericTemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        // CPU
        rows.add(row("temperaturaCPU",           ">",  "90",    "COOLING"));
        rows.add(row("temperaturaCPU",           ">",  "90",    "CPU"));
        // GPU
        rows.add(row("temperaturaGPU",           ">",  "90",    "GPU"));
        // Motherboard
        rows.add(row("temperaturaChipseta",      ">",  "85",    "MOTHERBOARD"));
        // Disk - SMART
        rows.add(row("smartReallocatedSectors",  ">",  "0",     "DISK"));
        rows.add(row("smartPendingSectors",      ">",  "0",     "DISK"));
        rows.add(row("smartUncorrectableErrors", ">",  "0",     "DISK"));
        rows.add(row("diskPowerOnHours",         ">",  "30000", "DISK"));
        // PSU
        rows.add(row("napon12V",                 "<",  "11.4",  "PSU"));
        rows.add(row("napon5V",                  "<",  "4.75",  "PSU"));
        rows.add(row("napon3V3",                 "<",  "3.1",   "PSU"));
        // Network
        rows.add(row("packetLoss",               ">",  "10",    "NETWORK"));
        rows.add(row("pingMs",                   ">",  "500",   "NETWORK"));
        rows.add(row("mrezBrzinaMbps",           "<",  "100",   "NETWORK"));
        // OS
        rows.add(row("eventLogGreske",           ">",  "5",     "OS"));

        return rows;
    }

    private static Map<String, Object> row(String atribut, String operator, String prag, String komponenta) {
        Map<String, Object> map = new HashMap<>();
        map.put("atribut", atribut);
        map.put("operator", operator);
        map.put("prag", prag);
        map.put("komponenta", komponenta);
        return map;
    }
}
