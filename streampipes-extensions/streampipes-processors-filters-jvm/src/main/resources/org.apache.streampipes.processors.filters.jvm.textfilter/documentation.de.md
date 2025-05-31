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

## Textfilter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Textfilter-Prozessor filtert Ereignisse basierend auf Textfeldinhalten. Er ermöglicht:
* Exakte Textstring-Übereinstimmung
* Überprüfung auf Textenthaltenheit
* Filterung von Ereignissen basierend auf Textkriterien
* Weiterleitung von Ereignissen basierend auf Textinhalt

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabeereignisstrom, der mindestens ein zu filterndes Textfeld enthält.

***

## Konfiguration

### Textfeld
Wählen Sie das Feld aus, das den zu filternden Text enthält.

### Operation
Wählen Sie aus zwei Filteroperationen:
* **MATCHES**: Exakte String-Übereinstimmung (Groß-/Kleinschreibung beachten)
* **CONTAINS**: Teilstring-Übereinstimmung (Groß-/Kleinschreibung beachten)

### Schlüsselwort
Geben Sie den zu vergleichenden Textstring an.

## Ausgabe
Der Prozessor leitet das Eingabeereignis nur weiter, wenn das Textfeld die Filterbedingung erfüllt.

### Beispiel

#### Eingabeereignis
```json
{
  "message": "Temperature warning: 25.5°C",
  "timestamp": 1586380104915
}
```

#### Konfiguration
* Textfeld: `message`
* Operation: `CONTAINS`
* Schlüsselwort: `warning`

#### Ausgabeereignis
```json
{
  "message": "Temperature warning: 25.5°C",
  "timestamp": 1586380104915
}
```

## Anwendungsfälle

1. **Ereignisweiterleitung**
   * Weiterleitung von Ereignissen basierend auf Textinhalt
   * Filterung von Log-Nachrichten
   * Verarbeitung spezifischer Fehlermeldungen
   * Behandlung verschiedener Ereignistypen

2. **Inhaltsfilterung**
   * Filterung textbasierter Alarme
   * Verarbeitung spezifischer Schlüsselwörter
   * Extraktion relevanter Nachrichten
   * Filterung von Benachrichtigungen

3. **Datenvalidierung**
   * Validierung von Textinhalten
   * Sicherstellung erforderlicher Textmuster
   * Filterung ungültiger Nachrichten
   * Durchsetzung von Textstandards

## Hinweise

* Die Textübereinstimmung berücksichtigt Groß-/Kleinschreibung
* Der Prozessor behält die ursprüngliche Ereignisstruktur bei
* Es werden keine Texttransformationen durchgeführt
* Ereignisse, die nicht dem Filter entsprechen, werden verworfen
* Der Filter arbeitet auf dem exakten Textfeldwert 