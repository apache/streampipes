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
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { AddVisualizationDialogComponent } from '../../dialogs/add-widget/add-visualization-dialog.component';
import {
    DashboardItem,
    DashboardService,
    DashboardWidgetModel,
    DataLakeMeasure,
    DatalakeQueryParameters,
    DatalakeRestService,
    DataViewDataExplorerService,
    Pipeline,
    PipelineService,
    SpQueryResult,
} from '@streampipes/platform-services';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { EditModeService } from '../../services/edit-mode.service';
import { ReloadPipelineService } from '../../services/reload-pipeline.service';
import { zip } from 'rxjs';
import { GridsterItemComponent } from 'angular-gridster2';
import { ResizeService } from '../../services/resize.service';
import { GridsterInfo } from '../../models/gridster-info.model';
import { BaseStreamPipesWidget } from '../widgets/base/base-widget';

@Component({
    selector: 'sp-dashboard-widget',
    templateUrl: './dashboard-widget.component.html',
    styleUrls: ['./dashboard-widget.component.css'],
})
export class DashboardWidgetComponent implements OnInit {
    @Input() widget: DashboardItem;
    @Input() editMode: boolean;
    @Input() headerVisible = false;
    @Input() itemWidth: number;
    @Input() itemHeight: number;
    @Input() gridsterItemComponent: GridsterItemComponent;
    @Input() globalRefresh: boolean;
    @Input() allMeasurements: DataLakeMeasure[];

    @Output() deleteCallback: EventEmitter<DashboardItem> =
        new EventEmitter<DashboardItem>();
    @Output() updateCallback: EventEmitter<DashboardWidgetModel> =
        new EventEmitter<DashboardWidgetModel>();

    widgetLoaded = false;
    configuredWidget: DashboardWidgetModel;
    widgetDataConfig: DataLakeMeasure;
    pipeline: Pipeline;

    pipelineRunning = false;
    widgetNotAvailable = false;

    _activeWidget: BaseStreamPipesWidget;

    constructor(
        private dashboardService: DashboardService,
        private dialogService: DialogService,
        private pipelineService: PipelineService,
        private editModeService: EditModeService,
        private reloadPipelineService: ReloadPipelineService,
        private dataExplorerService: DataViewDataExplorerService,
        private dataLakeRestService: DatalakeRestService,
        private resizeService: ResizeService,
    ) {}

    ngOnInit(): void {
        this.loadWidget();
        this.reloadPipelineService.reloadPipelineSubject.subscribe(() => {
            this.loadWidget();
        });
    }

    loadWidget() {
        this.dashboardService.getWidget(this.widget.id).subscribe(response => {
            this.configuredWidget = response;
            this.loadVisualizablePipeline();
        });
    }

    loadVisualizablePipeline() {
        zip(
            this.dataExplorerService.getPersistedDataStream(
                this.configuredWidget.pipelineId,
                this.configuredWidget.visualizationName,
            ),
        ).subscribe(
            res => {
                const vizPipeline = res[0];
                const measurement = this.allMeasurements.find(
                    m => m.measureName === vizPipeline.measureName,
                );
                vizPipeline.eventSchema = measurement.eventSchema;
                this.widgetDataConfig = vizPipeline;
                this.dashboardService
                    .getPipelineById(vizPipeline.pipelineId)
                    .subscribe(pipeline => {
                        this.pipeline = pipeline;
                        this.pipelineRunning = pipeline.running;
                        this.widgetNotAvailable = false;
                        this.widgetLoaded = true;
                        setTimeout(() => {
                            this.resizeService.notify({
                                gridsterItem: this.widget,
                                gridsterItemComponent:
                                    this.gridsterItemComponent,
                            } as GridsterInfo);
                        }, 20);
                    });
            },
            err => {
                this.widgetLoaded = true;
                this.widgetNotAvailable = true;
            },
        );
    }

    removeWidget() {
        this.deleteCallback.emit(this.widget);
    }

    startPipeline() {
        if (!this.pipelineRunning) {
            this.pipelineService
                .startPipeline(this.pipeline._id)
                .subscribe(status => {
                    this.reloadPipelineService.reloadPipelineSubject.next();
                });
        }
    }

    modifyWidget() {
        this.editModeService.notify(true);
        this.editWidget();
    }

    editWidget(): void {
        const dialogRef = this.dialogService.open(
            AddVisualizationDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Edit widget',
                width: '50vw',
                data: {
                    widget: this.configuredWidget,
                    pipeline: this.widgetDataConfig,
                    editMode: true,
                    startPage: this.widgetNotAvailable
                        ? 'select-pipeline'
                        : 'configure-widget',
                },
            },
        );

        dialogRef.afterClosed().subscribe(widget => {
            if (widget) {
                this.configuredWidget = widget;
                this.loadVisualizablePipeline();
                this.updateCallback.emit(this.configuredWidget);
            }
        });
    }

    @ViewChild('activeWidget')
    set activeWidget(activeWidget: BaseStreamPipesWidget) {
        this._activeWidget = activeWidget;
    }

    getWidgetQuery(): DatalakeQueryParameters {
        if (this._activeWidget) {
            return this._activeWidget.buildQuery(true);
        } else {
            return undefined;
        }
    }

    processQueryResponse(res: SpQueryResult) {
        this._activeWidget.processQueryResult(res);
    }

    getWidgetId(): string {
        return this.widget.id;
    }
}
