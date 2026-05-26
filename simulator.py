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
# napon12V i 5V pad + pregrevanje GPU kao posledica nestabilnog napajanja
scenario15 = {**prazan_simptom(),
    "napon12V": 10.5,
    "napon5V": 4.6,
    "temperaturaGPU": 97.0,
    "ucestaliRestartovi": True,
    "artefaktiNaEkranu": True
}

# VRAM kvar: artefakti sa normalnom GPU temperaturom
scenario16 = {**prazan_simptom(),
    "temperaturaGPU": 72.0,
    "artefaktiNaEkranu": True
}

# GPU ventilator stao
scenario17 = {**prazan_simptom(),
    "rpmGPUVentilator": 150,
    "temperaturaGPU": 88.0
}

# Case ventilator stao
scenario18 = {**prazan_simptom(),
    "rpmCaseVentilator": 100,
    "temperaturaCPU": 91.0
}

# Spor rad - mapira na DISK + RAM + CPU
scenario19 = {**prazan_simptom(),
    "sporRad": True,
    "ramZauzetost": 88.0,
    "memtestGreske": 2,
    "diskPowerOnHours": 35000
}

# SMART pending sektori
scenario20 = {**prazan_simptom(),
    "smartPendingSectors": 7,
    "sporRad": True
}

# SMART uncorrectable errors
scenario21 = {**prazan_simptom(),
    "smartUncorrectableErrors": 2
}

# Niska brzina mrezne veze
scenario22 = {**prazan_simptom(),
    "mrezBrzinaMbps": 45.0,
    "problemiSaMrezom": True
}

# BSOD IRQL_NOT_LESS_OR_EQUAL - driver kvar OS
scenario23 = {**prazan_simptom(),
    "bsod": True,
    "bsodKod": "IRQL_NOT_LESS_OR_EQUAL"
}

# BSOD PAGE_FAULT_IN_NONPAGED_AREA - RAM i DISK sumnja
scenario24 = {**prazan_simptom(),
    "bsod": True,
    "bsodKod": "PAGE_FAULT_IN_NONPAGED_AREA",
    "smartPendingSectors": 3,
    "memtestGreske": 1
}

# BSOD KERNEL_SECURITY_CHECK_FAILURE - OS corrupt
scenario25 = {**prazan_simptom(),
    "bsod": True,
    "bsodKod": "KERNEL_SECURITY_CHECK_FAILURE"
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
    ("Scenario 16: VRAM kvar (artefakti + normalna GPU temp)",            scenario16),
    ("Scenario 17: GPU ventilator stao",                                  scenario17),
    ("Scenario 18: Case ventilator stao + visoka temp CPU",               scenario18),
    ("Scenario 19: Spor rad - RAM + DISK + CPU sumnja",                   scenario19),
    ("Scenario 20: SMART pending sektori",                                scenario20),
    ("Scenario 21: SMART uncorrectable errors",                           scenario21),
    ("Scenario 22: Niska brzina mrezne veze (45 Mbps)",                   scenario22),
    ("Scenario 23: BSOD IRQL_NOT_LESS_OR_EQUAL - driver kvar",           scenario23),
    ("Scenario 24: BSOD PAGE_FAULT_IN_NONPAGED_AREA - RAM + DISK",       scenario24),
    ("Scenario 25: BSOD KERNEL_SECURITY_CHECK_FAILURE - OS corrupt",     scenario25),
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
