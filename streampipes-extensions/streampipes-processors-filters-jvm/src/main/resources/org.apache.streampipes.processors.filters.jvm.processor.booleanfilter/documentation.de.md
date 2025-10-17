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

## Boolean-Filter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Boolean-Filter-Prozessor filtert Ereignisse basierend auf einem Boolean-Feldwert. Er unterstützt:
* Exakte Boolean-Wert-Übereinstimmung
* Ereignisweiterleitung bei Übereinstimmung
* Einfache true/false-Filterung
* Zustandsbasierte Ereignisfilterung

Dieser Prozessor ist wichtig für:
* Filterung von Ereignissen nach Boolean-Zustand
* Implementierung bedingter Ereignisweiterleitung
* Zustandsbasierte Ereignisverarbeitung
* Boolean-Bedingungsfilterung

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Datenstrom, der mindestens ein zu filterndes Boolean-Feld enthält.

***

## Konfiguration

### Feld
Wählen Sie das zu filternde Boolean-Feld aus. Der Prozessor prüft den Wert dieses Feldes gegen den ausgewählten Filterwert.

### Feldwert
Wählen Sie, ob Ereignisse behalten werden sollen, bei denen der Feldwert ist:
* True - Nur Ereignisse mit true-Werten werden weitergeleitet
* False - Nur Ereignisse mit false-Werten werden weitergeleitet

## Ausgabe
Der Prozessor erstellt ein neues Ereignis, das alle ursprünglichen Felder aus dem Eingabeereignis enthält, aber nur wenn das ausgewählte Boolean-Feld dem konfigurierten Wert entspricht.

### Beispiel

#### Eingabeereignisstrom
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "isActive": true,
  "timestamp": 1586380104915
}
```
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "isActive": false,
  "timestamp": 1586380105015
}
```

#### Konfiguration
* Feld: isActive
* Feldwert: True

#### Ausgabeereignis
```json
{
  "deviceId": "sensor01",
  "location": "l1",
  "isActive": true,
  "timestamp": 1586380104915
}
```

## Anwendungsfälle

1. **Zustandsfilterung**
   * Filterung aktiver/inaktiver Zustände
   * Verarbeitung nur aktivierter Geräte
   * Behandlung von Betriebszuständen
   * Filterung nach Statusflags

2. **Bedingte Verarbeitung**
   * Weiterleitung von Ereignissen nach Bedingung
   * Filterung nach Boolean-Flags
   * Verarbeitung basierend auf Zustand
   * Behandlung von Boolean-Triggern

## Hinweise

* Nur exakte Boolean-Übereinstimmungen werden unterstützt
* Ereignisse werden unverändert weitergeleitet
* Keine Transformation von Werten
* Einfache true/false-Filterung
* Ursprüngliche Ereignisstruktur wird beibehalten 