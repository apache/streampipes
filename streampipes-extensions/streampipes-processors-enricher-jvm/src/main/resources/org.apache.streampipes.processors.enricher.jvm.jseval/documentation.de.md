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

## JavaScript-Auswertung

<p align="center"> 
    <img src="icon.png" width="150px;" class="pe-image-documentation"/>
</p>

***

## Beschreibung
Der JavaScript-Auswertung-Prozessor ermöglicht es Ihnen, benutzerdefinierte JavaScript-Funktionen zum Transformieren und Anreichern von Ereignissen zu schreiben. Er:
* Führt benutzerdefinierten JavaScript-Code aus
* Bietet vollen Zugriff auf Eingabe-Ereignisdaten
* Unterstützt komplexe Datentransformationen
* Ermöglicht dynamische Feld-Erstellung und -Modifikation

***

## Erforderliche Eingabe
Der Prozessor funktioniert mit jedem Eingabe-Ereignisstrom. Alle Felder aus dem Eingabe-Ereignis sind als Eigenschaften in der JavaScript-Funktion verfügbar.

***

## Konfiguration

### JavaScript-Funktion
Sie müssen eine JavaScript-Funktion bereitstellen, die das Eingabe-Ereignis verarbeitet und ein neues Ereignisobjekt zurückgibt. Die Funktion muss:
* Den Namen `process` haben
* Einen einzelnen Parameter akzeptieren, der das Eingabe-Ereignis enthält
* Eine Map/ein Objekt mit den Ausgabefeldern zurückgeben

Beispiel für die Funktionsstruktur:
```javascript
    function process(event) {
        // Verarbeitung hier durchführen.
        // Eine Map mit Feldern zurückgeben, die dem definierten Ausgabeschema entsprechen.
        return {id: event.id, tempInCelsius: (event.tempInKelvin - 273.15)};
    }
```

Das definierte Ausgabeschema muss mit dem Ereignis übereinstimmen, das von der JavaScript-Funktion zurückgegeben wird.

## Ausgabe
Der Prozessor leitet ein neues Ereignis weiter, das die von Ihrer JavaScript-Funktion zurückgegebenen Felder enthält.

### Beispiel

#### Eingabe-Ereignis
```json
{
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1586380105115
}
```

#### Konfiguration
```javascript
function process(event) {
    // Temperatur von Celsius in Fahrenheit umrechnen
    const tempF = (event.temperature * 9/5) + 32;
    
    // Hitzeindex berechnen
    const heatIndex = calculateHeatIndex(tempF, event.humidity);
    
    // Neues Ereignis mit transformierten Daten zurückgeben
    return {
        temperature_celsius: event.temperature,
        temperature_fahrenheit: tempF,
        humidity: event.humidity,
        heat_index: heatIndex,
        timestamp: event.timestamp
    };
}

function calculateHeatIndex(temp, humidity) {
    // Vereinfachte Hitzeindex-Berechnung
    return temp + (humidity * 0.1);
}
```

#### Ausgabe-Ereignis
```json
{
  "temperature_celsius": 25.5,
  "temperature_fahrenheit": 77.9,
  "humidity": 60,
  "heat_index": 83.9,
  "timestamp": 1586380105115
}
```

## Anwendungsfälle

1. **Datentransformation**
   * Einheitenumrechnungen
   * Datenormalisierung
   * Komplexe Berechnungen
   * Feldrestrukturierung

2. **Datenanreicherung**
   * Hinzufügen abgeleiteter Felder
   * Berechnung von Statistiken
   * Kombinieren mehrerer Felder
   * Erstellen berechneter Metriken

3. **Benutzerdefinierte Logik**
   * Implementierung von Geschäftsregeln
   * Bedingte Transformationen
   * Datenvalidierung
   * Benutzerdefinierte Algorithmen

## Hinweise

* Die JavaScript-Funktion läuft in einer GraalVM JavaScript-Umgebung
* Alle Eingabefelder sind als Eigenschaften des Ereignisobjekts zugänglich
* Die Funktion muss ein gültiges JavaScript-Objekt zurückgeben
* Fehlerbehandlung sollte im JavaScript-Code implementiert werden
* Komplexe JavaScript-Operationen werden unterstützt
* Die Funktion wird für jedes eingehende Ereignis ausgeführt, aber der Zustand kann beibehalten werden 