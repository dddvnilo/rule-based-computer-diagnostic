# Predlog projekta - Sistemi bazirani na znanju (SBNZ)
## Sistem za dijagnostiku kvarova na računaru

Danilo Drobnjak SV19/2022


## 1. Motivacija

Savremeni računari su kompleksni sistemi sastavljeni od velikog broja međusobno zavisnih hardverskih i softverskih komponenti. Kada dođe do kvara, prosečan korisnik najčešće nema dovoljno tehničkog znanja da identifikuje uzrok problema.

Motivacija za ovaj projekat leži u potrebi za sistemom koji korisniku može pomoći da brzo dijagnostikuje probleme na svom računaru. Sistemi bazirani na znanju prirodno se uklapaju u ovaj problem - dijagnostika kvarova oslanja se na skup pravila koja se primenjuju sistematično, od simptoma ka zaključku o kvaru.


## 2. Pregled problema

Postoje komercijalni alati za praćenje stanja računara kao što su HWiNFO, CrystalDiskInfo i Windows Reliability Monitor, međutim oni imaju nekoliko ključnih nedostataka:

- Prikazuju sirove podatke bez automatskog zaključivanja - korisnik mora sam da interpretira vrednosti.
- Ne integrišu više izvora simptoma u jedinstven dijagnostički zaključak.
- Ne prate dinamiku događaja kroz vreme (npr. učestalost grešaka ili trendove temperatura).
- Ne podržavaju interaktivnu proveru korisničkih hipoteza o uzroku kvara.
- Ne uzimaju u obzir međuzavisnosti između komponenti (npr. loše napajanje koje utiče na više komponenti istovremeno).

Predloženo rešenje razlikuje se od postojećih po tome što:

- Koristi rule-based rezonovanje sa ulančavanjem pravila.
- Integriše Complex Event Processing (CEP) za detekciju ponavljajućih obrazaca ponašanja u realnom vremenu.
- Uzima u obzir kombinovane kvarove i međuzavisnosti između komponenti.
- Generiše objašnjenja i konkretne preporuke razumljive korisniku bez tehničkog predznanja.


## 3. Metodologija rada

### 3.1 Ulazi u sistem (Input)

Sistem prihvata dve vrste ulaza:

**Simptomi koje korisnik bira kroz interfejs (predefinisane opcije):**
- Spor rad sistema
- Plavi ekran (BSOD) sa ili bez error koda
- Pregrevanje (računar se sam gasi)
- Neobični zvukovi (škripanje, zujanje)
- Učestali restartovi
- Zamrzavanje sistema (freeze)
- Artefakti na ekranu (pikseli, linije, glitchevi)
- Problemi sa mrežnom konekcijom (packet loss, pad brzine)
- Nestabilnost OS-a (crashevi programa, spor boot)

**Hardverski podaci prikupljeni iz simulatora:**
- Temperatura CPU-a i GPU-a (°C) pri idle i load stanju
- Procenat zauzetosti RAM-a i greške iz memtest alata
- SMART parametri diska: reallocated sectors, pending sectors, uncorrectable errors, power-on hours
- Napon napajanja na +12V, +5V i +3.3V
- Brzina ventilatora po komponentama (RPM): CPU, GPU, case
- Temperatura chipseta matične ploče i POST status kodovi
- Windows Event Log greške i BSOD dump kodovi
- Mrežne metrike: packet loss %, ping stabilnost, brzina


### 3.2 Izlazi iz sistema (Output)

Na osnovu ulaznih podataka, sistem generiše:

- **Identifikovanu komponentu** koja je izvor problema (CPU, GPU, RAM, Disk, PSU, Motherboard, Cooling system, Network, OS/Softver).
- **Tip kvara** (pregrevanje, fizički kvar, softverski konflikt, nestabilno napajanje, degradacija, driver problem itd.).
- **Nivo ozbiljnosti:** KRITIČNO / UPOZORENJE / INFO.
- **Objašnjenje zaključka** - koji simptomi i podaci su doveli do dijagnoze.

### 3.3 Baza znanja

Baza znanja sistema sastoji se od sledećih elemenata:

#### Templejti komponenti

Svaka od 9 komponenti modelovana je kao templejt koji sadrži relevantne atribute, normalne opsege vrednosti i moguće kvarove:

