# LoadTesterApp & GrainWeightApp

## Cíl práce

Cílem této bakalářské práce je analyzovat a porovnat různé přístupy ke zpracování HTTP požadavků v Javě — zejména z hlediska výkonu, propustnosti a stability serveru při vysoké zátěži.

Součástí projektu je vytvoření dvojice aplikací, které spolu komunikují přes REST API:

- LoadTesterApp – nástroj pro generování, řízení a vyhodnocování testovacích požadavků,
- GrainWeightApp – cílová (testovaná) aplikace, která požadavky zpracovává různými způsoby (režimy) a vrací metriky výkonu.

Hlavním výstupem práce je porovnání jednotlivých přístupů zpracování a jejich dopad na chování serveru při velké zátěži s využitím paralelismu a concurrency.

---

## Architektura projektu

Projekt je rozďdělen do dvou nezávislých částí, které spolu komunikují přes REST API:

### LoadTesterApp

- Hlavní aplikace pro vytváření, spouštění a správu testů.
- Umožňuje uživateli definovat testy (URL, počet požadavků, režim zpracování, scénář apod.).
- Každý test odesílá požadavky na GrainWeightApp a po ukončení zpracování vyhodnocuje metriky ( úspěšnost, propustnost, doba odezvy, vytížení serveru).
- Ukládá výsledky testů a přehledné souhrny do databáze PostgreSQL.
- Nabízí React frontend, kde lze testy vytvářet, sledovat a mazat.

### GrainWeightApp

- Jednoduchá  aplikace původně vytvořená jako školní projekt pro předmět PRO2.
- Pro potřeby bakalářské práce byla rozšířena o různé režimy zpracování požadavků, měření výkonu a sledování využití hardwaru.
- Každý požadavek vrací informace o čase čekání, době zpracování a dalších metrikách, které slouží jako vstup pro analýzu.

---

## Režimy zpracování požadavků

GrainWeightApp dokáže zpracovávat požadavky různými způsoby – tzv. strategiemi zpracování:

| Režim     | Popis                                                                | Implementace                                  |
| --------- | -------------------------------------------------------------------- | --------------------------------------------- |
| `SERIAL`  | Požadavky jsou zpracovávány sekvenčně pomocí semaforu (jedno vlákno) | `Semaphore(1, true)`                          |
| `POOL`    | Zpracování pomocí pevného thread poolu s danou kapacitou             | `Executors.newFixedThreadPool(n)`             |
| `VIRTUAL` | Každý požadavek běží ve vlastním virtuálním vlákně                   | `Executors.newVirtualThreadPerTaskExecutor()` |

Každý režim má odlišný dopad na výkon a chování serveru při vysokém počtu požadavků, což je hlavním předmětem zkoumání.

---

## Scénáře testování

Aplikace podporuje dva testovací scénáře:

- STEADY – pevný počet požadavků a konstantní míra souběžnosti.
- RAMP-UP – počet současně odesílaných požadavků se postupně zvyšuje během testu.
  Tento scénář slouží ke sledování tzv. „breakpointu“ – bodu, kdy server přestává zvládat rostoucí zátěž.

---

## Databáze

Databázová vrstva (PostgreSQL) uchovává:

- uživatele a jejich role (USER, ADMIN),
- testovací běhy (`test_run`) včetně všech souhrnných metrik,
- záznamy o využití hardwaru během testu (`test_run_hw_sample`).

Každý test má jednoznačné ID, přiřazeného autora a souhrn metrik (počet úspěšných požadavků, doba odezvy, propustnost, využití CPU atd.).

---

## Zabezpečení a role

Aplikace používá Spring Security a RBAC (Role-Based Access Control).

### Role:

- USER – může vytvářet, spouštět a mazat pouze své testy,
- ADMIN – má přístup ke všem testům, může je schvalovat, mazat a sledovat souhrnné statistiky.

Přístup k endpointům je řízen pomocí `SecurityFilterChain`, který je nastaven ke spolupráci s React frontendem.

---

## Vyhodnocování výsledků

Po dokončení testu se provede agregace dat (souhrnné metriky):

- počet úspěšných a neúspěšných požadavků,
- průměrná a 95. percentilová doba odezvy,
- propustnost (RPS),
- průměrná doba zpracování a čekání na serveru,
- úspěšnost (%).

Výsledky jsou uloženy do tabulky `test_run` a zobrazeny na frontendu (grafy, přehledy).

---

## Použité technologie

| Vrstva      | Technologie                                    | Účel                                             |   
| ----------- | ---------------------------------------------- | ------------------------------------------------ | 
| Backend     | Java 21, Spring Boot, WebFlux, JPA (Hibernate) | Aplikační logika, REST API, zpracování požadavků |   
| Frontend    | React + TypeScript + Vite                      | Uživatelské rozhraní a vizualizace výsledků      |   
| Databáze    | PostgreSQL / H2 (dev)                          | Ukládání testů, uživatelů, metrik                |   
| Zabezpečení | Spring Security                                | Autentizace, autorizace, správa rolí             |   
                                                                                                                  

## Principy a návrhové vzory:

- Layered Architecture – oddělení prezentační, servisní a datové vrstvy,
- MVC pattern – Controller ↔ Service ↔ Repository,
- Dependency Injection (IoC) – řízení závislostí pomocí Spring Containeru,
- Repository Pattern – správa entit pomocí JPA,
- Strategy Pattern – volba způsobu zpracování (SERIAL / POOL / VIRTUAL) za běhu.

---

## Shrnutí

Projekt představuje řešení pro zátěžové testování webových aplikací.
Ukazuje praktické rozdíly mezi sekvenčním, paralelním a virtuálním zpracování požadavků v Javě, a umožňuje analyzovat výkon a stabilitu serveru při různých úrovních zátěže.



