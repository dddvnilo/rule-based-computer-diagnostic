package com.ftn.sbnz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nivo3TemplateData {

    public static List<Map<String, Object>> getRows() {
        List<Map<String, Object>> rows = new ArrayList<>();

        // Cooling
        rows.add(row("VENTILATOR_STAO",         "KRITICNO",   "Ventilator je stao. Odmah iskljuciti racunar - bez hladjenja sistem ce se pregrejati za nekoliko minuta. Proveriti/zameniti ventilator."));
        rows.add(row("KVAR_VENTILATORA",        "KRITICNO",   "Hitno iskljuciti racunar. Proveriti ventilator komponente (moguci kvar lezaja ili zacepljenje prasinom)."));
        rows.add(row("ISTROSENA_TERMALNA_PASTA", "UPOZORENJE", "Planirati zamenu termalne paste na CPU-u."));
        // CPU
        rows.add(row("PREGREVANJE_CPU",          "KRITICNO",   "CPU se pregreva pod opterecenjem. Proveriti hladjenje i aplikacije koje opterecuju procesor."));
        // GPU
        rows.add(row("VRAM_KVAR",                "KRITICNO",   "Artefakti bez pregrevanja ukazuju na kvar VRAM memorije. Zameniti graficku karticu."));
        // RAM
        rows.add(row("FIZICKI_KVAR_RAM",         "KRITICNO",   "Zameniti RAM modul. Do tada izbegavati upotrebu racunara."));
        // Disk
        rows.add(row("LOSI_SEKTORI_DISK",        "KRITICNO",   "Odmah napraviti backup podataka. Disk pokazuje znakove fizickog kvara."));
        rows.add(row("ISTROSENOST_DISKA",        "INFO",       "Disk ima veliki broj radnih sati. Planirati zamenu i redovno praviti backup."));
        // PSU
        rows.add(row("NESTABILAN_NAPON",         "KRITICNO",   "Nestabilan napon na napajanju moze ostetiti vise komponenti. Zameniti napajanje."));
        // Motherboard
        rows.add(row("PREGREVANJE_CHIPSETA",     "UPOZORENJE", "Chipset maticne ploce se pregreva. Proveriti ventilaciju kucista i termopaste na MB."));
        rows.add(row("VRM_KVAR",                 "KRITICNO",   "VRM sekcija maticne ploce moze biti neispravna. Ucestali restartovi bez problema na napajanju ukazuju na nestabilnost naponskih regulatora. Konsultovati servis."));
        // Network
        rows.add(row("DRIVER_KONFLIKT",          "INFO",       "Reinstalirati drajvere za mreznu karticu."));
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
