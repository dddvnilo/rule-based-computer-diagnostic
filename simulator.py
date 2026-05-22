import requests
import json

BASE_URL = "http://localhost:8080/dijagnoza"

# Scenario 1: Kvar ventilatora (RPM nizak, temperatura CPU visoka)
scenario1 = {
    "temperaturaCPU": 97.0,
    "temperaturaGPU": 65.0,
    "rpmVentilator": 450,
    "memtestGreske": 0,
    "napon12V": 12.1,
    "packetLoss": 0.5,
    "artefaktiNaEkranu": False,
    "bsod": False,
    "bsodKod": None,
    "neobicniZvukovi": True,
    "zamrzavanje": False
}

# Scenario 2: Fizicki kvar RAM (BSOD + memtest greske)
scenario2 = {
    "temperaturaCPU": 55.0,
    "temperaturaGPU": 60.0,
    "rpmVentilator": 1200,
    "memtestGreske": 5,
    "napon12V": 12.0,
    "packetLoss": 0.1,
    "artefaktiNaEkranu": False,
    "bsod": True,
    "bsodKod": "MEMORY_MANAGEMENT",
    "neobicniZvukovi": False,
    "zamrzavanje": False
}

# Scenario 3: Pregrevanje GPU (artefakti + visoka temperatura GPU)
scenario3 = {
    "temperaturaCPU": 60.0,
    "temperaturaGPU": 102.0,
    "rpmVentilator": 1500,
    "memtestGreske": 0,
    "napon12V": 12.0,
    "packetLoss": 0.2,
    "artefaktiNaEkranu": True,
    "bsod": False,
    "bsodKod": None,
    "neobicniZvukovi": False,
    "zamrzavanje": False
}

scenarios = [
    ("Scenario 1: Kvar ventilatora", scenario1),
    ("Scenario 2: Fizicki kvar RAM", scenario2),
    ("Scenario 3: Pregrevanje GPU", scenario3),
]

for naziv, scenario in scenarios:
    print(f"\n{'='*50}")
    print(f"{naziv}")
    print(f"{'='*50}")

    response = requests.post(BASE_URL, json=scenario)

    if response.status_code == 200:
        dijagnoze = response.json()
        if dijagnoze:
            for d in dijagnoze:
                kvar = d.get('kvar', {})
                komponenta = kvar.get('komponenta', {})
                print(f"Komponenta : {komponenta.get('tipKomponente')}")
                print(f"Tip kvara  : {kvar.get('tipKvara')}")
                print(f"Ozbiljnost : {d.get('ozbiljnost')}")
                print(f"Preporuka  : {d.get('preporuka')}")
        else:
            print("Nisu detektovani kvarovi.")
    else:
        print(f"Greska: {response.status_code} - {response.text}")
