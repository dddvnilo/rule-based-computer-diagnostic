#!/usr/bin/env python3
"""
Simulator - kontinualno salje izmerene vrednosti na POST /merenja.

Komponente u kvaru se navode kao argumenti pri pokretanju:
  python simulator.py cpu disk psu

Dostupne komponente: cpu, gpu, hladjenje, disk, psu, mreza, os, maticna
Ako se ne navede nijedna, sve komponente rade u normalnom rezim.

Opcije:
  --interval N    sekundi izmedju merenja (default: 3)
"""
import argparse
import time
import random
import requests

BASE_URL = "http://localhost:8080"

DOSTUPNE_KOMPONENTE = ["cpu", "gpu", "hladjenje", "ram", "disk", "psu", "mreza", "os", "maticna"]

# ---------------------------------------------------------------------------
# Normalni generatori
# ---------------------------------------------------------------------------

def cpu_normalno():
    return {
        "temperaturaCPU": round(random.uniform(45.0, 68.0), 1),
        "cpuUtilizacija":  round(random.uniform(5.0,  45.0), 1),
    }

def gpu_normalno():
    return {
        "temperaturaGPU":    round(random.uniform(40.0, 72.0), 1),
        "rpmGPUVentilator":  random.randint(1200, 1800),
    }

def hladjenje_normalno():
    return {
        "rpmCPUVentilator":    random.randint(900,  1400),
        "rpmGPUVentilator":    random.randint(1200, 1800),
        "rpmCaseVentilator":   random.randint(700,  1100),
        "temperaturaChipseta": round(random.uniform(38.0, 58.0), 1),
    }

def disk_normalno():
    return {
        "smartReallocatedSectors":  0,
        "smartPendingSectors":       0,
        "smartUncorrectableErrors":  0,
        "diskPowerOnHours":          random.randint(3000, 8000),
    }

def psu_normalno():
    return {
        "napon12V": round(random.uniform(11.85, 12.25), 2),
        "napon5V":  round(random.uniform(4.90,  5.10),  2),
        "napon3V3": round(random.uniform(3.25,  3.38),  2),
    }

def mreza_normalno():
    return {
        "packetLoss":     round(random.uniform(0.0, 1.0),   1),
        "pingMs":         round(random.uniform(5.0, 35.0),  1),
        "mrezBrzinaMbps": round(random.uniform(850.0, 980.0), 1),
    }

def os_normalno():
    return {
        "eventLogGreske": random.choices([0, 0, 0, 0, 1], k=1)[0],
    }

def ram_normalno():
    return {
        "memtestGreske": 0,
        "ramZauzetost":  round(random.uniform(30.0, 65.0), 1),
    }

def maticna_normalno():
    return {
        "temperaturaChipseta": round(random.uniform(38.0, 58.0), 1),
    }

# ---------------------------------------------------------------------------
# Generatori kvara
# ---------------------------------------------------------------------------

def cpu_kvar():
    # temperaturaCPU > 90 -> nivo1 (CPU, COOLING) + CEP-1 nakon 3 merenja
    return {
        "temperaturaCPU": round(random.uniform(91.0, 98.0), 1),
        "cpuUtilizacija":  round(random.uniform(80.0, 99.0), 1),
    }

def gpu_kvar():
    # temperaturaGPU > 90 -> nivo1 GPU
    # rpmGPUVentilator < 300 -> nivo1 range (GPU, COOLING)
    return {
        "temperaturaGPU":   round(random.uniform(91.0, 96.0), 1),
        "rpmGPUVentilator": random.randint(50, 200),
    }

# Stanje za CEP-5: progresivni pad RPM CPU ventilatora
_cpu_fan_rpm = 1400

def hladjenje_kvar():
    # rpmCaseVentilator < 200 -> nivo1 range (COOLING)
    # rpmCPUVentilator progresivno pada -> CEP-5 nakon 5 uzastopnih merenja
    global _cpu_fan_rpm
    _cpu_fan_rpm = max(200, _cpu_fan_rpm - random.randint(60, 110))
    return {
        "rpmCPUVentilator":    _cpu_fan_rpm,
        "rpmGPUVentilator":    random.randint(50,  200),
        "rpmCaseVentilator":   random.randint(50,  150),
        "temperaturaChipseta": round(random.uniform(38.0, 58.0), 1),
    }

def disk_kvar():
    # smartReallocatedSectors/pendingSectors/uncorrectableErrors > 0 -> nivo1 DISK + CEP-2 nakon 5 merenja
    return {
        "smartReallocatedSectors":  random.randint(1, 5),
        "smartPendingSectors":       random.randint(0, 3),
        "smartUncorrectableErrors":  random.randint(0, 2),
        "diskPowerOnHours":          random.randint(3000, 8000),
    }

# Stanje za CEP-4: naizmenicno visok/nizak napon za oscilaciju > 0.6V
_psu_toggle = False

