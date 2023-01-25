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

import { Directive, EventEmitter, Input, Output } from '@angular/core';
import {
    Dashboard,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DataViewDataExplorerService,
    TimeSettings,
} from '@streampipes/platform-services';
import { ResizeService } from '../../services/resize.service';
import { zip } from 'rxjs';

@Directive()
export abstract class AbstractWidgetViewDirective {
    _dashboard: Dashboard;

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

    @Output() deleteCallback: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();
    @Output() updateCallback: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();
    @Output() configureWidgetCallback: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();
    @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();

    constructor(
        protected resizeService: ResizeService,
        protected dataViewDataExplorerService: DataViewDataExplorerService,
    ) {}

    updateAllWidgets() {
        this.configuredWidgets.forEach((value, key) => {
            this.dataViewDataExplorerService
                .updateWidget(value)
                .subscribe(response => {
                    value._rev = response._rev;
                    this.currentlyConfiguredWidgetId = undefined;
                });
        });
    }

    startEditMode(value: DataExplorerWidgetModel) {
        this.startEditModeEmitter.emit(value);
        this.currentlyConfiguredWidgetId = value._id;
    }

    @Input() set dashboard(dashboard: Dashboard) {
        this._dashboard = dashboard;
        this.loadWidgetConfigs();
    }

    get dashboard() {
        return this._dashboard;
    }

    loadWidgetConfigs() {
        const observables = this.dashboard.widgets.map(w =>
            this.dataViewDataExplorerService.getWidget(w.id),
        );
        zip(...observables).subscribe(results => {
            results.forEach(r => {
                this.processWidget(r);
                this.onWidgetsAvailable();
                this.widgetsAvailable = true;
                if (this.dashboard.widgets.length > 0 && this.editMode) {
                    this.startEditModeEmitter.emit(
                        this.configuredWidgets.get(
                            this.dashboard.widgets[0].id,
                        ),
                    );
                }
            });
        });
    }

    loadWidgetConfig(widgetId: string, setCurrentlyConfigured?: boolean) {
        if (!this.isGridView()) {
            this.widgetsVisible = false;
        }
        this.dataViewDataExplorerService
            .getWidget(widgetId)
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
        this.configuredWidgets.set(widget._id, widget);
        this.dataLakeMeasures.set(
            widget._id,
            widget.dataConfig.sourceConfigs[0].measure,
        );
    }

    propagateItemRemoval(widget: DataExplorerWidgetModel) {
        this.deleteCallback.emit(widget);
    }

    propagateItemUpdate(dashboardWidget: DataExplorerWidgetModel) {
        this.updateCallback.emit(dashboardWidget);
    }

    propagateWidgetSelection(configuredWidget: DataExplorerWidgetModel) {
        this.configureWidgetCallback.emit(configuredWidget);
        if (configuredWidget) {
            this.currentlyConfiguredWidgetId = configuredWidget._id;
        } else {
            this.currentlyConfiguredWidgetId = undefined;
        }
        this.onOptionsChanged();
    }

    selectFirstWidgetForEditing(widgetId: string): void {
        this.startEditModeEmitter.emit(this.configuredWidgets.get(widgetId));
    }

    abstract onOptionsChanged(): void;

    abstract onWidgetsAvailable(): void;

    abstract isGridView(): boolean;

    abstract selectNewWidget(widgetId): void;
}
