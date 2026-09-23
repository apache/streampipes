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

# Tennis-Spielstände

Dieser Adapter liest laufende Tennisspiele von [Live Tennis API](https://docs.livetennisapi.com/live-scores.html).
Er ruft alle 15 Minuten bis zu 200 Spiele ab. Abgeschlossene Spiele und einzelne Ballwechsel werden nicht abgerufen.

## Konfiguration

Tragen Sie einen eigenen [kostenlosen API-Schlüssel](https://livetennisapi.com/subscribe/free) ein.
Der kostenlose Tarif erlaubt 100 Anfragen pro Tag. Ein Abstand von 900 Sekunden ergibt höchstens 96 Anfragen pro Tag.
Vorschauen und Adapter mit demselben Schlüssel teilen den Cache und das Anfragebudget. Auch fehlgeschlagene Anfragen zählen.
HTTP-Weiterleitungen werden abgelehnt. Der Schlüssel wird nur im Header `X-API-Key` gesendet und niemals im Cache gespeichert.

SP_LIVE_TENNIS_CACHE_DIRECTORY legt das dauerhafte, beschreibbare Cache-Verzeichnis fest.
Ohne diese Einstellung wird streampipes-live-tennis im temporären JVM-Verzeichnis verwendet.
Bewahren Sie den Cache bei Neustarts auf. Dienste mit demselben Schlüssel müssen dieses Verzeichnis mit funktionierenden Dateisperren teilen.
Verwenden Sie sonst getrennte Schlüssel. Nutzen Sie diesen Schlüssel nicht gleichzeitig in anderen Anwendungen.
Löschen Sie den Cache nicht, um eine Abfrage zu erzwingen.
Ein beschädigter Cache verhindert weitere Anfragen. Stellen Sie ihn wieder her oder warten Sie vor dem Entfernen mindestens 15 Minuten.

Für eine Datenvorschau müssen gerade Spiele laufen. Eine leere Antwort bleibt zwischengespeichert und erzeugt keine Ereignisse.
Nach einem Anfragefehler meldet der Adapter einen Fehler bis zur nächsten erfolgreichen, zulässigen Anfrage.

## Ausgabe

Jedes Spiel erzeugt ein Ereignis pro Abfrage. timestamp enthält die Abrufzeit in Unix-Millisekunden und bleibt bei wiederverwendeten Antworten unverändert.
matchId, tournament, tour, player1 und player2 beschreiben das Spiel. Fehlende Tour-Werte bleiben leer.
sets, games und points enthalten Zeichenketten. Beispielsweise wird [[6,3],[4,4]] als "6-4, 3-4" ausgegeben.
Ein fehlender Spielstand hat scoreAvailable=false und leere Werte. Ein fehlender Wert innerhalb eines Paars erscheint als "?".
server ist "1", "2" oder leer. isTiebreak übernimmt die Angabe der API.
Bei einem Match-Tiebreak enthält das letzte games-Paar Punkte. Daraus darf kein normaler Spielstand oder Sieger abgeleitet werden.
Bei mehr als 200 gleichzeitigen Spielen ist die Momentaufnahme unvollständig. Weitere Seiten werden nicht abgerufen.
