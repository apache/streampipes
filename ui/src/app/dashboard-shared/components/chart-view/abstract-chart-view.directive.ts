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
import { DataExplorerChartRegistry } from '../../../data-explorer-shared/registry/data-explorer-chart-registry';
import { ObservableGenerator } from '../../../data-explorer-shared/models/dataview-dashboard.model';

@Directive()
export abstract class AbstractChartViewDirective {
    protected dataViewDataExplorerService = inject(ChartService);
    protected widgetRegistryService = inject(DataExplorerChartRegistry);

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

    @Output() deleteCallback: EventEmitter<number> = new EventEmitter<number>();
    @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();

    startEditMode(value: DataExplorerWidgetModel) {
        this.startEditModeEmitter.emit(value);
    }

    loadWidgetConfigs() {
        this.dashboard.widgets.forEach(widgetConfig => {
            widgetConfig.w ??= widgetConfig.cols;
            widgetConfig.h ??= widgetConfig.rows;
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
        this.dataViewDataExplorerService
            .getChart(dashboardItem.dataViewElementId)
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

    abstract onWidgetsAvailable(): void;

    abstract isGridView(): boolean;

    abstract selectNewWidget(widgetId): void;
}
