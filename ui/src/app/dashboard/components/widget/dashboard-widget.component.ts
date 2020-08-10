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

import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {DashboardItem} from "../../models/dashboard.model";
import {DashboardService} from "../../services/dashboard.service";
import {GridsterItem, GridsterItemComponent} from "angular-gridster2";
import {AddVisualizationDialogComponent} from "../../dialogs/add-widget/add-visualization-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {
    DashboardWidgetModel,
    VisualizablePipeline
} from "../../../core-model/gen/streampipes-model";

@Component({
    selector: 'dashboard-widget',
    templateUrl: './dashboard-widget.component.html',
    styleUrls: ['./dashboard-widget.component.css']
})
export class DashboardWidgetComponent implements OnInit {

    @Input() widget: DashboardItem;
    @Input() editMode: boolean;
    @Input() item: GridsterItem;
    @Input() gridsterItemComponent: GridsterItemComponent;

    @Output() deleteCallback: EventEmitter<DashboardItem> = new EventEmitter<DashboardItem>();
    @Output() updateCallback: EventEmitter<DashboardWidgetModel> = new EventEmitter<DashboardWidgetModel>();

    widgetLoaded: boolean = false;
    configuredWidget: DashboardWidgetModel;
    widgetDataConfig: VisualizablePipeline;

    pipelineNotRunning: boolean = false;

    constructor(private dashboardService: DashboardService,
                private dialog: MatDialog) {
    }

    ngOnInit(): void {
        this.dashboardService.getWidget(this.widget.id).subscribe(response => {
            this.configuredWidget = response;
            this.dashboardService.getVisualizablePipelineByTopic(this.configuredWidget.visualizablePipelineTopic).subscribe(pipeline => {
                this.widgetDataConfig = pipeline;
                this.pipelineNotRunning = false;
                this.widgetLoaded = true;
            }, err => {
                this.widgetLoaded = true;
                this.pipelineNotRunning = true;
            });
        });
    }

    removeWidget() {
        this.deleteCallback.emit(this.widget);
    }

    editWidget(): void {
        const dialogRef = this.dialog.open(AddVisualizationDialogComponent, {
            width: '70%',
            height: '500px',
            panelClass: 'custom-dialog-container',
            data: {
                "widget": this.configuredWidget,
                "pipeline": this.widgetDataConfig
            }
        });

        dialogRef.afterClosed().subscribe(widget => {
            if (widget) {
                this.configuredWidget = widget;
                this.updateCallback.emit(this.configuredWidget);
            }
        });
    }
}