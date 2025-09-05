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

## Listenfilter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Listenfilter-Prozessor filtert Ereignisse basierend auf dem Vorhandensein eines bestimmten Werts in einem Listenfeld. Er:
* Prüft, ob ein Wert in einer Liste existiert
* Filtert Ereignisse basierend auf der Listenmitgliedschaft
* Behält die ursprünglichen Ereignisdaten bei
* Funktioniert mit jedem Listenfeldtyp
* Unterstützt exakte Werteabgleichung

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabeereignisstrom mit mindestens einem Listenfeld zum Filtern.

***

## Konfiguration

### Listenfeld
Wählen Sie das Listenfeld aus, das auf den erforderlichen Wert geprüft werden soll. Das Feld muss vom Typ Liste sein.

### Erforderlicher Wert
Geben Sie den Wert an, der in der Liste gesucht werden soll. Der Prozessor gibt nur Ereignisse aus, bei denen dieser Wert im ausgewählten Listenfeld vorhanden ist.

## Ausgabe
Der Prozessor gibt nur die Ereignisse aus, bei denen der angegebene Wert im ausgewählten Listenfeld gefunden wird.

### Beispiel

#### Eingabeereignis
```json
{
  "sensor_id": "sensor1",
  "measurements": [22.1, 23.4, 24.2, 25.0, 25.5]
}
```

#### Konfiguration
* Listenfeld: `measurements`
* Erforderlicher Wert: `25.0`

#### Ausgabeereignis
Der Prozessor gibt das Ereignis aus, da 25.0 in der measurements-Liste vorhanden ist.

## Anwendungsfälle

1. **Datenfilterung**
   * Ereignisse nach Listenmitgliedschaft filtern
   * Spezifische Wertvorkommen auswählen
   * Basierend auf Wertvorhandensein filtern
   * Wertbasierte Teilmengen erstellen

2. **Ereignisauswahl**
   * Ereignisse mit bestimmten Werten auswählen
   * Basierend auf Wertexistenz filtern
   * Wertbasierte Ereignisströme erstellen
   * Wertbasierte Weiterleitung implementieren

3. **Qualitätskontrolle**
   * Gültige Messungen filtern
   * Ereignisse mit erwarteten Werten auswählen
   * Basierend auf Wertkriterien filtern
   * Wertbasierte Validierung implementieren

## Hinweise

* Der Prozessor führt exakte Werteabgleichung durch
* Die ursprünglichen Ereignisdaten werden beibehalten
* Der Prozessor funktioniert mit jedem Listenfeldtyp
* Ereignisse werden nur ausgegeben, wenn der Wert gefunden wird
* Die Listenreihenfolge beeinflusst die Filterung nicht 