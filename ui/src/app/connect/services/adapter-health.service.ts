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
import { Observable, of, forkJoin } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AdapterHealthStatus } from '../model/adapter-health-status.model';
import { AdapterService } from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class AdapterHealthService {
    private http = inject(HttpClient);
    private adapterService = inject(AdapterService);

    getHealthStatus(
        endpointUrl: string,
        adapterId: string,
    ): Observable<AdapterHealthStatus | null> {
        return this.http
            .get<AdapterHealthStatus>(
                `${endpointUrl}/api/v1/adapter-health/${encodeURIComponent(adapterId)}`,
            )
            .pipe(catchError(() => of(null)));
    }

    getAllHealthStatuses(
        adapters: Array<{ elementId: string; selectedEndpointUrl: string }>,
    ): Observable<Map<string, AdapterHealthStatus>> {
        if (!adapters.length) {
            return of(new Map());
        }
        const grouped = adapters.reduce(
            (acc, a) => {
                const url = a.selectedEndpointUrl;
                if (url) {
                    (acc[url] = acc[url] || []).push(a.elementId);
                }
                return acc;
            },
            {} as Record<string, string[]>,
        );

        const requests = Object.entries(grouped).map(([url, ids]) =>
            this.http
                .get<AdapterHealthStatus[]>(`${url}/api/v1/adapter-health`)
                .pipe(
                    map(statuses =>
                        statuses.filter(s => ids.includes(s.adapterId)),
                    ),
                    catchError(() => of([] as AdapterHealthStatus[])),
                ),
        );

        return forkJoin(requests).pipe(
            map(results =>
                results
                    .flat()
                    .reduce(
                        (m, s) => m.set(s.adapterId, s),
                        new Map<string, AdapterHealthStatus>(),
                    ),
            ),
        );
    }
}
