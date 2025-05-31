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

## Numerischer Textfilter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Numerische-Textfilter-Prozessor kombiniert numerische und textbasierte Filterung in einem einzigen Prozessor. Er leitet Ereignisse nur weiter, wenn sowohl die numerischen als auch die Textbedingungen erfüllt sind. Dieser Prozessor ist ideal für:
* Komplexe Ereignisfilterung
* Mehrkriterien-Ereignisauswahl
* Datenvalidierung
* Ereignisweiterleitung basierend auf mehreren Bedingungen

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabeereignisstrom, der enthält:
* Mindestens ein numerisches Feld für numerische Vergleiche
* Mindestens ein Textfeld für String-Vergleiche

***

## Konfiguration

### Numerischer Filter
* **Feld**: Wählen Sie das numerische Feld aus, auf das die Filteroperation angewendet werden soll
* **Operation**: Wählen Sie aus den folgenden Vergleichsoperatoren:
  * **<** (Kleiner als)
  * **<=** (Kleiner als oder gleich)
  * **>** (Größer als)
  * **>=** (Größer als oder gleich)
  * **==** (Gleich)
  * **!=** (Ungleich)
* **Schwellenwert**: Geben Sie den numerischen Wert für den Vergleich an

### Textfilter
* **Feld**: Wählen Sie das Textfeld aus, auf das die Filteroperation angewendet werden soll
* **Operation**: Wählen Sie aus:
  * **MATCHES**: Exakte String-Übereinstimmung
  * **CONTAINS**: Teilstring-Übereinstimmung
* **Schlüsselwort**: Geben Sie den zu vergleichenden Text an

## Ausgabe
Der Prozessor leitet das Eingabeereignis nur weiter, wenn sowohl die numerischen als auch die Textbedingungen wahr ergeben.

### Beispiel

#### Eingabeereignis
```json
{
  "temperature": 25.5,
  "status": "active",
  "timestamp": 1586380104915
}
```

#### Konfiguration
Numerischer Filter:
* Feld: temperature
* Operation: >
* Schwellenwert: 20.0

Textfilter:
* Feld: status
* Operation: MATCHES
* Schlüsselwort: active

#### Ausgabeereignis
```json
{
  "temperature": 25.5,
  "status": "active",
  "timestamp": 1586380104915
}
```

## Anwendungsfälle

1. **Sensordatenfilterung**
   * Filterung von Sensorwerten basierend auf Wertebereichen und Status
   * Überwachung spezifischer Bedingungen in Sensordaten
   * Validierung von Sensorwerten gegen erwartete Muster

2. **Ereignisvalidierung**
   * Sicherstellung, dass Ereignisse sowohl numerische als auch kategorische Kriterien erfüllen
   * Validierung von Geschäftsregeln mit mehreren Bedingungen
   * Filterung von Ereignissen basierend auf komplexen Kriterien

3. **Datenqualitätskontrolle**
   * Filterung ungültiger oder unerwarteter Datenkombinationen
   * Sicherstellung, dass Daten Qualitätsschwellenwerte erfüllen
   * Validierung von Daten gegen Geschäftsregeln

## Hinweise

* Sowohl numerische als auch Textbedingungen müssen erfüllt sein, damit ein Ereignis weitergeleitet wird
* Numerische Vergleiche verwenden Fließkommapräzision (0.000001 Toleranz für Gleichheit)
* Textübereinstimmung berücksichtigt Groß-/Kleinschreibung
* Der Prozessor behält die ursprüngliche Ereignisstruktur bei
* Alle Felder aus dem Eingabeereignis werden in der Ausgabe einbezogen 