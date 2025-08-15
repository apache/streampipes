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
    Dashboard,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    TimeSettings,
} from '@streampipes/platform-services';
import { ResizeService } from '../../../data-explorer-shared/services/resize.service';
import { DataExplorerChartRegistry } from '../../../data-explorer-shared/registry/data-explorer-chart-registry';

@Directive()
export abstract class AbstractChartViewDirective {
    protected resizeService = inject(ResizeService);
    protected dataViewDataExplorerService = inject(ChartService);
    protected widgetRegistryService = inject(DataExplorerChartRegistry);

    @Input()
    dashboard: Dashboard;

    @Input()
    widgets: DataExplorerWidgetModel[] = [];

    @Input()
    editMode: boolean;

    @Input()
    currentlyConfiguredWidgetId: string;

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
        this.currentlyConfiguredWidgetId = value.elementId;
    }

    loadWidgetConfigs() {
        this.dashboard.widgets.forEach(widgetConfig => {
            const availableWidget = this.widgets.find(
                w => w.elementId === widgetConfig.id,
            );
            this.processWidget(availableWidget);
        });
        this.onWidgetsAvailable();
        this.widgetsAvailable = true;
    }

    loadWidgetConfig(widgetId: string, setCurrentlyConfigured?: boolean) {
        if (!this.isGridView()) {
            this.widgetsVisible = false;
        }
        this.dataViewDataExplorerService
            .getChart(widgetId)
            .subscribe(response => {
                this.processWidget(response);
                if (setCurrentlyConfigured) {
                    this.propagateWidgetSelection(
                        this.configuredWidgets.get(widgetId),
                    );
                    if (!this.isGridView()) {
                        this.selectNewWidget(widgetId);
                    }
                }
                if (!this.isGridView()) {
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

    propagateItemRemoval(widgetIndex: number) {
        this.deleteCallback.emit(widgetIndex);
    }

    propagateWidgetSelection(configuredWidget: DataExplorerWidgetModel) {
        if (configuredWidget) {
            this.currentlyConfiguredWidgetId = configuredWidget.elementId;
        } else {
            this.currentlyConfiguredWidgetId = undefined;
        }
        this.onOptionsChanged();
    }

    abstract onOptionsChanged(): void;

    abstract onWidgetsAvailable(): void;

    abstract isGridView(): boolean;

    abstract selectNewWidget(widgetId): void;
}
