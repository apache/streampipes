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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

import { Observable } from 'rxjs';
import { PlatformServicesCommons } from './commons.service';
import {
    AdapterDescription,
    AdapterDescriptionUnion,
    Message,
} from '../model/gen/streampipes-model';

@Injectable({ providedIn: 'root' })
export class AdapterService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    get connectPath() {
        return `${this.platformServicesCommons.apiBasePath}/connect`;
    }

    getAdapterDescriptions(): Observable<AdapterDescriptionUnion[]> {
        return this.requestAdapterDescriptions('/master/description/adapters');
    }

    getAdapters(): Observable<AdapterDescriptionUnion[]> {
        return this.requestAdapterDescriptions('/master/adapters');
    }

    getAdapter(id: string): Observable<AdapterDescriptionUnion> {
        return this.http
            .get(this.connectPath + `/master/adapters/${id}`)
            .pipe(
                map(response =>
                    AdapterDescription.fromDataUnion(response as any),
                ),
            );
    }

    deleteAdapterDescription(adapterId: string): Observable<any> {
        return this.http.delete(
            `${this.connectPath}/master/description/${adapterId}`,
        );
    }

    requestAdapterDescriptions(
        path: string,
    ): Observable<AdapterDescriptionUnion[]> {
        return this.http.get(this.connectPath + path).pipe(
            map(response => {
                return (response as any[]).map(p =>
                    AdapterDescription.fromDataUnion(p),
                );
            }),
        );
    }

    stopAdapter(adapter: AdapterDescriptionUnion): Observable<Message> {
        return this.http
            .post(this.adapterMasterUrl + adapter.elementId + '/stop', {})
            .pipe(map(response => Message.fromData(response as any)));
    }

    startAdapter(adapter: AdapterDescriptionUnion): Observable<Message> {
        return this.http
            .post(this.adapterMasterUrl + adapter.elementId + '/start', {})
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

    get adapterMasterUrl() {
        return `${this.connectPath}/master/adapters/`;
    }

    deleteAdapter(adapter: AdapterDescription): Observable<any> {
        return this.deleteRequest(adapter, '/master/adapters/');
    }

    getAdapterCategories(): Observable<any> {
        return this.http.get(`${this.baseUrl}/api/v2/categories/adapter`);
    }

    private deleteRequest(adapter: AdapterDescription, url: string) {
        return this.http.delete(this.connectPath + url + adapter.elementId);
    }

    getAssetUrl(appId) {
        return `${this.connectPath}/master/description/${appId}/assets`;
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }
}
