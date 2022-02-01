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
import {Dashboard, DashboardItem} from "@streampipes/platform-services/src/lib/model/dashboard/dashboard.model";
import {forkJoin, Observable, Subscription} from "rxjs";
import {AddVisualizationDialogComponent} from "../../dialogs/add-widget/add-visualization-dialog.component";
import {DashboardService} from "../../services/dashboard.service";
import {RefreshDashboardService} from "../../services/refresh-dashboard.service";
import {DashboardWidgetModel} from "../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model";
import {PanelType} from "../../../core-ui/dialog/base-dialog/base-dialog.model";
import {DialogService} from "../../../core-ui/dialog/base-dialog/base-dialog.service";

@Component({
    selector: 'dashboard-panel',
    templateUrl: './dashboard-panel.component.html',
    styleUrls: ['./dashboard-panel.component.css']
})
export class DashboardPanelComponent implements OnInit {

    @Input() dashboard: Dashboard;
    @Input("editMode") editMode: boolean;
    @Output("editModeChange") editModeChange: EventEmitter<boolean> = new EventEmitter();

    public items: DashboardItem[];

    protected subscription: Subscription;

    widgetIdsToRemove: Array<string> = [];
    widgetsToUpdate: Map<string, DashboardWidgetModel> = new Map<string, DashboardWidgetModel>();

    headerVisible: boolean = true;

    constructor(private dashboardService: DashboardService,
                private dialogService: DialogService,
                private refreshDashboardService: RefreshDashboardService) {
    }

    public ngOnInit() {

    }

    addWidget(): void {
        const dialogRef = this.dialogService.open(AddVisualizationDialogComponent,{
            panelType: PanelType.SLIDE_IN_PANEL,
            title: "Add widget",
            width: "50vw",
        });

        dialogRef.afterClosed().subscribe(widget => {
            if (widget) {
                this.addWidgetToDashboard(widget);
            }
        });
    }

    addWidgetToDashboard(widget: DashboardWidgetModel) {
        let dashboardItem = {} as DashboardItem;
        dashboardItem.widgetId = widget._id;
        dashboardItem.id = widget._id;
        // TODO there should be a widget type DashboardWidget
        dashboardItem.widgetType = widget.dashboardWidgetSettings.widgetName;
        dashboardItem.cols = 4;
        dashboardItem.rows = 4;
        dashboardItem.x = 0;
        dashboardItem.y = 0;
        this.dashboard.widgets.push(dashboardItem);
    }

    updateDashboardAndCloseEditMode() {
        this.dashboardService.updateDashboard(this.dashboard).subscribe(result => {
            if (this.widgetsToUpdate.size > 0) {
                forkJoin(this.prepareWidgetUpdates()).subscribe(result => {
                    this.closeEditModeAndReloadDashboard();
                });
            } else {
                this.deleteWidgets();
                this.closeEditModeAndReloadDashboard()
            }
        })
    }

    closeEditModeAndReloadDashboard() {
        this.editModeChange.emit(!(this.editMode));
        this.refreshDashboardService.notify(this.dashboard._id);
    }

    prepareWidgetUpdates(): Array<Observable<any>> {
        let promises: Array<Observable<any>> = [];
        this.widgetsToUpdate.forEach((widget, key) => {
            promises.push(this.dashboardService.updateWidget(widget));
        })

        return promises;
    }

    discardChanges() {
        this.editModeChange.emit(!(this.editMode));
        this.refreshDashboardService.notify(this.dashboard._id);
    }

    removeAndQueueItemForDeletion(widget: DashboardItem) {
        this.dashboard.widgets.splice(this.dashboard.widgets.indexOf(widget), 1);
        this.widgetIdsToRemove.push(widget.id);
    }

    updateAndQueueItemForDeletion(dashboardWidget: DashboardWidgetModel) {
        this.widgetsToUpdate.set(dashboardWidget._id, dashboardWidget);
    }

    deleteWidgets() {
        this.widgetIdsToRemove.forEach(widgetId => {
            this.dashboardService.deleteWidget(widgetId).subscribe();
        });
    }
}