| Komponenta | Ključni atributi | Mogući kvarovi |
|---|---|---|
| **CPU** | temperatura, utilization % | pregrevanje |
| **GPU** | temperatura, fan speed (RPM) | pregrevanje, VRAM kvar |
| **RAM** | zauzetost %, memtest greške | fizički kvar ćelija |
| **Disk** | SMART parametri, temperatura, power-on hours | fizički kvar, loši sektori, istrošenost |
| **PSU** | napon po linijama (+12V, +5V, +3.3V), wattage | nestabilan napon |
| **Motherboard** | temperatura chipseta, POST kodovi, VRM status | loši kondenzatori, VRM kvar, BIOS problem |
| **Cooling system** | RPM po ventilatorima, broj ventilatora, case temperatura | istrošen ležaj, ventilator stao |
| **Network** | packet loss %, ping, brzina konekcije | driver konflikt, fizički kvar čipa |
| **OS/Softver** | event log greške | zastareli drajveri, corrupt fajlovi |

#### Pravila (IF-THEN)

Pravila su organizovana u 3 nivoa ulančavanja:

- **Nivo 1** - Sirovi podaci i simptomi -> identifikacija relevantnih komponenti. Na osnovu odabranih simptoma i vrednosti iz simulatora, sistem mapira svaki simptom na jednu ili više komponenti koje su potencijalni uzrok (npr. artefakti na ekranu -> GPU, BSOD kod MEMORY_MANAGEMENT -> RAM).
- **Nivo 2** - Komponenta + atributi -> tip kvara. Kada je komponenta identifikovana, sistem gleda njene konkretne atribute iz templejta i određuje tip kvara (npr. sumnja na Cooling system + RPM < 500 -> tip = kvar ventilatora; RPM normalan ali temperatura visoka -> tip = istrošena termalna pasta).
- **Nivo 3** - Tip kvara -> ozbiljnost i preporuka. Tip kvara direktno određuje nivo ozbiljnosti i konkretnu preporuku za akciju. Ozbiljnost može biti fiksna (npr. fizički kvar RAM-a -> uvek KRITIČNO) ili zavisna od vrednosti (npr. pregrevanje GPU -> UPOZORENJE pri 85°C, KRITIČNO pri 100°C+). Kao finalni nivo ozbiljnosti uzima se najgori među svim detektovanim kvarovima.

#### CEP pravila

Pravila za praćenje ponavljajućih obrazaca ponašanja kroz vreme.

#### Popunjavanje baze znanja

Vrednosti hardverskih parametara (temperature, RPM, naponi, SMART parametri, BSOD kodovi i sl.) prikupljaju se iz simulatora koji reprodukuje realne uslove rada računara. Sama pravila su ručno definisana na osnovu ekspertskog znanja i tehničke dokumentacije proizvođača hardvera.


### 3.4 Forward Chaining - nivoi ulančavanja

**Nivo 1 - Simptomi -> Komponenta:**
```
AKO artefakti_na_ekranu = TAČNO
ONDA sumnja_na_komponentu = GPU

AKO BSOD_kod = "MEMORY_MANAGEMENT"
ONDA sumnja_na_komponentu = RAM

AKO packet_loss > 10%
ONDA sumnja_na_komponentu = NETWORK
```

**Nivo 2 - Komponenta + atributi -> Tip kvara:**
```
AKO sumnja_na_komponentu = GPU
  I temperatura_GPU > 95°C
ONDA tip_kvara = PREGREVANJE_GPU

AKO sumnja_na_komponentu = RAM
  I memtest_greske > 0
ONDA tip_kvara = FIZICKI_KVAR_RAM

AKO sumnja_na_komponentu = COOLING
  I rpm_ventilator < 500
ONDA tip_kvara = KVAR_VENTILATORA

AKO sumnja_na_komponentu = COOLING
  I rpm_ventilator >= 500
  I temperatura_CPU > 90
ONDA tip_kvara = ISTROSENA_TERMALNA_PASTA
```

