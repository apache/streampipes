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

## Mathematischer Ausdrucksauswerter

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der Mathematische Ausdrucksauswerter-Prozessor ermöglicht es Ihnen, mathematische Berechnungen an numerischen Feldern mit Hilfe der Apache Commons JEXL-Bibliothek durchzuführen. Er:
* Wertet mathematische Ausdrücke aus
* Unterstützt komplexe Berechnungen
* Bietet Zugriff auf Java Math-Funktionen
* Erstellt neue Felder mit Berechnungsergebnissen

***

## Erforderliche Eingabe
Der Prozessor benötigt einen Eingabe-Ereignisstrom, der mindestens ein numerisches Feld für die Durchführung von Berechnungen enthält.

***

## Konfiguration

Weitere Informationen zur JEXL-Syntax finden Sie unter https://commons.apache.org/proper/commons-jexl/index.html.

### Zusätzliche Felder
Für jede Berechnung müssen Sie angeben:
* **Feldname**: Der Name des neuen Feldes, das das Berechnungsergebnis speichern wird
* **Ausdruck**: Der mit JEXL-Syntax auszuwertende mathematische Ausdruck

### Ausdruckssyntax
Der Prozessor unterstützt:
* Grundlegende arithmetische Operationen (+, -, *, /)
* Mathematische Funktionen aus `java.lang.Math`
* Verweise auf Eingabefeldwerte
* Komplexe Ausdrücke mit mehreren Operationen

## Ausgabe
Der Prozessor leitet das Eingabe-Ereignis mit zusätzlichen Feldern weiter, die die Berechnungsergebnisse enthalten.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 10.1,
  "flowrate": 2
}
```

#### Konfiguration
* Feldname: `result1`
* Ausdruck: `temperature+12`

* Feldname: `result2`
* Ausdruck: `temperature*flowrate`

#### Ausgabe-Ereignis
```json
{
  "temperature": 10.1,
  "flowrate": 2,
  "result1": 22.1,
  "result2": 20.2
}
```

## Anwendungsfälle

1. **Datentransformation**
   * Einheitenumrechnung
   * Berechnung abgeleiteter Metriken
   * Normalisierung von Werten
   * Skalierung von Messungen

2. **Statistische Analyse**
   * Berechnung von Durchschnitten
   * Berechnung von Standardabweichungen
   * Durchführung von Trendanalysen
   * Generierung statistischer Metriken

3. **Geschäftslogik**
   * Kostenberechnung
   * Berechnung von Leistungsmetriken
   * Auswertung von Geschäftsregeln
   * Generierung abgeleiteter Werte

## Hinweise

* Alle Eingabefelder sind als Variablen in Ausdrücken verfügbar
* Die `Math`-Klasse ist für erweiterte Berechnungen verfügbar
* Ergebnisse werden als doppelt genaue Fließkommazahlen gespeichert
* Ausdrücke werden für jedes eingehende Ereignis ausgewertet
* Ungültige Ausdrücke werden als Fehler protokolliert
* Der Prozessor behält alle Eingabefelder bei 