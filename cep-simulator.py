#!/usr/bin/env python3
"""
CEP Simulator - konfigurabilan simulator za testiranje CEP pravila.

Koriscenje:
  python cep-simulator.py              # interaktivni meni
  python cep-simulator.py cep1         # direktno pokretanje scenarija

Zahteva: pip install requests websocket-client
"""
import sys
import time
import json
import threading
import requests
import websocket

BASE_URL = "http://localhost:8080"
WS_URL   = "ws://localhost:8080/ws"


# ---------------------------------------------------------------------------
# Bazno merenje - sve vrednosti u normalnom opsegu
# ---------------------------------------------------------------------------

def baza():
    return {
        "temperaturaCPU":         55.0,
        "temperaturaGPU":         60.0,
        "temperaturaChipseta":    50.0,
        "rpmCPUVentilator":       1200,
        "rpmGPUVentilator":       1500,
        "rpmCaseVentilator":       900,
        "memtestGreske":             0,
        "ramZauzetost":           50.0,
        "smartReallocatedSectors":   0,
        "smartPendingSectors":        0,
        "smartUncorrectableErrors":  0,
        "diskPowerOnHours":        5000,
        "napon12V":               12.1,
        "napon5V":                 5.0,
        "napon3V3":                3.3,
        "cpuUtilizacija":         30.0,
        "packetLoss":              0.5,
        "pingMs":                 20.0,
        "mrezBrzinaMbps":        950.0,
        "eventLogGreske":            0,
    }


# ---------------------------------------------------------------------------
# Scenariji po CEP pravilu
# ---------------------------------------------------------------------------

SCENARIJI = {
    "cep1": {
        "naziv":    "CEP-1: Ponavljajuce pregrevanje CPU",
        "opis":     "Salje 3 merenja sa temperaturaCPU > 90 C u prozoru od 10 minuta.",
        "napomena": None,
        "interval": 5,
        "merenja": [
            {**baza(), "temperaturaCPU": 93.0},
            {**baza(), "temperaturaCPU": 96.0},
            {**baza(), "temperaturaCPU": 94.0},
        ],
    },
    "cep2": {
        "naziv":    "CEP-2: Ucestale SMART greske diska",
        "opis":     "Salje 5 merenja sa SMART greskama u prozoru od 24h.",
        "napomena": "Vremenski prozor je 24h - alarm ce biti aktiviran ako se sva merenja posalju unutar tog perioda.",
        "interval": 5,
        "merenja": [
            {**baza(), "smartReallocatedSectors": 1},
            {**baza(), "smartPendingSectors":      2},
            {**baza(), "smartUncorrectableErrors": 1},
            {**baza(), "smartReallocatedSectors":  3},
            {**baza(), "smartPendingSectors":       1},
        ],
    },
    "cep3": {
        "naziv":    "CEP-3: Nestabilan ping",
        "opis":     "Salje 3 merenja sa pingMs > 200 ms u prozoru od 5 minuta.",
        "napomena": None,
        "interval": 5,
        "merenja": [
            {**baza(), "pingMs": 350.0},
            {**baza(), "pingMs": 420.0},
            {**baza(), "pingMs": 280.0},
        ],
    },
    "cep4": {
        "naziv":    "CEP-4: Oscilacija napona 12V",
        "opis":     "Salje 5 merenja sa oscilujucim naponom 12V (raspon > 0.6V u prozoru od 5 minuta). Alarm po 5. merenju.",
        "napomena": None,
        "interval": 5,
        "merenja": [
            {**baza(), "napon12V": 12.3},
            {**baza(), "napon12V": 11.6},
            {**baza(), "napon12V": 12.2},
            {**baza(), "napon12V": 11.5},
            {**baza(), "napon12V": 12.1},
        ],
    },
    "cep5": {
        "naziv":    "CEP-5: Progresivni pad RPM CPU ventilatora",
        "opis":     "Salje 5 merenja sa opadajucim RPM vrednostima. Alarm po 5. merenju.",
        "napomena": None,
        "interval": 5,
        "merenja": [
            {**baza(), "rpmCPUVentilator": 1200},
            {**baza(), "rpmCPUVentilator": 1050},
            {**baza(), "rpmCPUVentilator":  880},
            {**baza(), "rpmCPUVentilator":  720},
            {**baza(), "rpmCPUVentilator":  560},
        ],
    },
}


# ---------------------------------------------------------------------------
# WebSocket / STOMP klijent
# ---------------------------------------------------------------------------

