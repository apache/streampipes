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

## Zeitstempel-Extraktor

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Zeitstempel-Extraktor-Prozessor zerlegt einen Zeitstempel in seine einzelnen Zeitkomponenten. Er unterstützt:
* Jahr-Extraktion
* Monat-Extraktion
* Tag-Extraktion
* Stunde-Extraktion
* Minute-Extraktion
* Sekunde-Extraktion
* Wochentag-Extraktion
* Benutzerdefinierte Feldauswahl
* Komponentenisolierung

Dieser Prozessor ist essentiell für:
* Zeitanalyse
* Datumsverarbeitung
* Zeitkomponenten-Extraktion
* Temporale Analyse
* Zeitbasierte Gruppierung
* Zeitreihenanalyse

***

## Erforderliche Eingabe

Der Prozessor benötigt einen Datenstrom, der mindestens ein Zeitstempelfeld enthält, aus dem Komponenten extrahiert werden sollen.

***

## Konfiguration

### Zeitstempelfeld

Wähle das Feld aus, das den Zeitstempel enthält, aus dem Komponenten extrahiert werden sollen. Dies sollte ein gültiger Zeitstempelwert sein.

### Zu extrahierende Felder

Wähle aus, welche Zeitkomponenten extrahiert werden sollen:
* Jahr (numerisch)
* Monat (numerisch, 1-12)
* Tag (numerisch, 1-31)
* Stunde (numerisch, 0-23)
* Minute (numerisch, 0-59)
* Sekunde (numerisch, 0-59)
* Wochentag (String: Montag-Sonntag)

## Ausgabe

Der Prozessor erstellt eine neue Nachricht, die enthält:
* Alle ursprünglichen Felder aus der Eingabe-Nachricht
* Neue Felder für jede extrahierte Zeitkomponente mit dem Präfix "timestamp"
* Originaler Zeitstempel wird beibehalten

### Beispiel

#### Eingabe-Nachricht
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "value": 23.5
}
```

#### Konfiguration
* Zeitstempelfeld: timestamp
* Zu extrahierende Felder: Jahr, Monat, Tag, Stunde, Minute, Wochentag

#### Ausgabe-Nachricht
```json
{
  "deviceId": "sensor01",
  "timestamp": 1586380104915,
  "value": 23.5,
  "timestampYear": 2020,
  "timestampMonth": 4,
  "timestampDay": 8,
  "timestampHour": 15,
  "timestampMinute": 35,
  "timestampWeekday": "Mittwoch"
}
```

## Anwendungsfälle

1. **Zeitanalyse**
   * Zeitkomponenten extrahieren
   * Muster analysieren
   * Nach Zeit gruppieren
   * Änderungen verfolgen
   * Zeitreihen erstellen

2. **Datenverarbeitung**
   * Zeitstempel verarbeiten
   * Komponenten extrahieren
   * Muster analysieren
   * Daten gruppieren
   * Metriken erstellen

3. **Berichterstattung**
   * Zeitberichte generieren
   * Komponenten extrahieren
   * Muster analysieren
   * Daten gruppieren
   * Zusammenfassungen erstellen

4. **Überwachung**
   * Zeitmuster überwachen
   * Komponenten extrahieren
   * Trends analysieren
   * Änderungen verfolgen
   * Warnungen erstellen

## Hinweise

* Zeitstempel muss gültig sein
* Komponenten sind optional
* Verarbeitung ist zustandslos
* Mehrere Komponenten werden unterstützt
* Extraktion erfolgt sofort
* Keine Verzögerung bei der Verarbeitung
* Originaler Zeitstempel wird beibehalten
* Komponenten sind standardisiert
* Wochentag wird als String zurückgegeben
* Monat ist 1-basiert (1-12)
* Stunde ist im 24-Stunden-Format (0-23) 