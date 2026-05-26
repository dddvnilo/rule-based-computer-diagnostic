package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo1RangeTemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("rpmGPUVentilator",  "0", "300", "GPU"));
        rows.add(row("rpmGPUVentilator",  "0", "300", "COOLING"));
        rows.add(row("rpmCaseVentilator", "0", "200", "COOLING"));

        return rows;
    }

    private static Map<String, Object> row(String atribut, String min, String max, String komponenta) {
        Map<String, Object> map = new HashMap<>();
        map.put("atribut", atribut);
        map.put("min", min);
        map.put("max", max);
        map.put("komponenta", komponenta);
        return map;
    }
}
