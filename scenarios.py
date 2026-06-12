import requests

BASE_URL = "http://localhost:8080"

MERENJE_FIELDS = {
    "temperaturaCPU", "temperaturaGPU", "temperaturaChipseta",
    "rpmCPUVentilator", "rpmGPUVentilator", "rpmCaseVentilator",
    "memtestGreske", "ramZauzetost",
    "smartReallocatedSectors", "smartPendingSectors", "smartUncorrectableErrors", "diskPowerOnHours",
    "napon12V", "napon5V", "napon3V3",
    "cpuUtilizacija",
    "packetLoss", "pingMs", "mrezBrzinaMbps",
    "eventLogGreske"
}

ODGOVORI_FIELDS = {
    "sporRad", "bsod", "bsodKod",
    "pregrevanje", "neobicniZvukovi", "ucestaliRestartovi", "zamrzavanje",
    "artefaktiNaEkranu", "problemiSaMrezom", "nestabilnostOS"
}

def split(scenario):
    merenje  = {k: v for k, v in scenario.items() if k in MERENJE_FIELDS}
    odgovori = {k: v for k, v in scenario.items() if k in ODGOVORI_FIELDS}
    return merenje, odgovori

def prazan():
    return {
        "temperaturaCPU": 55.0, "temperaturaGPU": 60.0, "temperaturaChipseta": 50.0,
        "rpmCPUVentilator": 1200, "rpmGPUVentilator": 1500, "rpmCaseVentilator": 900,
        "memtestGreske": 0, "ramZauzetost": 50.0,
        "smartReallocatedSectors": 0, "smartPendingSectors": 0,
        "smartUncorrectableErrors": 0, "diskPowerOnHours": 5000,
        "napon12V": 12.1, "napon5V": 5.0, "napon3V3": 3.3,
        "cpuUtilizacija": 30.0,
        "packetLoss": 0.5, "pingMs": 20.0, "mrezBrzinaMbps": 950.0,
        "eventLogGreske": 0,
        "sporRad": False, "bsod": False, "bsodKod": None,
        "pregrevanje": False, "neobicniZvukovi": False, "ucestaliRestartovi": False,
        "zamrzavanje": False, "artefaktiNaEkranu": False,
        "problemiSaMrezom": False, "nestabilnostOS": False
    }

