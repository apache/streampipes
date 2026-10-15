/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export type FavoriteResourceType =
    'ASSET' | 'ADAPTER' | 'PIPELINE' | 'DASHBOARD' | 'CHART';

export interface FavoriteSummary {
    resourceType: FavoriteResourceType;
    resourceId: string;
    name: string | null;
    available: boolean;
}

@Injectable({ providedIn: 'root' })
export class FavoritesService {
    private readonly http = inject(HttpClient);
    private readonly url = '/streampipes-backend/api/v2/favorites';

    list(): Observable<FavoriteSummary[]> {
        return this.http.get<FavoriteSummary[]>(this.url);
    }

    add(
        resourceType: FavoriteResourceType,
        resourceId: string,
    ): Observable<FavoriteSummary> {
        return this.http.put<FavoriteSummary>(this.url, null, {
            params: { resourceType, resourceId },
        });
    }

    remove(
        resourceType: FavoriteResourceType,
        resourceId: string,
    ): Observable<void> {
        return this.http.delete<void>(this.url, {
            params: { resourceType, resourceId },
        });
    }
}
