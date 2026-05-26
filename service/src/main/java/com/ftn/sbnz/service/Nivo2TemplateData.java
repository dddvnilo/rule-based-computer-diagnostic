package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo2TemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("COOLING",  "rpmVentilator",  "<",  "500",  "KVAR_VENTILATORA"));
        rows.add(row("GPU",      "temperaturaGPU",  ">",  "95",   "PREGREVANJE_GPU"));
        rows.add(row("RAM",      "memtestGreske",   ">",  "0",    "FIZICKI_KVAR_RAM"));
        rows.add(row("PSU",      "napon12V",        "<",  "11.4", "NESTABILAN_NAPON"));

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
