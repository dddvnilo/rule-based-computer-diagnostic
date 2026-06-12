package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo1BooleanTemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("bsod",               "OS"));
        rows.add(row("neobicniZvukovi",    "COOLING"));
        rows.add(row("pregrevanje",        "COOLING"));
        rows.add(row("pregrevanje",        "CPU"));
        rows.add(row("ucestaliRestartovi", "PSU"));
        rows.add(row("ucestaliRestartovi", "MOTHERBOARD"));
        rows.add(row("zamrzavanje",        "RAM"));
        rows.add(row("artefaktiNaEkranu",  "GPU"));
        rows.add(row("problemiSaMrezom",   "NETWORK"));
        rows.add(row("nestabilnostOS",     "OS"));
        rows.add(row("sporRad",            "DISK"));
        rows.add(row("sporRad",            "RAM"));
        rows.add(row("sporRad",            "CPU"));

        return rows;
    }

    private static Map<String, Object> row(String atribut, String komponenta) {
        Map<String, Object> map = new HashMap<>();
        map.put("atribut", atribut);
        map.put("komponenta", komponenta);
        return map;
    }
}
