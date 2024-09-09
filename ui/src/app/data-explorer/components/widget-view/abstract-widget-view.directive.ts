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
import { of, zip } from 'rxjs';
import { DataExplorerWidgetRegistry } from '../../registry/data-explorer-widget-registry';
import { catchError } from 'rxjs/operators';

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

    @Output() deleteCallback: EventEmitter<number> = new EventEmitter<number>();
    @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();

    constructor(
        protected resizeService: ResizeService,
        protected dataViewDataExplorerService: DataViewDataExplorerService,
        protected widgetRegistryService: DataExplorerWidgetRegistry,
    ) {}

    startEditMode(value: DataExplorerWidgetModel) {
        this.startEditModeEmitter.emit(value);
        this.currentlyConfiguredWidgetId = value.elementId;
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
            this.dataViewDataExplorerService
                .getWidget(w.id)
                .pipe(catchError(() => of(undefined))),
        );
        zip(...observables).subscribe(results => {
            results.forEach(r => {
                this.processWidget(r);
                this.onWidgetsAvailable();
                this.widgetsAvailable = true;
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
        if (widget !== undefined) {
            widget.widgetType = this.widgetRegistryService.getWidgetType(
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
