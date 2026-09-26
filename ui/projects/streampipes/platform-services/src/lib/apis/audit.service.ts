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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PlatformServicesCommons } from './commons.service';
import {
    AuditEntry,
    AuditEntryDetails,
    AuditPage,
    AuditQuery,
    AuditStatus,
} from '../model/audit/audit.model';

@Injectable({ providedIn: 'root' })
export class AuditService {
    private http = inject(HttpClient);
    private commons = inject(PlatformServicesCommons);

    getStatus(): Observable<AuditStatus> {
        return this.http.get<AuditStatus>(`${this.baseUrl}/status`);
    }

    getEventTypes(): Observable<string[]> {
        return this.http.get<string[]>(`${this.baseUrl}/event-types`);
    }

    getEvents(query: AuditQuery): Observable<AuditPage> {
        let params = new HttpParams();
        Object.entries(query).forEach(([key, value]) => {
            if (value !== undefined && value !== '') {
                params = params.set(key, String(value));
            }
        });
        return this.http.get<AuditPage>(`${this.baseUrl}/events`, { params });
    }

    getDetails(event: AuditEntry): Observable<AuditEntryDetails> {
        return this.http.get<AuditEntryDetails>(
            `${this.baseUrl}/events/${encodeURIComponent(event.eventId)}`,
            {
                params: new HttpParams().set('locator', event.locator),
            },
        );
    }

    private get baseUrl(): string {
        return `${this.commons.apiBasePath}/admin/audit`;
    }
}
