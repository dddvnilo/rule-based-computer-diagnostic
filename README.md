# Rule-Based Computer Diagnostic

## Pokretanje

### 1. Instalacija model

```powershell
cd model
.\mvnw.cmd install
```

### 2. Instalacija kjar

```powershell
cd ..\kjar
.\mvnw.cmd install
```

### 3. Pokretanje service

```powershell
cd ..\service
.\mvnw.cmd spring-boot:run
```

Servis se pokrece na `http://localhost:8080`.

## Testiranje

### REST endpoint

```
POST http://localhost:8080/dijagnoza
Content-Type: application/json
```

Primer tela zahteva:

```json
{
  "temperaturaCPU": 97.0,
  "temperaturaGPU": 65.0,
  "rpmVentilator": 450,
  "memtestGreske": 0,
  "napon12V": 12.1,
  "packetLoss": 0.5,
  "artefaktiNaEkranu": false,
  "bsod": false,
  "bsodKod": null,
  "neobicniZvukovi": true,
  "zamrzavanje": false
}
```

### Simulator

Python skripta sa predefinisanim scenarijima: 

```powershell
python simulator.py
```
