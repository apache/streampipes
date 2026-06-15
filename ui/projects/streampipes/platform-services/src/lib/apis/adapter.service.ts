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
import { map } from 'rxjs/operators';

import { Observable } from 'rxjs';
import { PlatformServicesCommons } from './commons.service';
import {
    AdapterDescription,
    CompactAdapter,
    Message,
    PipelineUpdateInfo,
} from '../model/gen/streampipes-model';
import {
    AdapterSummaryDto,
    ResourceSummaryDto,
} from '../model/resource/resource-summary.model';

@Injectable({
    providedIn: 'root',
})
export class AdapterService {
    private http = inject(HttpClient);
    private platformServicesCommons = inject(PlatformServicesCommons);

    get connectPath() {
        return `${this.platformServicesCommons.apiBasePath}/connect`;
    }

    getAdapterDescriptions(): Observable<AdapterDescription[]> {
        return this.requestAdapterDescriptions('/master/description/adapters');
    }

    getAdapters(): Observable<AdapterDescription[]> {
        return this.requestAdapterDescriptions('/master/adapters');
    }

    getAdapterSummary(): Observable<ResourceSummaryDto<AdapterSummaryDto>> {
        return this.http.get<ResourceSummaryDto<AdapterSummaryDto>>(
            `${this.connectPath}/master/adapters/summary`,
        );
    }

    getAdapter(id: string): Observable<AdapterDescription> {
        return this.http
            .get(this.connectPath + `/master/adapters/${id}`)
            .pipe(
                map(response => AdapterDescription.fromData(response as any)),
            );
    }

    convertToCompactAdapter(
        adapterDescription: AdapterDescription,
    ): Observable<CompactAdapter> {
        return this.http.post<CompactAdapter>(
            this.connectPath + `/master/adapters/compact`,
            adapterDescription,
        );
    }

    requestAdapterDescriptions(path: string): Observable<AdapterDescription[]> {
        return this.http.get(this.connectPath + path).pipe(
            map(response => {
                return (response as any[]).map(p =>
                    AdapterDescription.fromData(p),
                );
            }),
        );
    }

    stopAdapter(
        adapter: AdapterDescription,
        forceStop = false,
    ): Observable<Message> {
        return this.stopAdapterByElementId(adapter.elementId, forceStop);
    }

    stopAdapterByElementId(
        elementId: string,
        forceStop = false,
    ): Observable<Message> {
        return this.http
            .post(
                this.adapterMasterUrl + elementId + '/stop',
                {},
                { params: { forceStop } },
            )
            .pipe(map(response => Message.fromData(response as any)));
    }

    startAdapter(adapter: AdapterDescription): Observable<Message> {
        return this.startAdapterByElementId(adapter.elementId);
    }

    startAdapterByElementId(elementId: string): Observable<Message> {
        return this.http
            .post(this.adapterMasterUrl + elementId + '/start', {})
            .pipe(map(response => Message.fromData(response as any)));
    }

    addAdapter(adapter: AdapterDescription): Observable<Message> {
        return this.http
            .post(`${this.connectPath}/master/adapters`, adapter)
            .pipe(map(response => Message.fromData(response as any)));
    }

    updateAdapter(adapter: AdapterDescription): Observable<Message> {
        return this.http
            .put(`${this.connectPath}/master/adapters`, adapter)
            .pipe(map(response => Message.fromData(response as any)));
    }

    performPipelineMigrationPreflight(
        adapter: AdapterDescription,
    ): Observable<PipelineUpdateInfo[]> {
        return this.http
            .put(
                `${this.connectPath}/master/adapters/pipeline-migration-preflight`,
                adapter,
            )
            .pipe(
                map(response => {
                    return (response as any[]).map(p =>
                        PipelineUpdateInfo.fromData(p),
                    );
                }),
            );
    }

    get adapterMasterUrl() {
        return `${this.connectPath}/master/adapters/`;
    }

    deleteAdapter(
        adapter: AdapterDescription,
        deleteAssociatedPipelines: boolean,
    ): Observable<any> {
        return this.deleteAdapterById(
            adapter.elementId,
            deleteAssociatedPipelines,
        );
    }

    deleteAdapterById(
        elementId: string,
        deleteAssociatedPipelines: boolean,
    ): Observable<any> {
        return this.deleteRequest(
            elementId,
            deleteAssociatedPipelines,
            'master/adapters',
        );
    }

    private deleteRequest(
        elementId: string,
        deleteAssociatedPipelines: boolean,
        url: string,
    ) {
        const queryString = deleteAssociatedPipelines
            ? '?deleteAssociatedPipelines=true'
            : '';
        return this.http.delete(
            `${this.connectPath}/${url}/${elementId}${queryString}`,
        );
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }
}
