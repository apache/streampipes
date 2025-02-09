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
import { PlatformServicesCommons } from '@streampipes/platform-services';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NGX_LOADING_BAR_IGNORED } from '@ngx-loading-bar/http-client';

@Injectable({ providedIn: 'root' })
export class RestApi {
    constructor(
        private platformServicesCommons: PlatformServicesCommons,
        private $http: HttpClient,
    ) {}

    getServerUrl() {
        return this.platformServicesCommons.apiBasePath;
    }

    urlApiBase() {
        return this.platformServicesCommons.apiBasePath;
    }

    getAssetUrl(appId) {
        return this.getServerUrl() + '/pe/' + appId + '/assets';
    }

    configured(): Observable<any> {
        return this.$http.get(this.getServerUrl() + '/setup/configured', {
            context: new HttpContext().set(NGX_LOADING_BAR_IGNORED, true),
        });
    }

    getUnreadNotificationsCount(): Observable<any> {
        return this.$http.get(this.urlApiBase() + '/notifications/count', {
            context: new HttpContext().set(NGX_LOADING_BAR_IGNORED, true),
        });
    }

    updateCachedPipeline(rawPipelineModel: any) {
        return this.$http.post(
            this.urlApiBase() + '/pipeline-cache',
            rawPipelineModel,
        );
    }

    getVersionInfo() {
        return this.$http.get(this.getServerUrl() + '/info/versions');
    }

    getSystemInfo() {
        return this.$http.get(this.getServerUrl() + '/info/system');
    }
}