def psu_kvar():
    # Alternira 12.1V / 12.9V -> raspon 0.8V > 0.6V -> CEP-4 nakon 5 merenja u 5min
    # napon12V < 11.4 -> nivo1 PSU (jednom u svakih N ciklusa)
    global _psu_toggle
    _psu_toggle = not _psu_toggle
    napon = 12.9 if _psu_toggle else 12.1
    return {
        "napon12V": round(napon + random.uniform(-0.05, 0.05), 2),
        "napon5V":  round(random.uniform(4.90, 5.10), 2),
        "napon3V3": round(random.uniform(3.25, 3.38), 2),
    }

def mreza_kvar():
    # pingMs > 200 -> CEP-3 nakon 3 merenja u 5min
    # packetLoss > 10, mrezBrzinaMbps < 100 -> nivo1 NETWORK
    return {
        "packetLoss":     round(random.uniform(12.0, 30.0), 1),
        "pingMs":         round(random.uniform(220.0, 480.0), 1),
        "mrezBrzinaMbps": round(random.uniform(10.0,  80.0), 1),
    }

def os_kvar():
    # eventLogGreske > 5 -> nivo1 OS
    return {
        "eventLogGreske": random.randint(6, 15),
    }

def ram_kvar():
    # ramZauzetost > 85 -> nivo1 RAM + nivo2 PREOPTERECENJE_RAM -> INFO
    return {
        "memtestGreske": 0,
        "ramZauzetost":  round(random.uniform(88.0, 98.0), 1),
    }

def maticna_kvar():
    # temperaturaChipseta > 85 -> nivo1 MOTHERBOARD
    return {
        "temperaturaChipseta": round(random.uniform(86.0, 92.0), 1),
    }

# ---------------------------------------------------------------------------
# Dispečer: komponenta -> profil -> funkcija
# ---------------------------------------------------------------------------

GENERATORI = {
    "cpu":      {"normalno": cpu_normalno,      "kvar": cpu_kvar},
    "gpu":      {"normalno": gpu_normalno,       "kvar": gpu_kvar},
    "hladjenje":{"normalno": hladjenje_normalno, "kvar": hladjenje_kvar},
    "ram":      {"normalno": ram_normalno,       "kvar": ram_kvar},
    "disk":     {"normalno": disk_normalno,      "kvar": disk_kvar},
    "psu":      {"normalno": psu_normalno,       "kvar": psu_kvar},
    "mreza":    {"normalno": mreza_normalno,      "kvar": mreza_kvar},
    "os":       {"normalno": os_normalno,         "kvar": os_kvar},
    "maticna":  {"normalno": maticna_normalno,    "kvar": maticna_kvar},
}

# ---------------------------------------------------------------------------
# Generisanje i slanje merenja
# ---------------------------------------------------------------------------

def generisi_merenje(profili: dict) -> dict:
    merenje = {}
    for komponenta, profil in profili.items():
        generator = GENERATORI.get(komponenta, {}).get(profil)
        if generator:
            merenje.update(generator())
    return merenje


def posalji(merenje: dict) -> bool:
    try:
        r = requests.post(f"{BASE_URL}/merenja", json=merenje, timeout=5)
        return r.status_code == 200
    except requests.exceptions.ConnectionError:
        return False

# ---------------------------------------------------------------------------
# Ispis i main
# ---------------------------------------------------------------------------

def ispisi_konfiguraciju(profili: dict, interval: int):
    print("=" * 50)
    print("  SIMULATOR")
    print("=" * 50)
    print(f"  Server  : {BASE_URL}")
    print(f"  Interval: {interval}s")
    print("  Profili :")
    for k, v in profili.items():
        marker = " *** KVAR ***" if v == "kvar" else ""
        print(f"    {k:<12} -> {v}{marker}")
    print("=" * 50)
    print()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Simulator merenja za sistem dijagnostike racunara."
    )
    parser.add_argument(
        "kvars",
        nargs="*",
        metavar="KOMPONENTA",
        help=f"Komponente u kvaru: {', '.join(DOSTUPNE_KOMPONENTE)}",
    )
    parser.add_argument(
        "--interval",
        type=int,
        default=3,
        metavar="N",
        help="Sekundi izmedju merenja (default: 3)",
    )
    args = parser.parse_args()

    nepoznate = [k for k in args.kvars if k not in DOSTUPNE_KOMPONENTE]
    if nepoznate:
        parser.error(f"Nepoznate komponente: {', '.join(nepoznate)}. "
                     f"Dostupne: {', '.join(DOSTUPNE_KOMPONENTE)}")

    profili = {k: ("kvar" if k in args.kvars else "normalno") for k in DOSTUPNE_KOMPONENTE}

    ispisi_konfiguraciju(profili, args.interval)

    br = 0
    try:
        while True:
            br += 1
            merenje = generisi_merenje(profili)
            ok = posalji(merenje)
            status = "OK" if ok else "GRESKA (server nedostupan)"
            kvars_str = ", ".join(args.kvars) if args.kvars else "—"
            print(f"  [{br:>4}] {status}  | kvars: {kvars_str}")
            time.sleep(args.interval)
    except KeyboardInterrupt:
        print("\n  Simulator zaustavljen.")
