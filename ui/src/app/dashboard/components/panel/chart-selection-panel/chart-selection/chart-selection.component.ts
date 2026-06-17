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
    DestroyRef,
    EventEmitter,
    inject,
    OnInit,
    Output,
} from '@angular/core';
import { ChartService, ChartSummaryDto } from '@streampipes/platform-services';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs';
import { ChartSelectionItem } from './chart-selection.model';

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
        ReactiveFormsModule,
    ],
})
export class ChartSelectionComponent implements OnInit {
    private dataViewService = inject(ChartService);
    private authService = inject(AuthService);
    private chartRegistryService = inject(ChartRegistry);
    private chartRoutingService = inject(ChartRoutingService);
    private cdr = inject(ChangeDetectorRef);
    private destroyRef = inject(DestroyRef);

    @Output()
    addChartEmitter: EventEmitter<string> = new EventEmitter();

    charts: ChartSummaryDto[] = [];
    chartItems: ChartSelectionItem[] = [];
    filteredChartItems: ChartSelectionItem[] = [];
    searchTerm = '';
    searchControl = new FormControl('', { nonNullable: true });
    isRefreshing = false;
    hasActiveSearch = false;
    readonly chartItemSize = 132;

    hasChartWritePrivileges: boolean = false;

    ngOnInit(): void {
        this.hasChartWritePrivileges = this.authService.hasRole(
            UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
        );

        this.refreshCharts();

        this.searchControl.valueChanges
            .pipe(
                debounceTime(150),
                distinctUntilChanged(),
                takeUntilDestroyed(this.destroyRef),
            )
            .subscribe(value => {
                this.setSearchTerm(value);
            });
    }

    navigateToDataViewCreation(): void {
        this.chartRoutingService.navigateToCreateChart(true, undefined, true);
    }

    refreshCharts(): void {
        this.isRefreshing = true;
        this.cdr.markForCheck();
        this.dataViewService
            .getChartSummary()
            .pipe(
                finalize(() => {
                    this.isRefreshing = false;
                    this.cdr.markForCheck();
                }),
            )
            .subscribe({
                next: chartSummary => {
                    this.charts = [...chartSummary.resources].sort((a, b) =>
                        a.name.localeCompare(b.name),
                    );
                    this.chartItems = this.charts.map(chart =>
                        this.toChartSelectionItem(chart),
                    );
                    this.applySearch();
                    this.cdr.markForCheck();
                },
                error: () => {
                    this.charts = [];
                    this.chartItems = [];
                    this.filteredChartItems = [];
                },
            });
    }

    clearSearch(): void {
        this.searchControl.setValue('', { emitEvent: false });
        this.setSearchTerm('');
    }

    trackByChartId(index: number, item: ChartSelectionItem): string {
        return item.chart.elementId;
    }

    private setSearchTerm(value: string): void {
        this.searchTerm = value;
        this.hasActiveSearch = this.searchTerm.trim().length > 0;
        this.applySearch();
        this.cdr.markForCheck();
    }

    private toChartSelectionItem(chart: ChartSummaryDto): ChartSelectionItem {
        const template = this.chartRegistryService.getRegisteredChartSummary(
            chart.widgetType,
        );
        const widgetTypeLabel = template?.label ?? chart.widgetType;

        return {
            chart,
            widgetTypeLabel,
            widgetTypeIcon: template?.icon ?? 'insert_chart',
            dataCyId: `add-data-view-btn-${chart.name.replaceAll(' ', '')}`,
            searchText: [
                chart.name,
                chart.datasetName,
                chart.widgetType,
                widgetTypeLabel,
            ]
                .filter((value): value is string => !!value)
                .join(' ')
                .toLowerCase(),
        };
    }

    private applySearch(): void {
        const query = this.searchTerm.trim().toLowerCase();
        if (!query) {
            this.filteredChartItems = this.chartItems;
            return;
        }

        this.filteredChartItems = this.chartItems.filter(item =>
            item.searchText.includes(query),
        );
    }
}
