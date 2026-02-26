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

import { Component, Input, OnInit } from '@angular/core';
import {
    DatalakeRestService,
    FilterExpressionGroup,
    SelectedFilter,
    SourceConfig,
} from '@streampipes/platform-services';
import { ChartConfigurationService } from '../../../../../../chart-shared/services/chart-configuration.service';
import { ChartFieldProviderService } from '../../../../../../chart-shared/services/chart-field-provider.service';
import {
    DialogService,
    PanelType,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { FilterSelectionPanelRowComponent } from './filter-selection-panel-row/filter-selection-panel-row.component';
import { TranslatePipe } from '@ngx-translate/core';
import {
    AdvancedFilterDialogComponent,
    AdvancedFilterDialogResult,
} from './advanced-filter-dialog/advanced-filter-dialog.component';
import { FilterExpressionPreviewService } from './filter-expression-preview.service';

@Component({
    selector: 'sp-filter-selection-panel',
    templateUrl: './filter-selection-panel.component.html',
    imports: [
        SplitSectionComponent,
        LayoutAlignDirective,
        FlexDirective,
        MatButton,
        LayoutDirective,
        FilterSelectionPanelRowComponent,
        TranslatePipe,
        SpAlertBannerComponent,
    ],
})
export class FilterSelectionPanelComponent implements OnInit {
    @Input() sourceConfig: SourceConfig;

    tagValues: Map<string, string[]> = new Map<string, string[]>();

    constructor(
        private widgetConfigService: ChartConfigurationService,
        private fieldProviderService: ChartFieldProviderService,
        private dataLakeRestService: DatalakeRestService,
        private dialogService: DialogService,
        private filterExpressionPreviewService: FilterExpressionPreviewService,
    ) {}

    ngOnInit(): void {
        this.sourceConfig.queryConfig.selectedFilters.forEach(filter => {
            filter.chainingOperator ??= 'AND';
        });

        this.sourceConfig.queryConfig.fields.forEach(f => {
            this.tagValues.set(f.runtimeName, []);
        });
        const fieldProvider = this.fieldProviderService.generateFieldLists([
            this.sourceConfig,
        ]);
        this.sourceConfig.queryConfig.fields
            .filter(f =>
                fieldProvider.booleanFields.find(
                    df =>
                        df.measure === this.sourceConfig.measureName &&
                        df.runtimeName === f.runtimeName,
                ),
            )
            .forEach(f => this.tagValues.set(f.runtimeName, ['true', 'false']));
        const fields = this.sourceConfig.queryConfig.fields
            .filter(f =>
                fieldProvider.dimensionFields.find(
                    df =>
                        df.measure === this.sourceConfig.measureName &&
                        df.runtimeName === f.runtimeName,
                ),
            )
            .map(f => f.runtimeName);
        this.dataLakeRestService
            .getTagValues(this.sourceConfig.measureName, fields)
            .subscribe(response => {
                Object.keys(response).forEach(key => {
                    this.tagValues.set(key, response[key]);
                });
            });
    }

    addFilter() {
        const newFilter: SelectedFilter = {
            operator: '=',
            value: '',
            chainingOperator: 'AND',
        };
        this.sourceConfig.queryConfig.selectedFilters.push(newFilter);
        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
        this.updateWidget();
    }

    remove(index: number) {
        this.sourceConfig.queryConfig.selectedFilters.splice(index, 1);

        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
        this.updateWidget();
    }

    openAdvancedFilterDialog(): void {
        const dialogRef = this.dialogService.open(
            AdvancedFilterDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Advanced Filter',
                width: '60vw',
                data: {
                    existingExpression:
                        this.sourceConfig.queryConfig.filterExpression,
                    selectedFilters: this.cloneSelectedFilters(
                        this.sourceConfig.queryConfig.selectedFilters,
                    ),
                    possibleFields: this.sourceConfig.queryConfig.fields,
                    tagValues: this.tagValues,
                },
            },
        );

        dialogRef
            .afterClosed()
            .subscribe((result?: AdvancedFilterDialogResult) => {
                if (!result) {
                    return;
                }

                if (result.action === 'clear') {
                    delete this.sourceConfig.queryConfig.filterExpression;
                } else if (result.action === 'save' && result.expression) {
                    this.sourceConfig.queryConfig.filterExpression =
                        this.cloneExpression(result.expression);
                }

                this.updateWidget();
            });
    }

    hasAdvancedFilterExpression(): boolean {
        return !!this.sourceConfig.queryConfig.filterExpression;
    }

    disableAdvancedFilter(): void {
        delete this.sourceConfig.queryConfig.filterExpression;
        this.updateWidget();
    }

    advancedFilterSummary(): string {
        return this.filterExpressionPreviewService.format(
            this.sourceConfig.queryConfig.filterExpression,
        );
    }

    updateWidget() {
        if (this.sourceConfig.queryConfig.filterExpression) {
            this.widgetConfigService.notify({
                refreshData: true,
                refreshView: true,
            });
            return;
        }

        let update = true;
        this.sourceConfig.queryConfig.selectedFilters.forEach(filter => {
            const hasValue =
                filter.value !== undefined &&
                filter.value !== null &&
                filter.value !== '';
            if (!filter.field || !hasValue || !filter.operator) {
                update = false;
            }
        });

        if (update) {
            this.widgetConfigService.notify({
                refreshData: true,
                refreshView: true,
            });
        }
    }

    private cloneExpression(
        expression: FilterExpressionGroup,
    ): FilterExpressionGroup {
        return {
            type: 'group',
            operator: expression.operator ?? 'AND',
            children: expression.children.map(child =>
                child.type === 'group'
                    ? this.cloneExpression(child)
                    : {
                          type: 'condition',
                          field: child.field,
                          operator: child.operator,
                          condition: child.condition,
                      },
            ),
        };
    }

    private cloneSelectedFilters(filters: SelectedFilter[]): SelectedFilter[] {
        return filters.map(filter => ({
            ...filter,
            field: filter.field ? { ...(filter.field as any) } : undefined,
        }));
    }
}
