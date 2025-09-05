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

## Wertänderung

<p align="center">
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung

Der Wertänderungs-Prozessor erkennt spezifische Übergänge in numerischen Werten. Er:
* Überwacht ein ausgewähltes numerisches Feld
* Erkennt, wenn sich der Wert von einem bestimmten Wert zu einem anderen ändert
* Fügt ein boolesches Flag hinzu, das die erkannte Änderung anzeigt
* Behält alle ursprünglichen Ereignisdaten bei
* Funktioniert mit jedem numerischen Feldtyp

***

## Erforderliche Eingabe

Der Prozessor benötigt einen Eingabe-Ereignisstrom, der mindestens ein numerisches Feld zur Überwachung spezifischer Wertübergänge enthält.

***

## Konfiguration

### Zu überwachende Eigenschaft

Wähle das numerische Feld aus dem Eingabe-Ereignis aus, das auf Wertänderungen überwacht werden soll.

### Von Wert

Gib den Ausgangswert an, der die Änderungserkennung auslösen soll. Der Prozessor sucht nach Übergängen von diesem Wert.

### Zu Wert

Gib den Zielwert an, der die Änderungserkennung abschließen soll. Der Prozessor sucht nach Übergängen zu diesem Wert.

## Ausgabe

Der Prozessor leitet das Eingabe-Ereignis mit einem zusätzlichen booleschen Feld namens `isChanged` weiter, das anzeigt, ob der spezifizierte Wertübergang erkannt wurde.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 25.5,
  "timestamp": 1586380105115
}
```

```json
{
  "temperature": 26.0,
  "timestamp": 1586380105116
}
```

#### Konfiguration
* Zu überwachende Eigenschaft: `temperature`
* Von Wert: `25.5`
* Zu Wert: `26.0`

#### Ausgabe-Ereignis
```json
{
  "temperature": 26.0,
  "timestamp": 1586380105116,
  "isChanged": true
}
```

## Anwendungsfälle

1. **Zustandsübergangs-Erkennung**
   * Überwachung von Systemzuständen
   * Erkennung von Modusänderungen
   * Verfolgung von Statusübergängen
   * Identifizierung von Phasenänderungen

2. **Schwellenwert-Überwachung**
   * Erkennung des Überschreitens spezifischer Schwellenwerte
   * Überwachung von Wertebereichen
   * Verfolgung von Grenzbedingungen
   * Identifizierung kritischer Übergänge

3. **Prozesssteuerung**
   * Überwachung von Steuerungssystemzuständen
   * Erkennung von Prozessübergängen
   * Verfolgung von Betriebsmodi
   * Identifizierung von Zustandsänderungen

## Hinweise

* Der Prozessor erkennt nur exakte Übereinstimmungen für die spezifizierten Werte
* Die Änderungserkennung ist sequentiell (muss von "Von Wert" zu "Zu Wert" gehen)
* Die ursprüngliche Ereignisstruktur wird beibehalten
* Das boolesche Flag wird jedem Ereignis hinzugefügt
* Der Prozessor behält den Zustand zwischen Ereignissen bei
* Werte werden mit exakter Gleichheit verglichen 