#!/usr/bin/env python3
"""
Simulator - kontinualno salje izmerene vrednosti na POST /merenja.

Konfiguracija je na vrhu fajla u recniku KONFIGURACIJA.
Svaka komponenta ima "profil" koji odredjuje sta simulator salje za tu komponentu.
Profil "normalno" salje vrednosti u normalnim granicama uz male slucajne oscilacije.
Novi profili se dodaju po potrebi (npr. "pregrevanje", "kvar_ventilatora", itd.)

Koriscenje:
  python simulator.py

"""
import time
import random
import requests

BASE_URL  = "http://localhost:8080"

# ---------------------------------------------------------------------------
# Konfiguracija - menjati profil po komponenti da bi se aktivirale anomalije
# ---------------------------------------------------------------------------

KONFIGURACIJA = {
    "interval": 3,          # sekundi izmedju merenja
    "profili": {
        "cpu":       "normalno",
        "gpu":       "normalno",
        "hladjenje": "normalno",
        "ram":       "normalno",
        "disk":      "normalno",
        "napajanje": "normalno",
        "mreza":     "normalno",
        "sistem":    "normalno",
    }
}

# ---------------------------------------------------------------------------
# Generatori vrednosti po komponentama i profilima
# ---------------------------------------------------------------------------

def cpu_normalno():
    return {
        "temperaturaCPU": round(random.uniform(45.0, 68.0), 1),
        "cpuUtilizacija":  round(random.uniform(5.0,  45.0), 1),
    }


def gpu_normalno():
    return {
        "temperaturaGPU": round(random.uniform(40.0, 72.0), 1),
    }


def hladjenje_normalno():
    return {
        "rpmCPUVentilator":  random.randint(900,  1400),
        "rpmGPUVentilator":  random.randint(1200, 1800),
        "rpmCaseVentilator": random.randint(700,  1100),
        "temperaturaChipseta": round(random.uniform(38.0, 58.0), 1),
    }


def ram_normalno():
    return {
        "memtestGreske": 0,
        "ramZauzetost":  round(random.uniform(30.0, 65.0), 1),
    }


def disk_normalno():
    return {
        "smartReallocatedSectors":  0,
        "smartPendingSectors":       0,
        "smartUncorrectableErrors":  0,
        "diskPowerOnHours":          random.randint(3000, 8000),
    }


def napajanje_normalno():
    return {
        "napon12V": round(random.uniform(11.85, 12.25), 2),
        "napon5V":  round(random.uniform(4.90,  5.10),  2),
        "napon3V3": round(random.uniform(3.25,  3.38),  2),
    }


def mreza_normalno():
    return {
        "packetLoss":    round(random.uniform(0.0, 1.0),   1),
        "pingMs":        round(random.uniform(5.0, 35.0),  1),
        "mrezBrzinaMbps": round(random.uniform(850.0, 980.0), 1),
    }


def sistem_normalno():
    return {
        "eventLogGreske": random.choices([0, 0, 0, 0, 1], k=1)[0],
    }


# ---------------------------------------------------------------------------
# Dispečer: komponenta -> profil -> funkcija
# ---------------------------------------------------------------------------

GENERATORI = {
    "cpu": {
        "normalno": cpu_normalno,
    },
    "gpu": {
        "normalno": gpu_normalno,
    },
    "hladjenje": {
        "normalno": hladjenje_normalno,
    },
    "ram": {
        "normalno": ram_normalno,
    },
    "disk": {
        "normalno": disk_normalno,
    },
    "napajanje": {
        "normalno": napajanje_normalno,
    },
    "mreza": {
        "normalno": mreza_normalno,
    },
    "sistem": {
        "normalno": sistem_normalno,
    },
}

# ---------------------------------------------------------------------------
# Generisanje merenja
# ---------------------------------------------------------------------------

def generisi_merenje() -> dict:
    merenje = {}
    for komponenta, profil in KONFIGURACIJA["profili"].items():
        generator = GENERATORI.get(komponenta, {}).get(profil)
        if generator is None:
            print(f"  [UPOZORENJE] Nepoznat profil '{profil}' za komponentu '{komponenta}', preskacemo.")
            continue
        merenje.update(generator())
    return merenje


def posalji(merenje: dict) -> bool:
    try:
        r = requests.post(f"{BASE_URL}/merenja", json=merenje, timeout=5)
        return r.status_code == 200
    except requests.exceptions.ConnectionError:
        return False


# ---------------------------------------------------------------------------
# Glavni loop
# ---------------------------------------------------------------------------

def ispisi_konfiguraciju():
    print("=" * 50)
    print("  SIMULATOR")
    print("=" * 50)
    print(f"  Server  : {BASE_URL}")
    print(f"  Interval: {KONFIGURACIJA['interval']}s")
    print("  Profili :")
    for k, v in KONFIGURACIJA["profili"].items():
        print(f"    {k:<12} -> {v}")
    print("=" * 50)
    print()


if __name__ == "__main__":
    ispisi_konfiguraciju()

    br = 0
    try:
        while True:
            br += 1
            merenje = generisi_merenje()
            ok = posalji(merenje)
            status = "OK" if ok else "GRESKA (server nedostupan)"
            print(f"  [{br:>4}] {status}")
            time.sleep(KONFIGURACIJA["interval"])
    except KeyboardInterrupt:
        print("\n  Simulator zaustavljen.")
