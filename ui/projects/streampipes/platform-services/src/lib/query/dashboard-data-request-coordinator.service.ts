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
import { Observable, ReplaySubject, Subject, of } from 'rxjs';
import { bufferTime, filter } from 'rxjs/operators';
import { DatasetRestService } from '../apis/dataset-rest.service';
import { DashboardKioskRestService } from '../apis/dashboard-kiosk.service';
import { DatasetQueryParameters } from '../model/dataset/DatasetQueryParameters';
import { SpQueryResult } from '../model/gen/streampipes-model';

interface PendingDatasetRequest {
    queryParams: DatasetQueryParameters;
    result$: ReplaySubject<SpQueryResult>;
}

interface PendingKioskRequest extends PendingDatasetRequest {
    dashboardId: string;
    widgetId: string;
}

@Injectable({
    providedIn: 'root',
})
export class DashboardDataRequestCoordinatorService {
    private readonly batchWindowMs = 20;

    private datasetRestService = inject(DatasetRestService);
    private dashboardKioskRestService = inject(DashboardKioskRestService);

    private datasetQueue$ = new Subject<PendingDatasetRequest>();
    private kioskQueue$ = new Subject<PendingKioskRequest>();

    constructor() {
        this.datasetQueue$
            .pipe(
                bufferTime(this.batchWindowMs),
                filter(requests => requests.length > 0),
            )
            .subscribe(requests => this.executeDatasetBatch(requests));

        this.kioskQueue$
            .pipe(
                bufferTime(this.batchWindowMs),
                filter(requests => requests.length > 0),
            )
            .subscribe(requests => this.executeKioskBatches(requests));
    }

    queueDatasetQuery(
        queryParams: DatasetQueryParameters,
    ): Observable<SpQueryResult> {
        if (queryParams.columns === '') {
            return of(this.makeEmptyQueryResult());
        }

        const result$ = new ReplaySubject<SpQueryResult>(1);
        this.datasetQueue$.next({ queryParams, result$ });
        return result$.asObservable();
    }

    queueKioskQuery(
        dashboardId: string,
        widgetId: string,
        queryParams: DatasetQueryParameters,
    ): Observable<SpQueryResult> {
        if (queryParams.columns === '') {
            return of(this.makeEmptyQueryResult());
        }

        const result$ = new ReplaySubject<SpQueryResult>(1);
        this.kioskQueue$.next({
            dashboardId,
            widgetId,
            queryParams,
            result$,
        });
        return result$.asObservable();
    }

    private executeDatasetBatch(requests: PendingDatasetRequest[]): void {
        this.datasetRestService
            .performMultiQuery(requests.map(request => request.queryParams))
            .subscribe({
                next: results =>
                    this.completeRequestsWithResults(requests, results),
                error: error => this.errorRequests(requests, error),
            });
    }

    private executeKioskBatches(requests: PendingKioskRequest[]): void {
        const requestsByDashboard = new Map<string, PendingKioskRequest[]>();
        requests.forEach(request => {
            const dashboardRequests =
                requestsByDashboard.get(request.dashboardId) ?? [];
            dashboardRequests.push(request);
            requestsByDashboard.set(request.dashboardId, dashboardRequests);
        });

        requestsByDashboard.forEach((dashboardRequests, dashboardId) => {
            this.dashboardKioskRestService
                .performMultiQuery(
                    dashboardId,
                    dashboardRequests.map(request => ({
                        widgetId: request.widgetId,
                        queryParams: request.queryParams,
                    })),
                )
                .subscribe({
                    next: results =>
                        this.completeRequestsWithResults(
                            dashboardRequests,
                            results,
                        ),
                    error: error =>
                        this.errorRequests(dashboardRequests, error),
                });
        });
    }

    private completeRequestsWithResults(
        requests: PendingDatasetRequest[],
        results: SpQueryResult[],
    ): void {
        requests.forEach((request, index) => {
            request.result$.next(results[index]);
            request.result$.complete();
        });
    }

    private errorRequests(
        requests: PendingDatasetRequest[],
        error: unknown,
    ): void {
        requests.forEach(request => request.result$.error(error));
    }

    private makeEmptyQueryResult(): SpQueryResult {
        const emptyQueryResult = new SpQueryResult();
        emptyQueryResult.total = 0;
        return emptyQueryResult;
    }
}
