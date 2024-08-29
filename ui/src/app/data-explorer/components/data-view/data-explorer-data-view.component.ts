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

import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import {
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DataViewDataExplorerService,
    TimeSelectionId,
    TimeSettings,
} from '@streampipes/platform-services';
import {
    ActivatedRoute,
    ActivatedRouteSnapshot,
    RouterStateSnapshot,
} from '@angular/router';
import { ConfirmDialogComponent } from '@streampipes/shared-ui';
import { TimeSelectionService } from '../../services/time-selection.service';
import { DataExplorerRoutingService } from '../../services/data-explorer-routing.service';
import { DataExplorerDashboardService } from '../../services/data-explorer-dashboard.service';
import { DataExplorerDetectChangesService } from '../../services/data-explorer-detect-changes.service';
import { SupportsUnsavedChangeDialog } from '../../models/dataview-dashboard.model';
import { Observable, of } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { map } from 'rxjs/operators';

@Component({
    selector: 'sp-data-explorer-data-view',
    templateUrl: './data-explorer-data-view.component.html',
    styleUrls: ['./data-explorer-data-view.component.scss'],
})
export class DataExplorerDataViewComponent
    implements OnInit, SupportsUnsavedChangeDialog
{
    dataViewLoaded = false;
    timeSettings: TimeSettings;

    editMode = true;
    dataView: DataExplorerWidgetModel;
    originalDataView: DataExplorerWidgetModel;
    dataLakeMeasure: DataLakeMeasure;
    gridsterItemComponent: any;

    @ViewChild('panel', { static: false }) outerPanel: ElementRef;

    constructor(
        private dashboardService: DataExplorerDashboardService,
        private detectChangesService: DataExplorerDetectChangesService,
        private route: ActivatedRoute,
        private dialog: MatDialog,
        private routingService: DataExplorerRoutingService,
        private dataViewService: DataViewDataExplorerService,
        private timeSelectionService: TimeSelectionService,
    ) {}

    ngOnInit() {
        const dataViewId = this.route.snapshot.params.id;
        this.editMode = this.route.snapshot.queryParams.editMode;

        if (dataViewId) {
            this.loadDataView(dataViewId);
        } else {
            this.createWidget();
            this.timeSettings = this.makeDefaultTimeSettings();
            this.afterDataViewLoaded();
        }
    }

    loadDataView(dataViewId: string): void {
        this.dataViewLoaded = false;
        this.dataViewService.getWidget(dataViewId).subscribe(res => {
            this.dataView = res;
            this.originalDataView = JSON.parse(JSON.stringify(this.dataView));
            if (!this.dataView.timeSettings?.startTime) {
                this.timeSettings = this.makeDefaultTimeSettings();
            } else {
                this.timeSelectionService.updateTimeSettings(
                    this.dataView.timeSettings as TimeSettings,
                    new Date(),
                );
                this.timeSettings = this.dataView.timeSettings as TimeSettings;
            }
            this.afterDataViewLoaded();
        });
    }

    afterDataViewLoaded(): void {
        this.dataViewLoaded = true;
        setTimeout(() => {
            const width = this.outerPanel.nativeElement.offsetWidth;
            const height = this.outerPanel.nativeElement.offsetHeight;
            this.gridsterItemComponent = { width, height };
            this.timeSelectionService.notify(this.timeSettings);
        });
    }

    editDataView(): void {
        this.routingService.navigateToDataView(true, this.dataView.elementId);
    }

    makeDefaultTimeSettings(): TimeSettings {
        return this.timeSelectionService.getDefaultTimeSettings();
    }

    setShouldShowConfirm(): boolean {
        const originalTimeSettings = this.originalDataView
            .timeSettings as TimeSettings;
        const currentTimeSettings = this.dataView.timeSettings as TimeSettings;
        return this.detectChangesService.shouldShowConfirm(
            this.originalDataView,
            this.dataView,
            originalTimeSettings,
            currentTimeSettings,
            model => {
                model.timeSettings = undefined;
            },
        );
    }

    createWidget() {
        this.dataView = new DataExplorerWidgetModel();
        this.dataView['@class'] =
            'org.apache.streampipes.model.datalake.DataExplorerWidgetModel';
        this.dataView.baseAppearanceConfig = {};
        this.dataView.baseAppearanceConfig.widgetTitle = 'New Widget';
        this.dataView.dataConfig = {};
        this.dataView.dataConfig.ignoreMissingValues = false;
        this.dataView.baseAppearanceConfig.backgroundColor = '#FFFFFF';
        this.dataView.baseAppearanceConfig.textColor = '#3e3e3e';
        this.dataView = { ...this.dataView };
    }

    saveDataView(): void {
        this.dataView.timeSettings = this.timeSettings;
        const observable =
            this.dataView.elementId !== undefined
                ? this.dataViewService.updateWidget(this.dataView)
                : this.dataViewService.saveWidget(this.dataView);
        observable.subscribe(() => {
            this.routingService.navigateToOverview(true);
        });
    }

    confirmLeaveDialog(
        _route: ActivatedRouteSnapshot,
        _state: RouterStateSnapshot,
    ): Observable<boolean> {
        if (this.editMode && this.setShouldShowConfirm()) {
            const dialogRef = this.dialog.open(ConfirmDialogComponent, {
                width: '500px',
                data: {
                    title: 'Save changes?',
                    subtitle:
                        'Update all changes to data view or discard current changes.',
                    cancelTitle: 'Discard changes',
                    okTitle: 'Update',
                    confirmAndCancel: true,
                },
            });
            return dialogRef.afterClosed().pipe(
                map(shouldUpdate => {
                    if (shouldUpdate) {
                        this.dataView.timeSettings = this.timeSettings;
                        const observable =
                            this.dataView.elementId !== undefined
                                ? this.dataViewService.updateWidget(
                                      this.dataView,
                                  )
                                : this.dataViewService.saveWidget(
                                      this.dataView,
                                  );
                        observable.subscribe(() => {
                            return true;
                        });
                    }
                    return true;
                }),
            );
        } else {
            return of(true);
        }
    }

    discardChanges() {
        this.routingService.navigateToOverview(true);
    }

    updateDateRange(timeSettings: TimeSettings) {
        this.timeSettings = timeSettings;
        this.timeSelectionService.notify(timeSettings);
    }

    downloadDataAsFile() {
        this.dashboardService.downloadDataAsFile(
            this.timeSettings,
            this.dataView,
        );
    }
}
