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

import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AdapterHealthStatus } from '../model/adapter-health-status.model';

@Injectable({ providedIn: 'root' })
export class AdapterHealthService {
    private http = inject(HttpClient);
    private readonly basePath = '/streampipes-backend/api/v2/adapter-health';

    getAllHealthStatuses(): Observable<Map<string, AdapterHealthStatus>> {
        return this.http.get<AdapterHealthStatus[]>(this.basePath).pipe(
            map(statuses =>
                statuses.reduce(
                    (mapByAdapterId, status) =>
                        mapByAdapterId.set(status.adapterId, status),
                    new Map<string, AdapterHealthStatus>(),
                ),
            ),
            catchError(() => of(new Map<string, AdapterHealthStatus>())),
        );
    }

    triggerHealthCheck(adapterId: string): Observable<void> {
        return this.http.post<void>(
            `${this.basePath}/${adapterId}/trigger`,
            {},
        );
    }
}
