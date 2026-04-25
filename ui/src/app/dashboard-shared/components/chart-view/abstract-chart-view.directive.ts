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

import { Directive, EventEmitter, inject, Input, Output } from '@angular/core';
import {
    ChartService,
    ClientDashboardItem,
    Dashboard,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    TimeSettings,
} from '@streampipes/platform-services';
import { ChartRegistry } from '../../../chart-shared/registry/chart-registry.service';
import { ObservableGenerator } from '../../../chart-shared/models/dataview-dashboard.model';
import {
    applyDashboardItemGridConstraints,
    getDashboardItemLabel,
    isChartDashboardItem,
} from '../../utils/dashboard-item.utils';

@Directive()
export abstract class AbstractChartViewDirective {
    protected dataViewDataExplorerService = inject(ChartService);
    protected widgetRegistryService = inject(ChartRegistry);

    @Input()
    dashboard: Dashboard;

    @Input()
    widgets: DataExplorerWidgetModel[] = [];

    @Input()
    editMode: boolean;

    @Input()
    observableGenerator: ObservableGenerator;

    configuredWidgets: Map<string, DataExplorerWidgetModel> = new Map<
        string,
        DataExplorerWidgetModel
    >();
    dataLakeMeasures: Map<string, DataLakeMeasure> = new Map<
        string,
        DataLakeMeasure
    >();

    widgetsAvailable = false;
    widgetsVisible = true;

    /**
     * This is the date range (start, end) to view the data and is set in data-explorer.ts
     */
    @Input()
    timeSettings: TimeSettings;

    @Input()
    selectedDashboardItemId?: string;

    @Output() deleteCallback: EventEmitter<number> = new EventEmitter<number>();
    @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();
    @Output() selectDashboardItemEmitter: EventEmitter<string | undefined> =
        new EventEmitter<string | undefined>();

    startEditMode(value: DataExplorerWidgetModel) {
        this.startEditModeEmitter.emit(value);
    }

    loadWidgetConfigs() {
        this.dashboard.widgets.forEach(widgetConfig => {
            applyDashboardItemGridConstraints(widgetConfig);
            widgetConfig.w ??= widgetConfig.cols;
            widgetConfig.h ??= widgetConfig.rows;
            if (!isChartDashboardItem(widgetConfig)) {
                return;
            }
            const availableWidget = this.widgets.find(
                w => w.elementId === widgetConfig.dataViewElementId,
            );
            this.processWidget(availableWidget);
        });
        this.onWidgetsAvailable();
        this.widgetsAvailable = true;
    }

    loadWidgetConfig(dashboardItem: ClientDashboardItem) {
        if (!this.isGridView()) {
            this.widgetsAvailable = false;
        }
        if (!isChartDashboardItem(dashboardItem)) {
            if (!this.isGridView()) {
                this.selectNewWidget(dashboardItem.id);
                this.widgetsVisible = true;
            }
            this.widgetsAvailable = true;
            return;
        }

        this.dataViewDataExplorerService
            .getChart(dashboardItem.dataViewElementId!)
            .subscribe(response => {
                this.processWidget(response);
                if (!this.isGridView()) {
                    this.selectNewWidget(dashboardItem.id);
                    this.widgetsVisible = true;
                }
                this.widgetsAvailable = true;
            });
    }

    processWidget(widget: DataExplorerWidgetModel) {
        if (widget !== undefined) {
            widget.widgetType = this.widgetRegistryService.getChartType(
                widget.widgetType,
            );
            this.configuredWidgets.set(widget.elementId, widget);
            this.dataLakeMeasures.set(
                widget.elementId,
                widget.dataConfig.sourceConfigs[0].measure,
            );
        }
    }

    isChartItem(item: ClientDashboardItem | undefined): boolean {
        return isChartDashboardItem(item);
    }

    getDashboardItemLabel(item: ClientDashboardItem | undefined): string {
        return getDashboardItemLabel(item);
    }

    selectDashboardItem(itemId: string | undefined): void {
        this.selectDashboardItemEmitter.emit(itemId);
    }

    abstract onWidgetsAvailable(): void;

    abstract isGridView(): boolean;

    abstract selectNewWidget(widgetId): void;
}
