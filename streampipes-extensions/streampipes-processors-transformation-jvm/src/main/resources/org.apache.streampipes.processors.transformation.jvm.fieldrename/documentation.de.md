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

## Feld-Umbenenner

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Feld-Umbenenner-Prozessor ändert die Namen von Feldern in Ereignissen. Er unterstützt:
* Einzelfeld-Umbenennung
* Benutzerdefinierte Feldnamen
* Laufzeitnamen-Modifikation
* Feldnamen-Standardisierung

Dieser Prozessor ist essentiell für:
* Standardisierung von Feldnamen
* Erstellung konsistenter Benennung
* Abbildung von Feldidentifikatoren
* Verbesserung der Lesbarkeit

***

## Erforderliche Eingabe

Der Prozessor benötigt einen Datenstrom, der mindestens ein umzubenennendes Feld enthält.

***

## Konfiguration

### Feld

Wähle das umzubenennende Feld aus. Der Name dieses Feldes wird im Ausgabe-Ereignis geändert.

### Neuer Feldname

Gib den neuen Namen für das ausgewählte Feld ein. Dieser Name ersetzt den ursprünglichen Feldnamen im Ausgabe-Ereignis.

## Ausgabe

Der Prozessor erstellt ein neues Ereignis, das enthält:
* Alle ursprünglichen Felder aus dem Eingabe-Ereignis
* Das ausgewählte Feld mit seinem neuen Namen
* Alle Feldwerte bleiben unverändert

### Beispiel

#### Eingabe-Ereignis
```json
{
  "deviceId": "sensor01",
  "temp": 23.5,
  "humidity": 45.2,
  "timestamp": 1586380104915
}
```

#### Konfiguration
* Feld: temp
* Neuer Feldname: temperature

#### Ausgabe-Ereignis
```json
{
  "deviceId": "sensor01",
  "temperature": 23.5,
  "humidity": 45.2,
  "timestamp": 1586380104915
}
```

## Anwendungsfälle

1. **Datenstandardisierung**
   * Standardisierung von Feldnamen
   * Erstellung von Benennungskonventionen
   * Abbildung von Feldidentifikatoren
   * Verbesserung der Konsistenz

2. **Systemintegration**
   * Abbildung von Feldnamen
   * Standardisierung von Schnittstellen
   * Erstellung von Konsistenz
   * Aufbau von Abbildungen

## Hinweise

* Nur Feldnamen werden geändert
* Feldwerte bleiben unverändert
* Ursprüngliche Feldreihenfolge wird beibehalten
* Verarbeitung ist zustandslos
* Mehrere Umbenennungen erfordern Verkettung 