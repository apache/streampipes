<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

## Signalflanken-Filter

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Signalflanken-Filter-Prozessor erkennt und leitet Nachrichten weiter, wenn sich ein boolesches Signal ändert (Flankenerkennung). Er unterstützt:
* Steigende Flanke (FALSE -> TRUE)
* Fallende Flanke (TRUE -> FALSE)
* Beide Flanken
* Konfigurierbare Verzögerung
* Nachrichtenauswahloptionen

Dieser Prozessor ist essentiell für:
* Erkennung von Zustandsübergängen
* Überwachung von Signaländerungen
* Auslösen von Aktionen bei Zustandsänderungen
* Implementierung von flankengesteuerter Logik

***

## Erforderliche Eingabe

Der Prozessor benötigt einen Datenstrom, der mindestens ein boolesches Feld zur Überwachung von Zustandsänderungen enthält.

***

## Konfiguration

### Boolesches Signal

Wähle das boolesche Feld aus, das auf Zustandsänderungen überwacht werden soll. Dieses Feld wird für die Flankenerkennung verwendet.

### Signalflanke

Wähle die Art der zu erkennenden Flanke:

* **FALSE -> TRUE**: Steigende Flanke
  * Wird ausgelöst, wenn sich das Signal von false zu true ändert
  * Verwendung: Erkennung von Aktivierungsereignissen
  * Beispiel: Gerät eingeschaltet

* **TRUE -> FALSE**: Fallende Flanke
  * Wird ausgelöst, wenn sich das Signal von true zu false ändert
  * Verwendung: Erkennung von Deaktivierungsereignissen
  * Beispiel: Gerät ausgeschaltet

* **BEIDE**: Beide Flanken
  * Wird bei jeder Zustandsänderung ausgelöst
  * Verwendung: Überwachung aller Übergänge
  * Beispiel: Jede Zustandsänderung

### Verzögerung

Gib an, wie viele Nachrichten gewartet werden sollen, bevor das Ergebnis nach einer Flankenerkennung weitergeleitet wird:
* Minimum: 0 (sofortige Weiterleitung)
* Verwendung: Entprellen von Signalen
* Beispiel: 5 Nachrichten warten, um einen stabilen Zustand sicherzustellen

### Ausgabe-Nachrichtenauswahl

Wähle aus, welche Nachrichten nach der Verzögerung weitergeleitet werden sollen:

* **Erste**: Nur die erste Nachricht nach Flankenerkennung weiterleiten
  * Verwendung: Einmalige Aktionen
  * Beispiel: Prozess einmal starten

* **Letzte**: Nur die letzte Nachricht nach Verzögerung weiterleiten
  * Verwendung: Erfassung des Endzustands
  * Beispiel: Stabilen Zustand erfassen

* **Alle**: Alle Nachrichten während der Verzögerung weiterleiten
  * Verwendung: Kontinuierliche Überwachung
  * Beispiel: Zustandsänderungen verfolgen

## Ausgabe

Der Prozessor erstellt eine neue Nachricht, die enthält:
* Alle ursprünglichen Felder aus der Eingabe-Nachricht
* Die Nachricht wird basierend auf der konfigurierten Verzögerung und Nachrichtenauswahl weitergeleitet

### Beispiel

#### Eingabe-Nachrichtenstrom
```json
[
  {
    "deviceId": "sensor01",
    "isActive": false,
    "timestamp": 1586380104915
  },
  {
    "deviceId": "sensor01",
    "isActive": true,
    "timestamp": 1586380105015
  },
  {
    "deviceId": "sensor01",
    "isActive": true,
    "timestamp": 1586380105115
  }
]
```

#### Konfiguration 1: Steigende Flanke mit erster Nachricht
* Boolesches Signal: isActive
* Signalflanke: FALSE -> TRUE
* Verzögerung: 0
* Ausgabe-Nachrichtenauswahl: Erste

#### Ausgabe-Nachricht
```json
{
  "deviceId": "sensor01",
  "isActive": true,
  "timestamp": 1586380105015
}
```

#### Konfiguration 2: Beide Flanken mit letzter Nachricht
* Boolesches Signal: isActive
* Signalflanke: BEIDE
* Verzögerung: 1
* Ausgabe-Nachrichtenauswahl: Letzte

#### Ausgabe-Nachricht
```json
{
  "deviceId": "sensor01",
  "isActive": true,
  "timestamp": 1586380105115
}
```

## Anwendungsfälle

1. **Geräteüberwachung**
   * Erkennung von Gerätestromzustandsänderungen
   * Überwachung der Sensoraktivierung
   * Verfolgung des Gerätestatus
   * Erkennung von Systemübergängen

2. **Prozesssteuerung**
   * Auslösen von Aktionen bei Zustandsänderungen
   * Überwachung von Prozessübergängen
   * Erkennung von Phasenänderungen
   * Verfolgung von Zustandsautomaten

## Hinweise

* Nur boolesche Felder können überwacht werden
* Flankenerkennung ist zustandsbehaftet
* Verzögerung ist nachrichtenbasiert, nicht zeitbasiert
* Nachrichtenauswahl beeinflusst das Ausgabeverhalten
* Verarbeitung ist zustandsbehaftet
* Flankenerkennung erfolgt sofort
* Verzögerung beginnt nach Flankenerkennung
* Nachrichtenauswahl wird nach Verzögerung angewendet 