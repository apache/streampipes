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
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class RestApi {
    encodeURIComponent: any;

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
            headers: { ignoreLoadingBar: '' },
        });
    }

    getUnreadNotificationsCount(): Observable<any> {
        return this.$http.get(this.urlApiBase() + '/notifications/count', {
            headers: { ignoreLoadingBar: '' },
        });
    }

    getDomainKnowledgeItems(query) {
        return this.$http.post(
            this.getServerUrl() + '/autocomplete/domain',
            query,
        );
    }

    getAllUnits() {
        return this.$http.get(this.getServerUrl() + '/units/instances');
    }

    getAllUnitTypes() {
        return this.$http.get(this.getServerUrl() + '/units/types');
    }

    getUnit(resource) {
        return this.$http.get(
            this.getServerUrl() +
                '/units/instances/' +
                encodeURIComponent(resource),
        );
    }

    getEpaCategories() {
        return this.$http.get(this.getServerUrl() + '/categories/epa');
    }

    getAdapterCategories() {
        return this.$http.get(this.getServerUrl() + '/categories/adapter');
    }

    getApplicationLinks() {
        return this.$http.get(this.getServerUrl() + '/applink');
    }

    getFileMetadata() {
        return this.$http.get(this.urlApiBase() + '/files');
    }

    getCachedPipeline() {
        return this.$http.get(this.urlApiBase() + '/pipeline-cache');
    }

    updateCachedPipeline(rawPipelineModel: any) {
        return this.$http.post(
            this.urlApiBase() + '/pipeline-cache',
            rawPipelineModel,
        );
    }

    removePipelineFromCache() {
        return this.$http.delete(this.urlApiBase() + '/pipeline-cache');
    }

    getVersionInfo() {
        return this.$http.get(this.getServerUrl() + '/info/versions');
    }

    getSystemInfo() {
        return this.$http.get(this.getServerUrl() + '/info/system');
    }
}
