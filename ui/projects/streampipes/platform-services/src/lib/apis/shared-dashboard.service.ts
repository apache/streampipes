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
import { Dashboard } from '../model/dashboard/dashboard.model';

@Injectable({
    providedIn: 'root',
})
export class SharedDatalakeRestService {
    constructor(private http: HttpClient) {}

    getDashboards(dashboardUrl: string): Observable<Dashboard[]> {
        return this.http.get(dashboardUrl).pipe(
            map(data => {
                return data as Dashboard[];
            }),
        );
    }

    updateDashboard(
        dashboardUrl: string,
        dashboard: Dashboard,
    ): Observable<Dashboard> {
        return this.http
            .put(dashboardUrl + '/' + dashboard._id, dashboard)
            .pipe(
                map(data => {
                    return data as Dashboard;
                }),
            );
    }

    deleteDashboard(
        dashboardUrl: string,
        dashboard: Dashboard,
    ): Observable<any> {
        return this.http.delete(dashboardUrl + '/' + dashboard._id);
    }

    saveDashboard(dashboardUrl: string, dashboard: Dashboard): Observable<any> {
        return this.http.post(dashboardUrl, dashboard);
    }
}
