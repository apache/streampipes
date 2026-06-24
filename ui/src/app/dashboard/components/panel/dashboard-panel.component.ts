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
import {
    firstValueFrom,
    from,
    Observable,
    of,
    Subscription,
    timer,
} from 'rxjs';
import { DashboardGridViewComponent } from '../../../dashboard-shared/components/chart-view/grid-view/dashboard-grid-view.component';
import {
    ClientDashboardItem,
    Dashboard,
    DashboardLiveSettings,
    DashboardService,
    ChartService,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    LinkageData,
    PermissionsService,
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
    DialogService,
    FormFieldComponent,
    KeyboardShortcutService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    ObjectManageDialogResult,
    PanelType,
    ShortcutRegistration,
    SpBasicViewComponent,
    SpBreadcrumbService,
    TimeSelectionService,
    AssetSaveService,
} from '@streampipes/shared-ui';
import { MatDialog } from '@angular/material/dialog';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
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
import {
    FlexFillDirective,
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { ChartSelectionComponent } from './chart-selection-panel/chart-selection/chart-selection.component';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
    selector: 'sp-dashboard-panel',
    templateUrl: './dashboard-panel.component.html',
    styleUrls: [
        './dashboard-panel.component.scss',
        '../../../chart/components/chart-view/designer-panel/chart-designer-panel.component.scss',
    ],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        FlexFillDirective,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        DashboardToolbarComponent,
        MatDrawerContainer,
        MatDrawer,
        MatDrawerContent,
        MatTabGroup,
        MatTab,
        ChartSelectionComponent,
        FormFieldComponent,
        MatFormField,
        MatInput,
        FormsModule,
        MatRadioGroup,
        MatRadioButton,
        MatCheckbox,
        MatIconButton,
        MatIcon,
        MatTooltip,
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

    createMode = false;
    editMode = false;
    chartSelectionPanelExpanded = false;
    timeRangeVisible = true;
    selectedDesignerTabIndex = 0;

    _dashboardGrid?: DashboardGridViewComponent;
    _dashboardSlide?: DashboardSlideViewComponent;

    hasDashboardWritePrivileges = false;

    public items: Dashboard[];

    dataLakeMeasure: DataLakeMeasure;
    auth$: Subscription;
    refresh$: Subscription;
    private shortcutReg: ShortcutRegistration;

    private shortcutService = inject(KeyboardShortcutService);
    private detectChangesService = inject(ChartDetectChangesService);
    private dialog = inject(MatDialog);
    private dialogService = inject(DialogService);
    private assetSaveService = inject(AssetSaveService);
    private permissionsService = inject(PermissionsService);
    private chartService = inject(ChartService);
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
        this.dataExplorerSharedService.dashboardObservableGenerator();

    private pendingManageDashboardResult?: ObjectManageDialogResult<Dashboard>;

    public ngOnInit() {
        this.shortcutReg = this.shortcutService.register('dashboard-panel', [
            { key: 'e', action: () => this.onShortcutEdit() },
            {
                key: 's',
                ctrl: true,
                action: () => this.onShortcutSave(),
                allowInDialog: true,
            },
        ]);

        const params = this.route.snapshot.params;
        const queryParams = this.route.snapshot.queryParams;

        const startTime = params.startTime;
        const endTime = params.endTime;

        this.createMode = params.id === 'create';
        if (this.createMode) {
            this.initializeNewDashboard();
        } else {
            this.getDashboard(params.id, startTime, endTime);
        }

        this.auth$ = this.currentUserService.user$.subscribe(_ => {
            this.hasDashboardWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DASHBOARD,
            );
            if (
                (this.createMode || queryParams.editMode) &&
                this.hasDashboardWritePrivileges
            ) {
                this.triggerEditMode();
            }
        });
    }

    ngOnDestroy() {
        this.shortcutReg?.unregister();
        this.auth$?.unsubscribe();
        this.refresh$?.unsubscribe();
    }

    private onShortcutEdit(): void {
        if (!this.editMode && this.hasDashboardWritePrivileges) {
            this.triggerEditMode();
        }
    }

    private onShortcutSave(): void {
        if (this.editMode) {
            this.persistDashboardChanges();
        }
    }

    addChartToDashboard(dataViewElementId: string) {
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
        this.chartService.getChart(dataViewElementId).subscribe(widget => {
            this.addWidgetToPreviewCache(widget);
            setTimeout(() => {
                this.activeDashboardView?.loadWidgetConfig(
                    dashboardItem,
                    widget,
                );
            });
        });
    }

    private addWidgetToPreviewCache(widget: DataExplorerWidgetModel): void {
        if (!this.widgets.some(w => w.elementId === widget.elementId)) {
            this.widgets.push(widget);
        }
    }

    private get activeDashboardView():
        | DashboardGridViewComponent
        | DashboardSlideViewComponent
        | undefined {
        return this.viewMode === 'grid'
            ? this.dashboardGrid
            : this.dashboardSlide;
    }

    onDefaultViewModeChange(viewMode: string): void {
        this.viewMode = viewMode;
        this.dashboard.dashboardGeneralSettings.defaultViewMode = viewMode;
    }

    onGridLayoutSettingsChange(): void {
        this.dashboardGrid?.updateDashboardLayout();
    }

    onGlobalTimeEnabledChange(): void {
        this.updateDateRange(this.timeSettings);
    }

    onChartOverridesChange(): void {
        this.dashboard.dashboardGeneralSettings.chartOverrides = {
            ...this.dashboard.dashboardGeneralSettings.chartOverrides,
        };
    }

    onBorderThicknessChange(value: number | string): void {
        const numericValue = Number(value);
        this.dashboard.dashboardGeneralSettings.chartOverrides = {
            ...this.dashboard.dashboardGeneralSettings.chartOverrides,
            borderThickness: Number.isFinite(numericValue)
                ? Math.max(0, Math.min(12, numericValue))
                : 0,
        };
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
        return (
            this.pendingManageDashboardResult !== undefined ||
            this.detectChangesService.shouldShowConfirm(
                this.originalDashboard,
                this.dashboard,
                originalTimeSettings,
                currentTimeSettings,
                model => {
                    model.dashboardTimeSettings = undefined;
                },
            )
        );
    }

    persistDashboardChanges() {
        this.dashboard.dashboardGeneralSettings.defaultViewMode = this.viewMode;
        if (this.createMode) {
            this.openCreateDashboardDialog();
            return;
        }
        this.saveDashboardChanges().subscribe(() => {
            this.routingService.navigateToDashboardOverview(true);
        });
    }

    private openCreateDashboardDialog(): void {
        const resourceConfig: ObjectManageDialogResourceConfig<Dashboard> = {
            resourceLabel: 'Dashboard',
            nameLabel: 'Dashboard title',
            descriptionLabel: 'Dashboard description',
            idProperty: 'elementId',
            nameProperty: 'name',
            assetLinkType: 'dashboard',
            assetLinkCheckboxLabel:
                'Add the current dashboard to an existing asset',
            saveResource: resource =>
                this.dashboardService.saveDashboard(resource).pipe(
                    tap(savedDashboard => {
                        Object.assign(resource, savedDashboard);
                        Object.assign(this.dashboard, savedDashboard);
                    }),
                ),
        };
        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('New dashboard'),
            width: '50vw',
            data: {
                createMode: true,
                resource: this.dashboard,
                saveMode: 'immediate',
                resourceConfig,
                headerTitle: this.translateService.instant('New dashboard'),
            },
        });

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.createMode = false;
                this.originalDashboard = JSON.parse(
                    JSON.stringify(this.dashboard),
                );
                this.routingService.navigateToDashboardOverview(true);
            }
        });
    }

    manageDashboard(): void {
        const resource: Dashboard = { ...this.dashboard };
        const resourceConfig: ObjectManageDialogResourceConfig<Dashboard> = {
            resourceLabel: 'Dashboard',
            nameLabel: 'Dashboard title',
            descriptionLabel: 'Dashboard description',
            nameProperty: 'name',
            assetLinkType: 'dashboard',
            assetLinkCheckboxLabel:
                'Add the current dashboard to an existing asset',
        };
        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage'),
            width: '50vw',
            data: {
                objectInstanceId: resource.elementId,
                resource,
                saveMode: 'deferred',
                resourceConfig,
                anonymousReadSupported: true,
                publicLink: this.makeDashboardKioskUrl(resource.elementId),
                headerTitle:
                    this.translateService.instant('Manage Dashboard ') +
                    resource.name,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result && typeof result !== 'boolean') {
                this.pendingManageDashboardResult = result;
                Object.assign(this.dashboard, result.resource);
                this.breadcrumbService.updateBreadcrumb(
                    this.breadcrumbService.makeRoute(
                        [SpDashboardRoutes.BASE],
                        this.dashboard.name,
                    ),
                );
            }
        });
    }

    private makeDashboardKioskUrl(dashboardId: string): string {
        return `${window.location.protocol}//${window.location.host}/#/dashboard-kiosk/${dashboardId}`;
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
        this.routingService.navigateToDashboardOverview(true);
    }

    triggerEditMode() {
        this.editMode = true;
        this.chartSelectionPanelExpanded = true;
    }

    toggleChartSelectionPanel() {
        this.chartSelectionPanelExpanded = !this.chartSelectionPanelExpanded;
    }

    deleteDashboard() {
        this.dashboardService.deleteDashboard(this.dashboard).subscribe(_ => {
            this.goBackToOverview();
        });
    }

    private initializeNewDashboard(): void {
        this.dashboard = {
            dashboardGeneralSettings: {
                chartOverrides: {
                    hideToolbox: false,
                },
                defaultViewMode: 'grid',
                globalTimeEnabled: true,
                gridRowHeightPx: 90,
            },
            widgets: [],
            name: '',
            dashboardLiveSettings: {
                refreshModeActive: false,
                refreshIntervalInSeconds: 10,
                label: this.translateService.instant('Off'),
            },
            dashboardTimeSettings:
                this.timeSelectionService.getDefaultTimeSettings(),
            metadata: {
                createdAtEpochMs: Date.now(),
                lastModifiedEpochMs: Date.now(),
            },
            gridColumns: 12,
        };
        this.widgets = [];
        this.originalDashboard = JSON.parse(JSON.stringify(this.dashboard));
        this.viewMode = this.dashboard.dashboardGeneralSettings.defaultViewMode;
        this.timeSettings = this.dashboard.dashboardTimeSettings;
        this.dashboardLoaded = true;
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.makeRoute(
                [SpDashboardRoutes.BASE],
                this.translateService.instant('New dashboard'),
            ),
        );
        this.modifyRefreshInterval(this.dashboard.dashboardLiveSettings);
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
                    this.initializeDashboardSettings();
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
        this.routingService.navigateToDashboardOverview();
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
                        return this.saveDashboardChanges().pipe(
                            map(() => true),
                        );
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

    private saveDashboardChanges(): Observable<unknown> {
        this.dashboard.metadata ??= {
            createdAtEpochMs: undefined,
            lastModifiedEpochMs: undefined,
        };
        this.dashboard.metadata.lastModifiedEpochMs = Date.now();
        return this.dashboardService.updateDashboard(this.dashboard).pipe(
            tap(savedDashboard => {
                Object.assign(this.dashboard, savedDashboard);
            }),
            switchMap(() => from(this.savePendingManageDashboardChanges())),
        );
    }

    private async savePendingManageDashboardChanges(): Promise<void> {
        const result = this.pendingManageDashboardResult;
        if (!result) {
            return;
        }

        if (result.permission) {
            await firstValueFrom(
                this.permissionsService.updatePermission(result.permission),
            );
        }

        if (this.shouldSaveManageDashboardAssets(result)) {
            await this.assetSaveService.saveSelectedAssets(
                result.selectedAssets,
                this.createDashboardLinkageData(result.resource),
                result.deselectedAssets,
                result.originalAssets,
            );
        }

        this.pendingManageDashboardResult = undefined;
    }

    private shouldSaveManageDashboardAssets(
        result: ObjectManageDialogResult<Dashboard>,
    ): boolean {
        return (
            result.addToAssets &&
            (result.selectedAssets.length > 0 ||
                result.deselectedAssets.length > 0 ||
                result.originalAssets.length > 0)
        );
    }

    private createDashboardLinkageData(dashboard: Dashboard): LinkageData[] {
        return [
            {
                type: 'dashboard',
                id: dashboard.elementId,
                name: dashboard.name,
            },
        ];
    }

    private initializeDashboardSettings(): void {
        this.dashboard.dashboardGeneralSettings ??= {};
        this.dashboard.dashboardGeneralSettings.defaultViewMode ||= 'grid';
        this.dashboard.dashboardGeneralSettings.globalTimeEnabled ??= true;
        this.dashboard.dashboardGeneralSettings.chartOverrides ??= {};
        this.dashboard.dashboardGeneralSettings.chartOverrides.hideToolbox ??= false;
        this.dashboard.dashboardGeneralSettings.chartOverrides.borderThickness ??= 0;
        this.dashboard.dashboardGeneralSettings.gridRowHeightPx ??= 90;
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
    set dashboardGrid(v: DashboardGridViewComponent | undefined) {
        this._dashboardGrid = v;
    }

    get dashboardGrid(): DashboardGridViewComponent | undefined {
        return this._dashboardGrid;
    }

    @ViewChild('dashboardSlide', { static: false })
    set dashboardSlide(v: DashboardSlideViewComponent | undefined) {
        this._dashboardSlide = v;
    }

    get dashboardSlide(): DashboardSlideViewComponent | undefined {
        return this._dashboardSlide;
    }
}
