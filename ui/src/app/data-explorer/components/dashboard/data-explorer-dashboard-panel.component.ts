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

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable, of, Subscription, timer } from 'rxjs';
import { DataExplorerDashboardGridComponent } from '../widget-view/grid-view/data-explorer-dashboard-grid.component';
import {
    ClientDashboardItem,
    Dashboard,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DataViewDataExplorerService,
    TimeSelectionId,
    TimeSettings,
} from '@streampipes/platform-services';
import { TimeSelectionService } from '../../services/time-selection.service';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import {
    ActivatedRoute,
    ActivatedRouteSnapshot,
    RouterStateSnapshot,
} from '@angular/router';
import { DataExplorerDashboardSlideViewComponent } from '../widget-view/slide-view/data-explorer-dashboard-slide-view.component';
import {
    ConfirmDialogComponent,
    CurrentUserService,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { map, switchMap } from 'rxjs/operators';
import { SpDataExplorerRoutes } from '../../data-explorer.routes';
import { DataExplorerRoutingService } from '../../services/data-explorer-routing.service';
import { DataExplorerDetectChangesService } from '../../services/data-explorer-detect-changes.service';
import { SupportsUnsavedChangeDialog } from '../../models/dataview-dashboard.model';

@Component({
    selector: 'sp-data-explorer-dashboard-panel',
    templateUrl: './data-explorer-dashboard-panel.component.html',
    styleUrls: ['./data-explorer-dashboard-panel.component.scss'],
})
export class DataExplorerDashboardPanelComponent
    implements OnInit, OnDestroy, SupportsUnsavedChangeDialog
{
    dashboardLoaded = false;
    originalDashboard: Dashboard;
    dashboard: Dashboard;

    /**
     * This is the date range (start, end) to view the data and is set in data-explorer.ts
     */
    timeSettings: TimeSettings;
    viewMode = 'grid';

    editMode = false;
    timeRangeVisible = true;

    @ViewChild('dashboardGrid')
    dashboardGrid: DataExplorerDashboardGridComponent;

    @ViewChild('dashboardSlide')
    dashboardSlide: DataExplorerDashboardSlideViewComponent;

    hasDataExplorerWritePrivileges = false;

    public items: Dashboard[];

    dataLakeMeasure: DataLakeMeasure;
    authSubscription: Subscription;
    refreshSubscription: Subscription;

    constructor(
        private dataViewDataExplorerService: DataViewDataExplorerService,
        private detectChangesService: DataExplorerDetectChangesService,
        private dialog: MatDialog,
        private timeSelectionService: TimeSelectionService,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private dashboardService: DataViewDataExplorerService,
        private route: ActivatedRoute,
        private dataViewService: DataViewDataExplorerService,
        private routingService: DataExplorerRoutingService,
        private breadcrumbService: SpBreadcrumbService,
    ) {}

    public ngOnInit() {
        const params = this.route.snapshot.params;
        const queryParams = this.route.snapshot.queryParams;

        const startTime = params.startTime;
        const endTime = params.endTime;

        this.getDashboard(params.id, startTime, endTime);

        this.authSubscription = this.currentUserService.user$.subscribe(_ => {
            this.hasDataExplorerWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
            );
            if (queryParams.editMode && this.hasDataExplorerWritePrivileges) {
                this.editMode = true;
            }
        });
    }

    ngOnDestroy() {
        this.authSubscription?.unsubscribe();
        this.refreshSubscription?.unsubscribe();
    }

    addDataViewToDashboard(dataViewElementId: string) {
        // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
        const dashboardItem = {} as ClientDashboardItem;
        dashboardItem.id = dataViewElementId;
        dashboardItem.cols = 3;
        dashboardItem.rows = 4;
        dashboardItem.x = 0;
        dashboardItem.y = 0;
        this.dashboard.widgets.push(dashboardItem);
        if (this.viewMode === 'grid') {
            this.dashboardGrid.loadWidgetConfig(dataViewElementId, true);
        } else {
            this.dashboardSlide.loadWidgetConfig(dataViewElementId, true);
        }
    }

    setShouldShowConfirm(): boolean {
        const originalTimeSettings = this.originalDashboard
            .dashboardTimeSettings as TimeSettings;
        const currentTimeSettings = this.dashboard
            .dashboardTimeSettings as TimeSettings;
        return this.detectChangesService.shouldShowConfirm(
            this.originalDashboard,
            this.dashboard,
            originalTimeSettings,
            currentTimeSettings,
            model => {
                model.dashboardTimeSettings = undefined;
            },
        );
    }

    persistDashboardChanges() {
        this.dashboard.dashboardGeneralSettings.defaultViewMode = this.viewMode;
        this.dataViewDataExplorerService
            .updateDashboard(this.dashboard)
            .subscribe(result => {
                this.routingService.navigateToOverview(true);
            });
    }

    startEditMode(widgetModel: DataExplorerWidgetModel) {
        this.routingService.navigateToDataView(
            true,
            widgetModel.elementId,
            true,
        );
    }

    removeDataViewFromDashboard(widgetIndex: number) {
        this.dashboard.widgets.splice(widgetIndex, 1);
    }

    updateDateRange(timeSettings: TimeSettings) {
        let ts = undefined;
        if (this.dashboard.dashboardGeneralSettings.globalTimeEnabled) {
            this.timeSettings = timeSettings;
            this.dashboard.dashboardTimeSettings = timeSettings;
            ts = timeSettings;
        }
        this.timeSelectionService.notify(ts);
    }

    discardChanges() {
        this.routingService.navigateToOverview(true);
    }

    triggerEditMode() {
        this.editMode = true;
    }

    deleteDashboard() {
        this.dashboardService.deleteDashboard(this.dashboard).subscribe(_ => {
            this.goBackToOverview();
        });
    }

    getDashboard(dashboardId: string, startTime: number, endTime: number) {
        this.dataViewService.getDashboard(dashboardId).subscribe(dashboard => {
            this.dashboard = dashboard;
            this.originalDashboard = JSON.parse(JSON.stringify(dashboard));
            this.breadcrumbService.updateBreadcrumb(
                this.breadcrumbService.makeRoute(
                    [SpDataExplorerRoutes.BASE],
                    this.dashboard.name,
                ),
            );
            this.viewMode =
                this.dashboard.dashboardGeneralSettings.defaultViewMode ||
                'grid';
            if (
                this.dashboard.dashboardGeneralSettings.globalTimeEnabled ===
                undefined
            ) {
                this.dashboard.dashboardGeneralSettings.globalTimeEnabled =
                    true;
            }
            if (!this.dashboard.dashboardTimeSettings.startTime) {
                this.dashboard.dashboardTimeSettings =
                    this.timeSelectionService.getDefaultTimeSettings();
            } else {
                this.timeSelectionService.updateTimeSettings(
                    this.dashboard.dashboardTimeSettings,
                    new Date(),
                );
            }
            this.timeSettings =
                startTime && endTime
                    ? this.overrideTime(+startTime, +endTime)
                    : this.dashboard.dashboardTimeSettings;
            this.dashboardLoaded = true;
            this.modifyRefreshInterval();
        });
    }

    overrideTime(startTime: number, endTime: number): TimeSettings {
        return {
            startTime,
            endTime,
            dynamicSelection: -1,
            timeSelectionId: TimeSelectionId.CUSTOM,
        };
    }

    goBackToOverview() {
        this.routingService.navigateToOverview();
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
                        'Update all changes to dashboard widgets or discard current changes.',
                    cancelTitle: 'Discard changes',
                    okTitle: 'Update',
                    confirmAndCancel: true,
                },
            });
            return dialogRef.afterClosed().pipe(
                map(shouldUpdate => {
                    if (shouldUpdate) {
                        this.dashboard.dashboardGeneralSettings.defaultViewMode =
                            this.viewMode;
                        this.dataViewDataExplorerService
                            .updateDashboard(this.dashboard)
                            .subscribe(result => {
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

    modifyRefreshInterval(): void {
        this.refreshSubscription?.unsubscribe();
        if (this.dashboard.dashboardLiveSettings.refreshModeActive) {
            this.createQuerySubscription();
        }
    }

    createQuerySubscription() {
        this.refreshSubscription = timer(
            0,
            this.dashboard.dashboardLiveSettings.refreshIntervalInSeconds *
                1000,
        )
            .pipe(
                switchMap(() => {
                    this.timeSelectionService.updateTimeSettings(
                        this.timeSettings,
                        new Date(),
                    );
                    this.updateDateRange(this.timeSettings);
                    return of(null);
                }),
            )
            .subscribe();
    }
}
