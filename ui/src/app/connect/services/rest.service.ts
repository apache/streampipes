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

import { Injectable, inject } from '@angular/core';

import { HttpClient, HttpContext } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { UnitDescription } from '../model/UnitDescription';
import {
    AdapterDescription,
    EventSchema,
    PlatformServicesCommons,
    SampleData,
    SpDataStream,
} from '@streampipes/platform-services';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';

@Injectable({ providedIn: 'root' })
export class RestService {
    private http = inject(HttpClient);
    private platformServicesCommons = inject(PlatformServicesCommons);

    get connectPath() {
        return this.platformServicesCommons.apiBasePath + '/connect';
    }

    getEventSchema(adapter: AdapterDescription): Observable<EventSchema> {
        return this.http
            .post(`${this.connectPath}/master/guess/schema`, adapter, {
                context: new HttpContext().set(NGX_LOADING_BAR_IGNORED, true),
            })
            .pipe(
                map(response => {
                    return EventSchema.fromData(response as EventSchema);
                }),
            );
    }

    getSampleEvents(adapter: AdapterDescription): Observable<SampleData> {
        return this.http
            .post(`${this.connectPath}/master/guess/sample`, adapter, {
                context: new HttpContext().set(NGX_LOADING_BAR_IGNORED, true),
            })
            .pipe(
                map(response => {
                    return SampleData.fromData(response as SampleData);
                }),
            );
    }

    sampleTransform(
        adapter: AdapterDescription,
    ): Observable<AdapterDescription> {
        return this.http
            .post(
                `${this.connectPath}/master/guess/sample/transform`,
                adapter,
                {
                    context: new HttpContext().set(
                        NGX_LOADING_BAR_IGNORED,
                        true,
                    ),
                },
            )
            .pipe(
                map(response => {
                    return AdapterDescription.fromData(
                        response as AdapterDescription,
                    );
                }),
            );
    }

    getAdapterEventPreview(
        adapterDescription: AdapterDescription,
    ): Observable<Record<string, any>> {
        return this.http
            .post(
                `${this.connectPath}/master/guess/schema/preview`,
                adapterDescription,
            )
            .pipe(map(response => response as Record<string, any>));
    }

    getSourceDetails(sourceElementId): Observable<SpDataStream> {
        return this.http
            .get(
                `${
                    this.platformServicesCommons.apiBasePath
                }/streams/${encodeURIComponent(sourceElementId)}`,
            )
            .pipe(
                map(response => {
                    return SpDataStream.fromData(response as SpDataStream);
                }),
            );
    }

    getFittingUnits(
        unitDescription: UnitDescription,
    ): Observable<UnitDescription[]> {
        return this.http
            .post<
                UnitDescription[]
            >(`${this.connectPath}/master/unit`, unitDescription)
            .pipe(
                map(response => {
                    const descriptions = response as UnitDescription[];
                    return descriptions.filter(
                        entry => entry.resource !== unitDescription.resource,
                    );
                }),
            );
    }

    getAllUnitDescriptions(): Observable<UnitDescription[]> {
        return this.http.get(`${this.connectPath}/master/unit/units`).pipe(
            map(response => {
                const descriptions = response as UnitDescription[];
                return descriptions;
            }),
        );
    }
}