def _ws_on_open(ws):
    ws.send("CONNECT\naccept-version:1.1,1.2\nhost:localhost\n\n\x00")

def _ws_on_message(ws, message):
    if "CONNECTED" in message:
        ws.send("SUBSCRIBE\nid:sub-0\ndestination:/topic/alarmi\n\n\x00")
        print("[WS] Konekcija uspostavljena, osluskujem /topic/alarmi ...\n")
    elif "MESSAGE" in message:
        try:
            body = message.split("\n\n", 1)[1].rstrip("\x00")
            alarm = json.loads(body)
            print()
            print("!" * 62)
            print(f"  [CEP ALARM]  tip    : {alarm.get('tip')}")
            print(f"               poruka : {alarm.get('poruka')}")
            print("!" * 62)
            print()
        except Exception:
            pass

def _ws_on_error(ws, error):
    print(f"[WS] Greska konekcije: {error}")

def _ws_on_close(ws, *args):
    print("[WS] Konekcija zatvorena.")

def pokreni_ws():
    ws = websocket.WebSocketApp(
        WS_URL,
        on_open=_ws_on_open,
        on_message=_ws_on_message,
        on_error=_ws_on_error,
        on_close=_ws_on_close,
    )
    ws.run_forever(reconnect=3)


# ---------------------------------------------------------------------------
# Slanje merenja
# ---------------------------------------------------------------------------

def posalji_merenje(merenje: dict, redni: int, ukupno: int) -> bool:
    try:
        r = requests.post(f"{BASE_URL}/merenja", json=merenje, timeout=5)
        status = "OK" if r.status_code == 200 else f"GRESKA {r.status_code}"
        print(f"  Merenje {redni}/{ukupno} [{status}]", end="")

        bazno = baza()
        razlike = {k: v for k, v in merenje.items() if bazno.get(k) != v}
        if razlike:
            print(f"  -->  {', '.join(f'{k}={v}' for k, v in razlike.items())}", end="")
        print()
        return r.status_code == 200
    except requests.exceptions.ConnectionError:
        print(f"  Merenje {redni}/{ukupno} [GRESKA] Server nije dostupan na {BASE_URL}")
        return False


def pokreni_scenario(naziv: str):
    if naziv not in SCENARIJI:
        print(f"  Nepoznat scenario: '{naziv}'")
        print(f"  Dostupni: {', '.join(SCENARIJI.keys())}")
        return

    s = SCENARIJI[naziv]
    print()
    print("=" * 62)
    print(f"  {s['naziv']}")
    print(f"  {s['opis']}")
    if s.get("napomena"):
        print(f"  NAPOMENA: {s['napomena']}")
    print(f"  Interval: {s['interval']}s   |   Merenja: {len(s['merenja'])}")
    print("=" * 62)
    print()

    for i, merenje in enumerate(s["merenja"], 1):
        ok = posalji_merenje(merenje, i, len(s["merenja"]))
        if not ok:
            print("  Zaustavljanje zbog greske.")
            return
        if i < len(s["merenja"]):
            print(f"  Cekam {s['interval']}s ...")
            time.sleep(s["interval"])

    print()
    print("  Sva merenja su poslata. Cekam na CEP alarm putem WebSocket-a ...")
    print()


# ---------------------------------------------------------------------------
# Interaktivni meni
# ---------------------------------------------------------------------------

def ispisi_meni():
    print()
    print("=" * 62)
    print("  CEP SIMULATOR")
    print("=" * 62)
    for kljuc, s in SCENARIJI.items():
        print(f"  [{kljuc}]  {s['naziv']}")
    print("  [q]      Izlaz")
    print("=" * 62)


# ---------------------------------------------------------------------------
# Ulazna tacka
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    ws_nit = threading.Thread(target=pokreni_ws, daemon=True)
    ws_nit.start()
    time.sleep(1)

    if len(sys.argv) > 1:
        pokreni_scenario(sys.argv[1].lower())
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            pass
        sys.exit(0)

    try:
        while True:
            ispisi_meni()
            izbor = input("  Izbor: ").strip().lower()
            if izbor in ("q", "quit", "exit"):
                break
            elif izbor in SCENARIJI:
                pokreni_scenario(izbor)
                input("  Pritisni Enter za povratak na meni ...")
            else:
                print(f"  Nepoznata opcija: '{izbor}'")
    except KeyboardInterrupt:
        pass
