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

# Tennis score snapshots

This adapter imports live tennis match snapshots from [Live Tennis API](https://docs.livetennisapi.com/live-scores.html).
It requests up to 200 matches every 15 minutes. It does not provide point-by-point updates or completed matches.

## Configuration

Use a dedicated [free API key](https://livetennisapi.com/subscribe/free) in the API key field.
The free plan permits 100 requests per day. One request every 900 seconds uses at most 96 requests per day.
The interval is fixed in code. Sample previews and adapters with matching API keys share the request budget and cached response.
Failed requests also consume an interval. HTTP redirects are rejected, and credentials are sent only in the `X-API-Key` header.

The cache records each attempt before contacting the API. It contains a key fingerprint and match data, never the key itself.
Set SP_LIVE_TENNIS_CACHE_DIRECTORY to a persistent writable directory when running containers.
The default is streampipes-live-tennis under the JVM temporary directory.
Retain this directory across restarts. Services with matching API keys must share this directory and support file locking.
Use separate dedicated keys when services cannot share the cache.
Do not delete the cache to force refreshes or use the same key in another application.

Creating an adapter requires a sample while matches are live. An empty slate produces no events and also remains cached.
After a request failure, the adapter reports an error until the next allowed request succeeds.
An unreadable or corrupt cache stops requests. Restore the cache or wait at least 15 minutes before removing its damaged state file.

## Output

Each match produces one event per poll. The timestamp is the snapshot fetch time in Unix milliseconds, including when a cached snapshot is reused.
Match identity and names are in matchId, tournament, tour, player1 and player2.

The sets, games and points fields are score strings. For example, games arrays [[6,3],[4,4]] become "6-4, 3-4".
An absent score has scoreAvailable=false and empty score strings. A missing side within a score pair appears as "?".
Server is "1", "2" or an empty string. The isTiebreak flag reports the API value.
During a deciding match tiebreak, the last games pair contains tiebreak points. Do not interpret that pair as ordinary games or infer a winner.
Missing tour values are empty strings. The adapter does not follow pagination, so more than 200 concurrent matches produce a partial snapshot.
