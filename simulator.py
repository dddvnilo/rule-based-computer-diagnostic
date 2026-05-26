import requests
import json

BASE_URL = "http://localhost:8080/dijagnoza"

def prazan_simptom():
    return {
        "temperaturaCPU": 55.0,
        "temperaturaGPU": 60.0,
        "temperaturaChipseta": 50.0,
        "rpmCPUVentilator": 1200,
        "rpmGPUVentilator": 1500,
        "rpmCaseVentilator": 900,
        "memtestGreske": 0,
        "ramZauzetost": 50.0,
        "smartReallocatedSectors": 0,
        "smartPendingSectors": 0,
        "smartUncorrectableErrors": 0,
        "diskPowerOnHours": 5000,
        "napon12V": 12.1,
        "napon5V": 5.0,
        "napon3V3": 3.3,
        "cpuUtilizacija": 30.0,
        "packetLoss": 0.5,
        "pingMs": 20.0,
        "mrezBrzinaMbps": 950.0,
        "eventLogGreske": 0,
        "sporRad": False,
        "bsod": False,
        "bsodKod": None,
        "pregrevanje": False,
        "neobicniZvukovi": False,
        "ucestaliRestartovi": False,
        "zamrzavanje": False,
        "artefaktiNaEkranu": False,
        "problemiSaMrezom": False,
        "nestabilnostOS": False
    }

scenario1 = {**prazan_simptom(),
    "temperaturaCPU": 97.0,
    "rpmCPUVentilator": 450,
    "neobicniZvukovi": True,
    "pregrevanje": True
}

scenario2 = {**prazan_simptom(),
    "memtestGreske": 5,
    "bsod": True,
    "bsodKod": "MEMORY_MANAGEMENT"
}

scenario3 = {**prazan_simptom(),
    "temperaturaGPU": 102.0,
    "artefaktiNaEkranu": True
}


# Istrosena termalna pasta: RPM ok, ali temp CPU visoka
scenario4 = {**prazan_simptom(),
    "temperaturaCPU": 93.0,
    "rpmCPUVentilator": 1400,
    "pregrevanje": True
}

# Losi sektori diska: SMART alarmi
scenario5 = {**prazan_simptom(),
    "smartReallocatedSectors": 12,
    "smartPendingSectors": 3,
    "sporRad": True
}

# Istrosenost diska: visok broj radnih sati
scenario6 = {**prazan_simptom(),
    "diskPowerOnHours": 45000,
    "sporRad": True
}

# Nestabilan napon PSU: pad 12V linije
scenario7 = {**prazan_simptom(),
    "napon12V": 10.8,
    "ucestaliRestartovi": True
}

# Nestabilan napon PSU: pad 5V linije
scenario8 = {**prazan_simptom(),
    "napon5V": 4.5,
    "ucestaliRestartovi": True
}

# Pregrevanje chipseta matične ploče
scenario9 = {**prazan_simptom(),
    "temperaturaChipseta": 92.0,
    "ucestaliRestartovi": True
}

# Mreza - fizicki kvar (extremno visok packet loss)
scenario10 = {**prazan_simptom(),
    "packetLoss": 45.0,
    "problemiSaMrezom": True
}

# Mreza - driver konflikt (visok ping, mali packet loss)
scenario11 = {**prazan_simptom(),
    "pingMs": 650.0,
    "problemiSaMrezom": True
}

# OS - corrupt fajlovi (mnogo event log gresaka)
scenario12 = {**prazan_simptom(),
    "eventLogGreske": 18,
    "nestabilnostOS": True
}

# OS - zastareli drajveri (umeren broj gresaka)
scenario13 = {**prazan_simptom(),
    "eventLogGreske": 7,
    "nestabilnostOS": True
}

# Pregrevanje CPU pod opterecenjem (visoka temp + visok utilization)
scenario14 = {**prazan_simptom(),
    "temperaturaCPU": 94.0,
    "cpuUtilizacija": 96.0,
    "pregrevanje": True
}

# Kombinovani kvar: PSU uzrokuje probleme na vise komponenti
scenario15 = {**prazan_simptom(),
    "napon12V": 10.5,
    "napon5V": 4.6,
    "temperaturaCPU": 88.0,
    "ucestaliRestartovi": True,
    "zamrzavanje": True
}

scenarios = [
    ("Scenario 1:  Kvar ventilatora (RPM nizak, temp CPU visoka)",        scenario1),
    ("Scenario 2:  Fizicki kvar RAM (BSOD + memtest greske)",             scenario2),
    ("Scenario 3:  Pregrevanje GPU kriticno (artefakti, temp > 100)",     scenario3),
    ("Scenario 4:  Istrosena termalna pasta (RPM ok, temp CPU visoka)",   scenario4),
    ("Scenario 5:  Losi sektori diska (SMART alarmi)",                    scenario5),
    ("Scenario 6:  Istrosenost diska (45000 radnih sati)",                scenario6),
    ("Scenario 7:  Nestabilan napon - pad 12V linije",                    scenario7),
    ("Scenario 8:  Nestabilan napon - pad 5V linije",                     scenario8),
    ("Scenario 9:  Pregrevanje chipseta maticne ploce",                   scenario9),
    ("Scenario 10: Fizicki kvar mrezne kartice (packet loss 45%)",        scenario10),
    ("Scenario 11: Driver konflikt mreze (visok ping)",                   scenario11),
    ("Scenario 12: Corrupt fajlovi OS (18 event log gresaka)",            scenario12),
    ("Scenario 13: Zastareli drajveri (7 event log gresaka)",             scenario13),
    ("Scenario 14: Pregrevanje CPU pod opterecenjem",                     scenario14),
    ("Scenario 15: Kombinovani kvar - nestabilan PSU",                    scenario15),
]

for naziv, scenario in scenarios:
    print(f"\n{'='*60}")
    print(f"  {naziv}")
    print(f"{'='*60}")

    response = requests.post(BASE_URL, json=scenario)

    if response.status_code == 200:
        dijagnoze = response.json()
        if dijagnoze:
            for d in dijagnoze:
                kvar = d.get('kvar', {})
                komponenta = kvar.get('komponenta', {})
                print(f"  Komponenta : {komponenta.get('tipKomponente')}")
                print(f"  Tip kvara  : {kvar.get('tipKvara')}")
                print(f"  Ozbiljnost : {d.get('ozbiljnost')}")
                print(f"  Preporuka  : {d.get('preporuka')}")
                print()
        else:
            print("  Nisu detektovani kvarovi.")
    else:
        print(f"  Greska: {response.status_code} - {response.text}")
