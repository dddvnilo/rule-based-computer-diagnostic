package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo3TemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        rows.add(row("KVAR_VENTILATORA",        "KRITICNO",   "Hitno iskljuciti racunar. Proveriti CPU ventilator (moguci kvar lezaja ili zacepljenje prasinom)."));
        rows.add(row("ISTROSENA_TERMALNA_PASTA", "UPOZORENJE", "Planirati zamenu termalne paste na CPU-u."));
        rows.add(row("FIZICKI_KVAR_RAM",         "KRITICNO",   "Zameniti RAM modul. Do tada izbegavati upotrebu racunara."));
        rows.add(row("DRIVER_KONFLIKT",          "UPOZORENJE", "Reinstalirati drajvere za problematicnu komponentu."));
        rows.add(row("LOSI_SEKTORI_DISK",        "KRITICNO",   "Odmah napraviti backup podataka. Disk pokazuje znakove fizickog kvara."));
        rows.add(row("NESTABILAN_NAPON",         "KRITICNO",   "Proveriti napajanje - nestabilan napon moze oštetiti vise komponenti istovremeno."));

        return rows;
    }

    private static Map<String, Object> row(String tipKvara, String ozbiljnost, String preporuka) {
        Map<String, Object> map = new HashMap<>();
        map.put("tipKvara", tipKvara);
        map.put("ozbiljnost", ozbiljnost);
        map.put("preporuka", preporuka);
        return map;
    }
}
