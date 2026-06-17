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

### 4. Pokretanje testova

Pokretanje svih testova:

```powershell
cd ..\service
.\mvnw.cmd test
```

Pokretanje pojedinacnih testova:
- po nivou pravila
- cep
- scenariji (integracioni)

```powershell
.\mvnw.cmd test -Dtest=NivoPravilaTest
.\mvnw.cmd test -Dtest=CepPravilaTest
.\mvnw.cmd test -Dtest=ScenarioTest
```

### 5. Pokretanje frontend-a

```powershell
cd ..\frontend
ng serve
```

Frontend se pokrece na `http://localhost:4200`.
