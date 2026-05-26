package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo3TemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        // Cooling
        rows.add(row("KVAR_VENTILATORA",        "KRITICNO",   "Hitno iskljuciti racunar. Proveriti CPU ventilator (moguci kvar lezaja ili zacepljenje prasinom)."));
        rows.add(row("ISTROSENA_TERMALNA_PASTA", "UPOZORENJE", "Planirati zamenu termalne paste na CPU-u."));
        // CPU
        rows.add(row("PREGREVANJE_CPU",          "KRITICNO",   "CPU se pregreva pod opterecenjem. Proveriti hladjenje i aplikacije koje opterecuju procesor."));
        // RAM
        rows.add(row("FIZICKI_KVAR_RAM",         "KRITICNO",   "Zameniti RAM modul. Do tada izbegavati upotrebu racunara."));
        // Disk
        rows.add(row("LOSI_SEKTORI_DISK",        "KRITICNO",   "Odmah napraviti backup podataka. Disk pokazuje znakove fizickog kvara."));
        rows.add(row("ISTROSENOST_DISKA",        "UPOZORENJE", "Disk ima veliki broj radnih sati. Planirati zamenu i redovno praviti backup."));
        // PSU
        rows.add(row("NESTABILAN_NAPON",         "KRITICNO",   "Nestabilan napon na napajanju moze ostetiti vise komponenti. Zameniti napajanje."));
        // Motherboard
        rows.add(row("PREGREVANJE_CHIPSETA",     "UPOZORENJE", "Chipset maticne ploce se pregreva. Proveriti ventilaciju kucista i termopaste na MB."));
        // Network
        rows.add(row("DRIVER_KONFLIKT",          "UPOZORENJE", "Reinstalirati drajvere za mreznu karticu."));
        rows.add(row("FIZICKI_KVAR_MREZE",       "KRITICNO",   "Mrezna kartica pokazuje znakove fizickog kvara. Razmotriti zamenu."));
        // OS
        rows.add(row("ZASTARELI_DRAJVERI",       "UPOZORENJE", "Detektovane greske u Event Log-u. Azurirati sistemske drajvere."));
        rows.add(row("CORRUPT_FAJLOVI",          "KRITICNO",   "Veliki broj sistemskih gresaka. Pokrenuti sfc /scannow i proveriti integritet OS-a."));

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
