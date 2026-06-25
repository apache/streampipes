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

import { Component, OnInit } from '@angular/core';
import { SpAbstractDatasetDetailsDirective } from '../abstract-dataset-details.directive';
import { SpQueryResult } from '@streampipes/platform-services';
import { SpBasicNavTabsComponent } from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { SpSimpleMetricsComponent } from '../../../../core-ui/monitoring/simple-metrics/simple-metrics.component';
import { SpConfigurationRoutes } from '../../../../configuration/configuration.breadcrumb';
import { catchError, finalize, forkJoin, map, Observable, of } from 'rxjs';
import {
    DailyEventCount,
    DailyEventCountsChartComponent,
} from './daily-event-counts-chart/daily-event-counts-chart.component';

interface DayBucket extends DailyEventCount {
    timestamp: number;
    key: string;
}

@Component({
    selector: 'sp-dataset-details-metrics',
    templateUrl: './dataset-details-metrics.component.html',
    styleUrls: ['./dataset-details-metrics.component.scss'],
    imports: [
        SpBasicNavTabsComponent,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        FlexDirective,
        MatIconButton,
        MatTooltip,
        MatProgressSpinner,
        TranslatePipe,
        SpSimpleMetricsComponent,
        DailyEventCountsChartComponent,
    ],
})
export class DatasetDetailsMetricsComponent
    extends SpAbstractDatasetDetailsDirective
    implements OnInit
{
    totalEventCount = 0;
    lastEventTimestamp = 0;
    loadingMetrics = false;
    dailyEventCounts: DailyEventCount[] = [];

    ngOnInit(): void {
        super.onInit();
    }

    onDatasetLoaded(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: 'Datasets', link: ['datasets'] },
            { label: this.dataset.measureName },
            { label: 'Metrics' },
        ]);
        this.triggerUpdate();
    }

    triggerUpdate(): void {
        if (!this.dataset) {
            return;
        }

        this.loadingMetrics = true;
        const now = new Date();
        const dayBuckets = this.makeLastSevenDayBuckets(now);

        forkJoin({
            totalEventCount: this.loadTotalEventCount(),
            latestEventTimestamp: this.loadLatestEventTimestamp(),
            dailyEventCounts: this.loadDailyEventCounts(dayBuckets, now),
        })
            .pipe(
                finalize(() => {
                    this.loadingMetrics = false;
                }),
            )
            .subscribe(result => {
                this.totalEventCount = result.totalEventCount;
                this.lastEventTimestamp = result.latestEventTimestamp;
                this.dailyEventCounts = result.dailyEventCounts;
            });
    }

    private loadTotalEventCount(): Observable<number> {
        return this.datalakeRestService
            .getMeasurementEntryCount(this.dataset.elementId)
            .pipe(catchError(() => of(0)));
    }

    private loadLatestEventTimestamp(): Observable<number> {
        return this.datalakeRestService
            .getLatestMeasurementEvents([this.dataset.measureName])
            .pipe(
                map(result => result[this.dataset.measureName] ?? 0),
                catchError(() => of(0)),
            );
    }

    private loadDailyEventCounts(
        dayBuckets: DayBucket[],
        now: Date,
    ): Observable<DayBucket[]> {
        const firstRuntimeName = this.getRuntimeNames()[0];
        if (!firstRuntimeName) {
            return of(dayBuckets);
        }

        return this.datalakeRestService
            .getData(this.dataset.measureName, {
                endDate: now.getTime(),
                startDate: dayBuckets[0].timestamp,
                order: 'ASC',
                missingValueBehaviour: 'empty',
                columns: firstRuntimeName,
                aggregationFunction: 'COUNT',
                timeInterval: '1d',
                fill: 0,
            })
            .pipe(
                map(result => this.normalizeDailyCounts(result, dayBuckets)),
                catchError(() => of(dayBuckets)),
            );
    }

    private normalizeDailyCounts(
        result: SpQueryResult,
        dayBuckets: DayBucket[],
    ): DayBucket[] {
        const countsByDay = new Map(
            dayBuckets.map(bucket => [bucket.key, bucket.count]),
        );

        result?.allDataSeries?.forEach(series => {
            const headers = result.headers?.length
                ? result.headers
                : series.headers;
            const timestampIndex = this.getHeaderIndex(headers, 'time', 0);

            series.rows?.forEach(row => {
                const countIndex = this.getCountHeaderIndex(headers, row);
                const key = this.toUtcDayKey(new Date(row[timestampIndex]));
                if (countsByDay.has(key)) {
                    countsByDay.set(
                        key,
                        (countsByDay.get(key) ?? 0) +
                            this.toCount(row[countIndex]),
                    );
                }
            });
        });

        return dayBuckets.map(bucket => ({
            ...bucket,
            count: countsByDay.get(bucket.key) ?? 0,
        }));
    }

    private makeLastSevenDayBuckets(now: Date): DayBucket[] {
        const startTimestamp = Date.UTC(
            now.getUTCFullYear(),
            now.getUTCMonth(),
            now.getUTCDate() - 6,
        );

        return Array.from({ length: 7 }, (_value, index) => {
            const timestamp = startTimestamp + index * 24 * 60 * 60 * 1000;
            const date = new Date(timestamp);

            return {
                timestamp,
                key: this.toUtcDayKey(date),
                label: new Intl.DateTimeFormat(undefined, {
                    month: 'short',
                    day: 'numeric',
                    timeZone: 'UTC',
                }).format(date),
                count: 0,
            };
        });
    }

    private getRuntimeNames(): string[] {
        return (this.dataset.eventSchema?.eventProperties ?? []).map(
            property => property.runtimeName,
        );
    }

    private getHeaderIndex(
        headers: string[] | undefined,
        header: string,
        fallback: number,
    ): number {
        const index = headers?.indexOf(header) ?? -1;
        return index >= 0 ? index : fallback;
    }

    private getCountHeaderIndex(
        headers: string[] | undefined,
        row: unknown[],
    ): number {
        const countIndex =
            headers?.findIndex(
                header => header === 'count' || header.startsWith('count_'),
            ) ?? -1;
        return countIndex >= 0 ? countIndex : Math.min(row.length - 1, 1);
    }

    private toCount(value: unknown): number {
        const count = Number(value);
        return Number.isFinite(count) ? count : 0;
    }

    private toUtcDayKey(date: Date): string {
        const year = date.getUTCFullYear();
        const month = String(date.getUTCMonth() + 1).padStart(2, '0');
        const day = String(date.getUTCDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
}
