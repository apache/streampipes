import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PlatformServicesCommons } from './commons.service';
import { Observable } from 'rxjs';

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

@Injectable({
    providedIn: 'root',
})
export class PipelineElementEndpointService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    add(elementUri, ispublic): Observable<any> {
        const payload = new HttpParams()
            .set('uri', elementUri)
            .set('publicElement', ispublic);
        return this.http.post(
            this.platformServicesCommons.apiBasePath + '/element',
            payload,
        );
    }

    update(elementUri): Observable<any> {
        return this.http.put(
            this.platformServicesCommons.apiBasePath +
                '/element/' +
                encodeURIComponent(elementUri),
            undefined,
        );
    }

    del(elementUri): Observable<any> {
        return this.http.delete(
            this.platformServicesCommons.apiBasePath +
                '/element/' +
                encodeURIComponent(elementUri),
        );
    }
}