**Nivo 3 - Tip kvara -> Ozbiljnost i preporuka:**
```
AKO tip_kvara = KVAR_VENTILATORA
ONDA ozbiljnost = KRITICNO
     preporuka = "Hitno isključiti računar. Proveriti CPU ventilator (moguć kvar ležaja ili začepljenje prašinom)."

AKO tip_kvara = FIZICKI_KVAR_RAM
ONDA ozbiljnost = KRITICNO
     preporuka = "Zameniti RAM modul. Do tada izbegavati upotrebu računara."

AKO tip_kvara = ISTROSENA_TERMALNA_PASTA
ONDA ozbiljnost = UPOZORENJE
     preporuka = "Planirati zamenu termalne paste na CPU-u."

AKO tip_kvara = DRIVER_KONFLIKT
ONDA ozbiljnost = UPOZORENJE
     preporuka = "Reinstalirati drajvere za problematičnu komponentu."

AKO vise_komponenti_u_kvaru = TAČNO
  I napon_nestabilan = TAČNO
ONDA ozbiljnost = KRITICNO
     preporuka = "Proveriti napajanje - nestabilan napon može biti uzrok problema na više komponenti."
```


### 3.5 CEP - Complex Event Processing

CEP prati stream događaja kroz vreme i reaguje na **obrazac ponašanja**, ne na pojedinačne vrednosti. Jedan događaj nije alarm - obrazac jeste.

Konkretna CEP pravila u sistemu:

```
CEP-1: AKO temperatura_CPU > 90°C se pojavi 3+ puta u roku od 10 minuta
       ONDA alarm = "Kritično ponavljajuće pregrevanje CPU"

CEP-2: AKO SMART greška na disku zabeležena 5+ puta u toku jednog dana
       ONDA alarm = "Disk pokazuje znakove skorog otkaza"

CEP-3: AKO ping > 5x baseline vrednosti u 3+ merenja tokom 5 minuta
       ONDA alarm = "Nestabilan network adapter - detektovane nagle oscilacije latencije"

CEP-4: AKO napon_12V oscilira (gore-dole) u 5 uzastopnih merenja
       ONDA alarm = "Nestabilno napajanje - rizik od oštećenja komponenti"

CEP-5: AKO temperatura_GPU raste linearno za 10°C+ tokom 15 minuta
         BEZ povećanja GPU utilization-a
       ONDA alarm = "Moguć začepljen ventilator ili istrošena termalna pasta GPU"
```

## 4. Konkretan primer rezonovanja

**Scenario:** Korisnik prijavljuje da mu računar često restartuje i da je bučan.

### Korak 1 - Unos podataka

Korisnik kroz interfejs bira simptome: *učestali restartovi*, *bučan ventilator*.

Simulator generiše hardverske podatke:
- Temperatura CPU = 97°C
- Brzina CPU ventilatora = 450 RPM
- Napon +12V = stabilan
- SMART diska = uredu
- RAM greške = nema

### Korak 2 - Nivo 1: Identifikacija komponenti

```
R1: temperatura_CPU (97) > 90 -> sumnja_na_komponentu = CPU
R2: rpm_cpu_ventilator (450) < 500 -> sumnja_na_komponentu = COOLING
R3: korisnik_bira("ucestali_restartovi") -> simptom_nestabilnost = TAČNO
```

Sumnja usmerena na: **CPU** i **Cooling system**.

### Korak 3 - Nivo 2: Tip kvara

```
R4: sumnja_na_komponentu = COOLING
    I rpm_ventilator < 500
    -> tip_kvara = KVAR_VENTILATORA

R5: simptom_nestabilnost = TAČNO
    I sumnja_na_komponentu = CPU
    -> uzrok_nestabilnosti = TERMALNI
```

### Korak 4 - CEP alarm

Simulator generiše podatke koji pokazuju da je temperatura CPU-a prešla 90°C pet puta u poslednjih 10 minuta:

```
CEP-1 aktiviran -> nova cinjenica: alarm_pregrevanjae_CPU = TAČNO
```

Ova cinjenia se dodaje u radnu memoriju i učestvuje u daljem rezonovanju.

### Korak 5 - Nivo 3: Ozbiljnost i preporuka

```
R6: tip_kvara = KVAR_VENTILATORA
    I alarm_pregrevanje_CPU = TAČNO
    -> ozbiljnost = KRITICNO
       preporuka = "Hitno isključiti računar. Proveriti CPU ventilator (moguć kvar ležaja ili začepljenje prašinom)."
```

**Izlaz sistema:**
- **KRITIČNO** - Sistem hlađenja (Cooling system)
- Tip kvara: Kvar ventilatora
- Preporuka: Hitna fizička intervencija - čišćenje i/ili zamena ventilatora