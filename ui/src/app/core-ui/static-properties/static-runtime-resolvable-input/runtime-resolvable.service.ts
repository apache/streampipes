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
import {
    RuntimeOptionsRequest,
    RuntimeOptionsResponse,
    PlatformServicesCommons,
} from '@streampipes/platform-services';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class RuntimeResolvableService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    fetchRemoteOptionsForAdapter(
        resolvableOptionsParameterRequest: RuntimeOptionsRequest,
        adapterId: string,
    ): Observable<RuntimeOptionsResponse> {
        const url: string =
            '/streampipes-backend/api/v2/connect/' +
            'master/resolvable/' +
            encodeURIComponent(adapterId) +
            '/configurations';
        return this.fetchRemoteOptions(url, resolvableOptionsParameterRequest);
    }

    fetchRemoteOptionsForPipelineElement(
        resolvableOptionsParameterRequest: RuntimeOptionsRequest,
    ): Observable<RuntimeOptionsResponse> {
        const url: string =
            this.platformServicesCommons.apiBasePath + '/pe/options';
        return this.fetchRemoteOptions(url, resolvableOptionsParameterRequest);
    }

    fetchRemoteOptions(url, resolvableOptionsParameterRequest) {
        resolvableOptionsParameterRequest['@class'] =
            'org.apache.streampipes.model.runtime.RuntimeOptionsRequest';
        return this.http.post(url, resolvableOptionsParameterRequest).pipe(
            map(response => {
                return RuntimeOptionsResponse.fromData(
                    response as RuntimeOptionsResponse,
                );
            }),
        );
    }
}
