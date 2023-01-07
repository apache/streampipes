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

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { DashboardConfiguration } from '../model/dashboard-configuration.model';
import { PlatformServicesCommons } from '@streampipes/platform-services';

@Injectable()
export class RestService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    getVisualizablePipelines(): Observable<any> {
        return this.http.get(
            '/visualizablepipeline/_all_docs?include_docs=true',
        );
    }

    getPipeline(pipelineId): Observable<any> {
        return this.http.get('/pipeline/' + pipelineId);
    }

    storeImage(file: File): Observable<any> {
        const data: FormData = new FormData();
        data.append('file_upload', file, file.name);
        return this.http.post(this.imagePath, data).pipe(
            map(res => {
                return res;
            }),
        );
    }

    deleteDashboard(dashboardId: string) {
        return this.http.delete(`${this.url}/${dashboardId}`);
    }

    storeDashboard(dashboardConfig: DashboardConfiguration) {
        return this.http.post(this.url, dashboardConfig);
    }

    updateDashboard(dashboardConfig: DashboardConfiguration) {
        return this.http.put(
            this.url + '/' + dashboardConfig.dashboardId,
            dashboardConfig,
        );
    }

    getDashboards(): Observable<DashboardConfiguration[]> {
        return this.http.get(this.url).pipe(
            map(response => {
                return response as DashboardConfiguration[];
            }),
        );
    }

    getImageUrl(imageName: string): string {
        return `${this.imagePath}/${imageName}`;
    }

    private get url() {
        return `${this.platformServicesCommons.apiBasePath}/asset-dashboards`;
    }

    private get imagePath() {
        return `${this.url}/images`;
    }
}