scenarios = [
    ("Scenario 1:  Kvar ventilatora (RPM nizak, temp CPU visoka)",
        {**prazan(), "temperaturaCPU": 97.0, "rpmCPUVentilator": 450,
                     "neobicniZvukovi": True, "pregrevanje": True}),

    ("Scenario 2:  Fizicki kvar RAM (BSOD + memtest greske)",
        {**prazan(), "memtestGreske": 5, "bsod": True, "bsodKod": "MEMORY_MANAGEMENT"}),

    ("Scenario 3:  Pregrevanje GPU kriticno (artefakti, temp > 100)",
        {**prazan(), "temperaturaGPU": 102.0, "artefaktiNaEkranu": True}),

    ("Scenario 4:  Istrosena termalna pasta (RPM ok, temp CPU visoka)",
        {**prazan(), "temperaturaCPU": 93.0, "rpmCPUVentilator": 1400, "pregrevanje": True}),

    ("Scenario 5:  Losi sektori diska (SMART alarmi)",
        {**prazan(), "smartReallocatedSectors": 12, "smartPendingSectors": 3, "sporRad": True}),

    ("Scenario 6:  Istrosenost diska (45000 radnih sati)",
        {**prazan(), "diskPowerOnHours": 45000, "sporRad": True}),

    ("Scenario 7:  Nestabilan napon - pad 12V linije",
        {**prazan(), "napon12V": 10.8, "ucestaliRestartovi": True}),

    ("Scenario 8:  Nestabilan napon - pad 5V linije",
        {**prazan(), "napon5V": 4.5, "ucestaliRestartovi": True}),

    ("Scenario 9:  Pregrevanje chipseta maticne ploce",
        {**prazan(), "temperaturaChipseta": 92.0, "ucestaliRestartovi": True}),

    ("Scenario 10: Fizicki kvar mrezne kartice (packet loss 45%)",
        {**prazan(), "packetLoss": 45.0, "problemiSaMrezom": True}),

    ("Scenario 11: Driver konflikt mreze (visok ping)",
        {**prazan(), "pingMs": 650.0, "problemiSaMrezom": True}),

    ("Scenario 12: Corrupt fajlovi OS (18 event log gresaka)",
        {**prazan(), "eventLogGreske": 18, "nestabilnostOS": True}),

    ("Scenario 13: Zastareli drajveri (7 event log gresaka)",
        {**prazan(), "eventLogGreske": 7, "nestabilnostOS": True}),

    ("Scenario 14: Pregrevanje CPU pod opterecenjem",
        {**prazan(), "temperaturaCPU": 94.0, "cpuUtilizacija": 96.0, "pregrevanje": True}),

    ("Scenario 15: Kombinovani kvar - nestabilan PSU",
        {**prazan(), "napon12V": 10.5, "napon5V": 4.6, "temperaturaGPU": 97.0,
                     "ucestaliRestartovi": True, "artefaktiNaEkranu": True}),

    ("Scenario 16: VRAM kvar (artefakti + normalna GPU temp)",
        {**prazan(), "temperaturaGPU": 72.0, "artefaktiNaEkranu": True}),

    ("Scenario 17: GPU ventilator stao",
        {**prazan(), "rpmGPUVentilator": 150, "temperaturaGPU": 88.0}),

    ("Scenario 18: Case ventilator stao + visoka temp CPU",
        {**prazan(), "rpmCaseVentilator": 100, "temperaturaCPU": 91.0}),

    ("Scenario 19: Spor rad - RAM + DISK + CPU sumnja",
        {**prazan(), "sporRad": True, "ramZauzetost": 88.0,
                     "memtestGreske": 2, "diskPowerOnHours": 35000}),

    ("Scenario 20: SMART pending sektori",
        {**prazan(), "smartPendingSectors": 7, "sporRad": True}),

    ("Scenario 21: SMART uncorrectable errors",
        {**prazan(), "smartUncorrectableErrors": 2}),

    ("Scenario 22: Niska brzina mrezne veze (45 Mbps)",
        {**prazan(), "mrezBrzinaMbps": 45.0, "problemiSaMrezom": True}),

    ("Scenario 23: BSOD IRQL_NOT_LESS_OR_EQUAL - driver kvar",
        {**prazan(), "bsod": True, "bsodKod": "IRQL_NOT_LESS_OR_EQUAL"}),

    ("Scenario 24: BSOD PAGE_FAULT_IN_NONPAGED_AREA - RAM + DISK",
        {**prazan(), "bsod": True, "bsodKod": "PAGE_FAULT_IN_NONPAGED_AREA",
                     "smartPendingSectors": 3, "memtestGreske": 1}),

    ("Scenario 25: BSOD KERNEL_SECURITY_CHECK_FAILURE - OS corrupt",
        {**prazan(), "bsod": True, "bsodKod": "KERNEL_SECURITY_CHECK_FAILURE"}),

    ("Scenario 26: Ventilator CPU stao (RPM = 0)",
        {**prazan(), "rpmCPUVentilator": 0}),

    ("Scenario 27: Ventilator GPU stao (RPM = 30)",
        {**prazan(), "rpmGPUVentilator": 30, "temperaturaGPU": 85.0}),

    ("Scenario 28: Ventilator case stao (RPM = 20)",
        {**prazan(), "rpmCaseVentilator": 20}),

    ("Scenario 29: VRM kvar maticne ploce (restartovi, PSU napon uredan)",
        {**prazan(), "ucestaliRestartovi": True}),

    ("Scenario 30: Pregrevanje chipseta - NE sme da okine VRM (restartovi + visoka temp chipseta)",
        {**prazan(), "temperaturaChipseta": 92.0, "ucestaliRestartovi": True}),
]

for naziv, scenario in scenarios:
    print(f"\n{'='*60}")
    print(f"  {naziv}")
    print(f"{'='*60}")

    merenje, odgovori = split(scenario)

    r = requests.post(f"{BASE_URL}/merenja", json=merenje)
    if r.status_code != 200:
        print(f"  Greska pri slanju merenja: {r.status_code} - {r.text}")
        continue

    r = requests.post(f"{BASE_URL}/dijagnoza", json=odgovori)
    if r.status_code != 200:
        print(f"  Greska: {r.status_code} - {r.text}")
        continue

    dijagnoze = r.json()
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
