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

import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    EventEmitter,
    inject,
    OnInit,
    Output,
} from '@angular/core';
import { ChartService, ChartSummaryDto } from '@streampipes/platform-services';
import { AuthService } from '../../../../../services/auth.service';
import { UserPrivilege } from '../../../../../core/auth/user-privilege.enum';
import { ChartRegistry } from '../../../../../chart-shared/registry/chart-registry.service';
import { ChartRoutingService } from '../../../../../chart-shared/services/chart-routing.service';
import {
    FlexDirective,
    FlexFillDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { ChartPreviewComponent } from './chart-preview/chart-preview.component';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import {
    MatFormField,
    MatPrefix,
    MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import {
    CdkFixedSizeVirtualScroll,
    CdkVirtualForOf,
    CdkVirtualScrollViewport,
} from '@angular/cdk/scrolling';

@Component({
    selector: 'sp-chart-selection',
    templateUrl: './chart-selection.component.html',
    styleUrls: ['./chart-selection.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexDirective,
        FlexFillDirective,
        LayoutGapDirective,
        LayoutDirective,
        ChartPreviewComponent,
        LayoutAlignDirective,
        MatButton,
        MatIconButton,
        MatIcon,
        MatFormField,
        MatPrefix,
        MatSuffix,
        MatInput,
        MatProgressSpinner,
        TranslatePipe,
        MatTooltip,
        CdkVirtualScrollViewport,
        CdkFixedSizeVirtualScroll,
        CdkVirtualForOf,
    ],
})
export class ChartSelectionComponent implements OnInit {
    private dataViewService = inject(ChartService);
    private authService = inject(AuthService);
    private chartRegistryService = inject(ChartRegistry);
    private chartRoutingService = inject(ChartRoutingService);
    private cdr = inject(ChangeDetectorRef);

    @Output()
    addChartEmitter: EventEmitter<string> = new EventEmitter();

    charts: ChartSummaryDto[] = [];
    filteredCharts: ChartSummaryDto[] = [];
    searchTerm = '';
    isRefreshing = false;
    readonly chartItemSize = 132;

    hasChartWritePrivileges: boolean = false;

    ngOnInit(): void {
        this.hasChartWritePrivileges = this.authService.hasRole(
            UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
        );

        this.refreshCharts();
    }

    navigateToDataViewCreation(): void {
        this.chartRoutingService.navigateToCreateChart(true, undefined, true);
    }

    refreshCharts(): void {
        this.isRefreshing = true;
        this.cdr.markForCheck();
        this.dataViewService.getChartSummary().subscribe({
            next: chartSummary => {
                this.charts = chartSummary.resources.sort((a, b) =>
                    a.name.localeCompare(b.name),
                );
                this.applySearch();
                this.cdr.markForCheck();
            },
            complete: () => {
                this.isRefreshing = false;
                this.cdr.markForCheck();
            },
            error: () => {
                this.isRefreshing = false;
                this.cdr.markForCheck();
            },
        });
    }

    onSearchTermChanged(value: string): void {
        this.searchTerm = value;
        this.applySearch();
        this.cdr.markForCheck();
    }

    clearSearch(): void {
        this.searchTerm = '';
        this.applySearch();
        this.cdr.markForCheck();
    }

    hasActiveSearch(): boolean {
        return this.searchTerm.trim().length > 0;
    }

    trackByChartId(index: number, chart: ChartSummaryDto): string {
        return chart.elementId;
    }

    private applySearch(): void {
        const query = this.searchTerm.trim().toLowerCase();
        if (!query) {
            this.filteredCharts = this.charts;
            return;
        }

        this.filteredCharts = this.charts.filter(chart =>
            [
                chart.name,
                chart.datasetName,
                chart.widgetType,
                this.chartRegistryService.getChartTemplate(chart.widgetType)
                    ?.label,
            ]
                .filter((value): value is string => !!value)
                .some(value => value.toLowerCase().includes(query)),
        );
    }
}
