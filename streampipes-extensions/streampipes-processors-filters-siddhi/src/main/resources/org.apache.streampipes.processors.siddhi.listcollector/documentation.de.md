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

## Listensammler

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Listensammler-Prozessor aggregiert Werte aus einem angegebenen Feld in einer Liste innerhalb eines konfigurierbaren Batch-Fensters. Er:
* Sammelt Feldwerte über die Zeit
* Erstellt eine Liste aller Werte im Fenster
* Behält die ursprünglichen Ereignisdaten bei
* Fügt ein neues Listenfeld zur Ausgabe hinzu
* Funktioniert mit jedem Feldtyp

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabeereignisstrom mit mindestens einem Feld, aus dem Werte gesammelt werden sollen.

***

## Konfiguration

### Feld
Wählen Sie das Feld aus, dessen Werte in eine Liste gesammelt werden sollen. Das Feld kann von jedem Datentyp sein.

### Batch-Fenstergröße
Geben Sie die Anzahl der Ereignisse an, die in jedes Batch-Fenster aufgenommen werden sollen. Der Prozessor sammelt Werte aus dieser Anzahl von Ereignissen, bevor eine neue Liste erstellt wird.

## Ausgabe
Der Prozessor gibt das ursprüngliche Ereignis mit einem zusätzlichen Feld aus, das die gesammelte Liste von Werten enthält.

### Beispiel

#### Eingabeereignis
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

#### Konfiguration
* Feld: `temperature`
* Batch-Fenstergröße: `5`

#### Ausgabeereignis
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115,
  "temperature_list": [22.1, 23.4, 24.2, 25.0, 25.5]
}
```

## Anwendungsfälle

1. **Datenaggregation**
   * Zeitreihendaten sammeln
   * Werteverläufe erstellen
   * Änderungen über die Zeit verfolgen
   * Datenfenster aufbauen

2. **Analysevorbereitung**
   * Daten für statistische Analyse vorbereiten
   * Eingabe für Trenderkennung erstellen
   * Daten für Mustererkennung generieren
   * Merkmalsvektoren aufbauen

3. **Überwachung**
   * Werteverteilungen verfolgen
   * Wertebereiche überwachen
   * Wertemuster analysieren
   * Wertemomentaufnahmen erstellen

## Hinweise

* Der Prozessor erstellt ein neues Listenfeld mit dem ursprünglichen Feldnamen plus "_list"
* Listen werden nach der angegebenen Anzahl von Ereignissen erstellt
* Die ursprünglichen Ereignisdaten werden beibehalten
* Der Prozessor funktioniert mit jedem Feldtyp
* Listen werden nach jedem Batch-Fenster gelöscht 