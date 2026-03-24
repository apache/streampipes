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

import { Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable, of, Subscription, timer } from 'rxjs';
import { DashboardGridViewComponent } from '../../../dashboard-shared/components/chart-view/grid-view/dashboard-grid-view.component';
import {
    ClientDashboardItem,
    Dashboard,
    DashboardLiveSettings,
    DashboardService,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    TimeSelectionConstants,
    TimeSettings,
} from '@streampipes/platform-services';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import {
    ActivatedRoute,
    ActivatedRouteSnapshot,
    RouterStateSnapshot,
} from '@angular/router';
import { DashboardSlideViewComponent } from '../../../dashboard-shared/components/chart-view/slide-view/dashboard-slide-view.component';
import {
    ConfirmDialogAction,
    ConfirmDialogComponent,
    CurrentUserService,
    SpBasicViewComponent,
    SpBreadcrumbService,
    TimeSelectionService,
} from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { catchError, map, switchMap } from 'rxjs/operators';
import { SpDashboardRoutes } from '../../dashboard.breadcrumb';
import { ChartRoutingService } from '../../../chart-shared/services/chart-routing.service';
import { ChartDetectChangesService } from '../../../chart/services/chart-detect-changes.service';
import { SupportsUnsavedChangeDialog } from '../../../chart-shared/models/dataview-dashboard.model';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { DataExplorerDashboardService } from '../../../dashboard-shared/services/dashboard.service';
import { ChartSharedService } from '../../../chart-shared/services/chart-shared.service';
import { DashboardToolbarComponent } from './dashboard-toolbar/dashboard-toolbar.component';
import {
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
} from '@angular/material/sidenav';
import { ChartSelectionPanelComponent } from './chart-selection-panel/chart-selection-panel.component';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';

