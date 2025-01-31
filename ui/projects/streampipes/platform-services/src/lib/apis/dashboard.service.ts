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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SharedDatalakeRestService } from './shared-dashboard.service';
import { Dashboard } from '../model/dashboard/dashboard.model';

@Injectable({
    providedIn: 'root',
})
export class DashboardService {
    constructor(
        private http: HttpClient,
        private sharedDatalakeRestService: SharedDatalakeRestService,
    ) {}

    getDashboards(): Observable<Dashboard[]> {
        return this.sharedDatalakeRestService.getDashboards(this.dashboardUrl);
    }

    getDashboard(dashboardId: string): Observable<Dashboard> {
        return this.http
            .get(`${this.dashboardUrl}/${dashboardId}`)
            .pipe(map(data => data as Dashboard));
    }

    updateDashboard(dashboard: Dashboard): Observable<Dashboard> {
        return this.sharedDatalakeRestService.updateDashboard(
            this.dashboardUrl,
            dashboard,
        );
    }

    deleteDashboard(dashboard: Dashboard): Observable<any> {
        return this.sharedDatalakeRestService.deleteDashboard(
            this.dashboardUrl,
            dashboard,
        );
    }

    saveDashboard(dashboard: Dashboard): Observable<any> {
        return this.sharedDatalakeRestService.saveDashboard(
            this.dashboardUrl,
            dashboard,
        );
    }

    private get baseUrl() {
        return '/streampipes-backend';
    }

    private get dashboardUrl() {
        return `${this.baseUrl}/api/v3/datalake/dashboard`;
    }
}
