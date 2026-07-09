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

import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { SpQueryResult } from '@streampipes/platform-services';
import {
    SpBasicHeaderTitleComponent,
    SpBasicNavTabsComponent,
    SpElementIdComponent,
} from '@streampipes/shared-ui';
import { SpAbstractDatasetDetailsDirective } from '../abstract-dataset-details.directive';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { catchError, finalize, of } from 'rxjs';
import { SpConfigurationRoutes } from '../../../../configuration/configuration.breadcrumb';
import { DatePipe } from '@angular/common';

type PreviewRow = Record<string, unknown>;

@Component({
    selector: 'sp-dataset-details-events',
    templateUrl: './dataset-details-events.component.html',
    styleUrls: ['./dataset-details-events.component.scss'],
    imports: [
        SpBasicNavTabsComponent,
        SpBasicHeaderTitleComponent,
        SpElementIdComponent,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        FlexDirective,
        MatButton,
        MatIcon,
        MatProgressSpinner,
        MatFormField,
        MatLabel,
        MatInput,
        MatIconButton,
        MatTooltip,
        FormsModule,
        TranslatePipe,
    ],
})
export class DatasetDetailsEventsComponent
    extends SpAbstractDatasetDetailsDirective
    implements OnInit
{
    private router = inject(Router);
    private datePipe = new DatePipe('en-US');

    eventLimit = 10;
    columns: string[] = [];
    rows: PreviewRow[] = [];
    totalRows = 0;
    loadingEvents = false;

    ngOnInit(): void {
        super.onInit();
    }

    onDatasetLoaded(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: 'Datasets', link: ['datasets'] },
            { label: this.dataset.measureName },
            { label: 'Latest events' },
        ]);
        this.loadLatestEvents();
    }

    loadLatestEvents(): void {
        if (!this.dataset) {
            return;
        }

        this.loadingEvents = true;
        this.datalakeRestService
            .getData(this.dataset.measureName, {
                endDate: new Date().getTime(),
                startDate: 0,
                limit: this.eventLimit,
                order: 'DESC',
                missingValueBehaviour: 'empty',
                columns: this.getRuntimeNames().toString(),
            })
            .pipe(
                catchError(() => {
                    return of(new SpQueryResult());
                }),
                finalize(() => {
                    this.loadingEvents = false;
                }),
            )
            .subscribe(result => this.applyPreviewResult(result));
    }

    onEventLimitChange(value: number): void {
        this.eventLimit = Math.min(Math.max(Number(value) || 1, 1), 1000);
        this.loadLatestEvents();
    }

    refreshLatestEvents(): void {
        this.onEventLimitChange(this.eventLimit);
    }

    navigateToCreateChart(): void {
        this.router.navigate(['chart', 'create'], {
            queryParams: {
                editMode: true,
                measureName: this.dataset.measureName,
            },
        });
    }

    formatPreviewValue(name: string, value: unknown): string {
        if (name === 'time' && this.isValidDateValue(value)) {
            return (
                this.datePipe.transform(
                    value as string | number | Date,
                    'yyyy-MM-dd HH:mm:ss.SSS',
                ) ?? this.stringify(value)
            );
        }

        return this.stringify(value);
    }

    stringify(value: unknown): string {
        if (value === null || value === undefined) {
            return '-';
        }

        return String(value);
    }

    isValidDateValue(value: unknown): boolean {
        if (value === null || value === undefined || value === '') {
            return false;
        }

        const date = new Date(value as string | number);
        return !Number.isNaN(date.getTime());
    }

    displayColumnName(column: string): string {
        return column;
    }

    trackColumn(_index: number, column: string): string {
        return column;
    }

    trackRow(index: number): number {
        return index;
    }

    private getRuntimeNames(): string[] {
        return (this.dataset.eventSchema?.eventProperties ?? []).map(
            property => property.runtimeName,
        );
    }

    private applyPreviewResult(result: SpQueryResult): void {
        this.columns = this.orderHeaderColumns(result?.headers ?? []);
        const rows = result?.allDataSeries?.[0]?.rows ?? [];
        this.totalRows = rows.length;
        this.rows = rows.map(row =>
            this.columns.reduce<PreviewRow>((previewRow, column) => {
                previewRow[column] = row[result.headers.indexOf(column)];
                return previewRow;
            }, {}),
        );
    }

    private orderHeaderColumns(columns: string[]): string[] {
        const timeColumns = columns.filter(column => column === 'time');
        const otherColumns = columns.filter(column => column !== 'time');
        return [...timeColumns, ...otherColumns];
    }
}
