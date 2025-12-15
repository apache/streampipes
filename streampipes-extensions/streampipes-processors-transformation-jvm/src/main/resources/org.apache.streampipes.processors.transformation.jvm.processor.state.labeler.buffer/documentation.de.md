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

## Zustands-Puffer-Beschriftung

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Zustands-Puffer-Beschriftungs-Prozessor fügt Beschriftungen zu Sensor-Zeitreihendaten basierend auf statistischen Operationen und benutzerdefinierten Regeln hinzu. Er unterstützt:
* Zustandsbasierte Beschriftung
* Statistische Operationen (Minimum, Maximum, Durchschnitt)
* Benutzerdefinierte Regeldefinition
* Mehrere Bedingungen
* Standardbeschriftungen

Dieser Prozessor ist essentiell für:
* Kontext zu Daten hinzufügen
* Messungen klassifizieren
* Muster identifizieren
* Bedingungen markieren

***

## Erforderliche Eingabe

Der Prozessor benötigt einen Datenstrom, der enthält:
* Ein Zustandsfeld (Array von Strings)
* Ein Sensorwertfeld (Array von Zahlen)

***

## Konfiguration

### Zustandsfeld

Wähle das Feld aus, das die Zustandsinformationen enthält. Dies bestimmt, wann Regeln angewendet werden.

### Wähle einen bestimmten Zustand

Füge einen Filter hinzu, um zu definieren, welche Zustände ausgewertet werden sollen. Verwende '*' um alle Zustände auszuwählen.

### Sensorwerte

Wähle das Array aus, das die zu bewertenden Sensorwerte enthält.

### Operation

Definiere die statistische Operation, die auf die Sensorwerte angewendet werden soll:
* Minimum: Niedrigsten Wert ermitteln
* Maximum: Höchsten Wert ermitteln
* Durchschnitt: Mittelwert berechnen

### Bedingung

Füge Bedingungen im Format hinzu:
* `<;5;ok` - Als "ok" beschriften, wenn der Wert kleiner als 5 ist
* `<;10;ok` - Als "ok" beschriften, wenn der Wert kleiner als 10 ist
* `*;nok` - Standardbeschriftung "nok" für alle anderen Fälle

## Ausgabe

Der Prozessor erstellt eine neue Nachricht, die enthält:
* Alle ursprünglichen Felder aus der Eingabe-Nachricht
* Ein neues Beschriftungsfeld basierend auf den Bedingungen

### Beispiel

#### Eingabe-Nachricht
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "state": ["active"],
  "values": [23.5, 24.1, 24.3]
}
```

#### Konfiguration
* Zustandsfeld: state
* Wähle einen bestimmten Zustand: active
* Sensorwerte: values
* Operation: Durchschnitt
* Bedingung: "<;20;kalt", "<;30;warm", "*;heiß"

#### Ausgabe-Nachricht
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "state": ["active"],
  "values": [23.5, 24.1, 24.3],
  "label": "warm"
}
```

## Anwendungsfälle

1. **Datenklassifizierung**
   * Kontext zu Daten hinzufügen
   * Messungen klassifizieren
   * Muster identifizieren
   * Bedingungen markieren

2. **Qualitätskontrolle**
   * Qualitätsstufen beschriften
   * Schwellenwerte markieren
   * Probleme identifizieren
   * Bedingungen verfolgen

## Hinweise

* Bedingungen werden in Reihenfolge ausgewertet
* Standardbeschriftung ist erforderlich
* Zustandsfilterung ist optional
* Verarbeitung ist zustandslos
* Mehrere Bedingungen werden unterstützt
* Statistische Operation wird vor der Bedingungsauswertung angewendet
* Eingabe-Arrays müssen numerische Werte enthalten
* Zustandsfeld muss ein Array von Strings sein 