@Component({
    selector: 'sp-dashboard-panel',
    templateUrl: './dashboard-panel.component.html',
    styleUrls: ['./dashboard-panel.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        DashboardToolbarComponent,
        MatDrawerContainer,
        MatDrawer,
        ChartSelectionPanelComponent,
        MatDrawerContent,
        DashboardGridViewComponent,
        DashboardSlideViewComponent,
        TranslatePipe,
    ],
})
export class DashboardPanelComponent
    implements OnInit, OnDestroy, SupportsUnsavedChangeDialog
{
    dashboardLoaded = false;
    originalDashboard: Dashboard;
    dashboard: Dashboard;
    widgets: DataExplorerWidgetModel[] = [];
    dashboardNotFound = false;

    /**
     * This is the date range (start, end) to view the data and is set in data-explorer.ts
     */
    timeSettings: TimeSettings;
    viewMode = 'grid';

    editMode = false;
    timeRangeVisible = true;

    _dashboardGrid: DashboardGridViewComponent;
    _dashboardSlide: DashboardSlideViewComponent;

    hasDashboardWritePrivileges = false;

    public items: Dashboard[];

    dataLakeMeasure: DataLakeMeasure;
    auth$: Subscription;
    refresh$: Subscription;

    private detectChangesService = inject(ChartDetectChangesService);
    private dialog = inject(MatDialog);
    private timeSelectionService = inject(TimeSelectionService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private dashboardService = inject(DashboardService);
    private route = inject(ActivatedRoute);
    private routingService = inject(ChartRoutingService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private translateService = inject(TranslateService);
    private dataExplorerDashboardService = inject(DataExplorerDashboardService);
    private dataExplorerSharedService = inject(ChartSharedService);

    observableGenerator =
        this.dataExplorerSharedService.defaultObservableGenerator();

    public ngOnInit() {
        const params = this.route.snapshot.params;
        const queryParams = this.route.snapshot.queryParams;

        const startTime = params.startTime;
        const endTime = params.endTime;

        this.getDashboard(params.id, startTime, endTime);

        this.auth$ = this.currentUserService.user$.subscribe(_ => {
            this.hasDashboardWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DASHBOARD,
            );
            if (queryParams.editMode && this.hasDashboardWritePrivileges) {
                this.editMode = true;
            }
        });
    }

    ngOnDestroy() {
        this.auth$?.unsubscribe();
        this.refresh$?.unsubscribe();
    }

    addChartToDashboard(dataViewElementId: string) {
        // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
        const dashboardItem = {} as ClientDashboardItem;
        dashboardItem.id =
            this.dataExplorerDashboardService.makeUniqueWidgetId();
        dashboardItem.cols = 3;
        dashboardItem.rows = 4;
        dashboardItem.w = 3;
        dashboardItem.h = 4;
        dashboardItem.x = 0;
        dashboardItem.y = this.getNextWidgetY();
        dashboardItem.dataViewElementId = dataViewElementId;
        this.dashboard.widgets.push(dashboardItem);
        setTimeout(() => {
            if (this.viewMode === 'grid') {
                this.dashboardGrid.loadWidgetConfig(dashboardItem);
            } else {
                this.dashboardSlide.loadWidgetConfig(dashboardItem);
            }
        });
    }

    private getNextWidgetY(): number {
        if (!this.dashboard?.widgets?.length) {
            return 0;
        }

        return this.dashboard.widgets.reduce((maxY, widget) => {
            const currentY = widget.y ?? 0;
            const currentHeight = widget.h ?? widget.rows ?? 1;
            return Math.max(maxY, currentY + currentHeight);
        }, 0);
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
        this.dashboard.metadata ??= {
            createdAtEpochMs: undefined,
            lastModifiedEpochMs: undefined,
        };
        this.dashboard.metadata.lastModifiedEpochMs = Date.now();
        this.dashboardService
            .updateDashboard(this.dashboard)
            .subscribe(result => {
                this.routingService.navigateToDashboardOverview(true);
            });
    }

    startEditMode(widgetModel: DataExplorerWidgetModel) {
        this.routingService.navigateToChart(true, widgetModel.elementId, true);
    }

    removeChartFromDashboard(widgetIndex: number) {
        this.dashboard.widgets.splice(widgetIndex, 1);
        this.widgets.splice(widgetIndex, 1);
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
        this.routingService.navigateToDataViewOverview(true);
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
        this.dashboardService
            .getCompositeDashboard(dashboardId)
            .pipe(
                catchError(() => {
                    this.dashboardNotFound = true;
                    return of(null);
                }),
            )
            .subscribe(resp => {
                if (!resp) {
                    return;
                }
                if (resp.ok) {
                    const compositeDashboard = resp.body;
                    compositeDashboard.dashboard.widgets.forEach(w => {
                        w.id ??=
                            this.dataExplorerDashboardService.makeUniqueWidgetId();
                    });
                    this.dashboard = compositeDashboard.dashboard;
                    this.widgets = compositeDashboard.widgets;
                    this.originalDashboard = JSON.parse(
                        JSON.stringify(compositeDashboard.dashboard),
                    );
                }
                this.breadcrumbService.updateBreadcrumb(
                    this.breadcrumbService.makeRoute(
                        [SpDashboardRoutes.BASE],
                        this.dashboard.name,
                    ),
                );
                this.viewMode =
                    this.dashboard.dashboardGeneralSettings.defaultViewMode ||
                    'grid';
                if (
                    this.dashboard.dashboardGeneralSettings
                        .globalTimeEnabled === undefined
                ) {
                    this.dashboard.dashboardGeneralSettings.globalTimeEnabled = true;
                }
                if (!this.dashboard.dashboardTimeSettings.startTime) {
                    this.dashboard.dashboardTimeSettings =
                        this.timeSelectionService.getDefaultTimeSettings();
                } else {
                    this.timeSelectionService.updateTimeSettings(
                        this.timeSelectionService.defaultQuickTimeSelections,
                        this.dashboard.dashboardTimeSettings,
                        new Date(),
                    );
                }
                this.timeSettings =
                    startTime && endTime
                        ? this.overrideTime(+startTime, +endTime)
                        : this.dashboard.dashboardTimeSettings;
                this.dashboardLoaded = true;
                this.modifyRefreshInterval(
                    this.dashboard.dashboardLiveSettings,
                );
            });
    }

    overrideTime(startTime: number, endTime: number): TimeSettings {
        return {
            startTime,
            endTime,
            dynamicSelection: -1,
            timeSelectionId: TimeSelectionConstants.CUSTOM,
        };
    }

    goBackToOverview() {
        this.routingService.navigateToDataViewOverview();
    }

    confirmLeaveDialog(
        _route: ActivatedRouteSnapshot,
        _state: RouterStateSnapshot,
    ): Observable<boolean> {
        if (this.editMode && this.setShouldShowConfirm()) {
            const dialogRef = this.dialog.open(ConfirmDialogComponent, {
                width: '500px',
                data: {
                    title: this.translateService.instant('Save changes?'),
                    subtitle: this.translateService.instant(
                        'Update all changes to dashboard charts or discard current changes.',
                    ),
                    neutralTitle: this.translateService.instant('Keep editing'),
                    cancelTitle:
                        this.translateService.instant('Discard changes'),
                    confirmTitle: this.translateService.instant('Update'),
                },
            });
            return dialogRef.afterClosed().pipe(
                switchMap((dialogResult: ConfirmDialogAction | undefined) => {
                    if (dialogResult === 'confirm') {
                        this.dashboard.dashboardGeneralSettings.defaultViewMode =
                            this.viewMode;
                        return this.dashboardService
                            .updateDashboard(this.dashboard)
                            .pipe(map(() => true));
                    }

                    if (dialogResult === 'cancel') {
                        return of(true);
                    }

                    return of(false);
                }),
            );
        } else {
            return of(true);
        }
    }

    modifyRefreshInterval(liveSettings: DashboardLiveSettings): void {
        this.dashboard.dashboardLiveSettings = liveSettings;
        this.refresh$?.unsubscribe();
        if (this.dashboard.dashboardLiveSettings.refreshModeActive) {
            this.createQuerySubscription();
        }
    }

    createQuerySubscription() {
        this.refresh$ = timer(
            0,
            this.dashboard.dashboardLiveSettings.refreshIntervalInSeconds *
                1000,
        )
            .pipe(
                switchMap(() => {
                    this.timeSelectionService.updateTimeSettings(
                        this.timeSelectionService.defaultQuickTimeSelections,
                        this.timeSettings,
                        new Date(),
                    );
                    this.updateDateRange(this.timeSettings);
                    return of(null);
                }),
            )
            .subscribe();
    }

    @ViewChild('dashboardGrid', { static: false })
    set dashboardGrid(v: DashboardGridViewComponent) {
        if (v) {
            this._dashboardGrid = v;
        }
    }

    get dashboardGrid(): DashboardGridViewComponent {
        return this._dashboardGrid;
    }

    @ViewChild('dashboardSlide', { static: false })
    set dashboardSlide(v: DashboardSlideViewComponent) {
        if (v) {
            this._dashboardSlide = v;
        }
    }

    get dashboardSlide(): DashboardSlideViewComponent {
        return this._dashboardSlide;
    }
}
