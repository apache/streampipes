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

import { Observable } from 'rxjs';
import { HttpClient, HttpContext, HttpEvent } from '@angular/common/http';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';
import { inject, Injectable } from '@angular/core';
import { PlatformServicesCommons } from './commons.service';
import { SpDataStream } from '../model/gen/streampipes-model';

@Injectable({
    providedIn: 'root',
})
export class PipelineElementRuntimeInfoService {
    private http = inject(HttpClient);
    private platformServicesCommons = inject(PlatformServicesCommons);

    getRuntimeInfo(
        sourceDescription: SpDataStream,
    ): Observable<HttpEvent<string>> {
        return this.http.post(
            `${this.platformServicesCommons.apiBasePath}/pipeline-element/runtime`,
            sourceDescription,
            {
                responseType: 'text',
                observe: 'events',
                reportProgress: true,
                context: new HttpContext().set(NGX_LOADING_BAR_IGNORED, true),
            },
        );
    }
}
