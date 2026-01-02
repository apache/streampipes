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
import {
    ConnectTransformationScriptTemplate,
    PlatformServicesCommons,
} from '@streampipes/platform-services';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ConnectScriptTemplatesService {
    private http = inject(HttpClient);
    private platformServicesCommons = inject(PlatformServicesCommons);

    getAll(): Observable<ConnectTransformationScriptTemplate[]> {
        return this.http.get<ConnectTransformationScriptTemplate[]>(
            this.baseUrl,
        );
    }

    create(template: ConnectTransformationScriptTemplate): Observable<void> {
        return this.http.post<void>(this.baseUrl, template);
    }

    update(template: ConnectTransformationScriptTemplate): Observable<void> {
        return this.http.put<void>(
            `${this.baseUrl}/${template.elementId}`,
            template,
        );
    }

    delete(elementId: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${elementId}`);
    }

    get baseUrl(): string {
        return `${this.platformServicesCommons.apiBasePath}/connect/master/script-templates`;
    }
}
