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
    ElementRef,
    inject,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import {
    ChartService,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DatalakeRestService,
    EventPropertyUnion,
    FieldConfig,
    LinkageData,
    SourceConfig,
    TimeSelectionConstants,
    SpQueryResult,
    TimeSettings,
} from '@streampipes/platform-services';
import {
    ActivatedRoute,
    ActivatedRouteSnapshot,
    Router,
    RouterStateSnapshot,
} from '@angular/router';
import {
    AssetSaveService,
    ConfirmDialogAction,
    ConfirmDialogComponent,
    CurrentUserService,
    DialogService,
    KeyboardShortcutService,
    PanelType,
    ShortcutRegistration,
    SidebarResizeComponent,
    SpAlertBannerComponent,
    SpBasicViewComponent,
    TimeSelectionService,
} from '@streampipes/shared-ui';
import { ChartRoutingService } from '../../../chart-shared/services/chart-routing.service';
import { ChartSharedService } from '../../../chart-shared/services/chart-shared.service';
import { ChartDetectChangesService } from '../../services/chart-detect-changes.service';
import { SupportsUnsavedChangeDialog } from '../../../chart-shared/models/dataview-dashboard.model';
import { Observable, of, Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { ResizeEchartsService } from '../../../chart-shared/services/resize-echarts.service';
import { ResizeService } from '../../../chart-shared/services/resize.service';
import { AssetDialogComponent } from '../../dialog/asset-dialog.component';
import { AuthService } from '../../../services/auth.service';
import { UserRole } from '../../../core/auth/user-role.enum';
import { ChartFieldProviderService } from '../../../chart-shared/services/chart-field-provider.service';
import { Tuple2 } from '../../../core-model/base/Tuple2';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { ChartViewToolbarComponent } from './toolbar/chart-view-toolbar.component';
import {
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
} from '@angular/material/sidenav';
import { ChartDesignerPanelComponent } from './designer-panel/chart-designer-panel.component';
import { ChartContainerComponent } from '../../../chart-shared/components/chart-container/chart-container.component';
import { ChartDataPreviewComponent } from './query-result-preview/chart-data-preview.component';

@Component({
    selector: 'sp-chart-data-view',
    templateUrl: './chart-view.component.html',
    styleUrls: ['./chart-view.component.scss'],
    imports: [
        SpBasicViewComponent,
        SpAlertBannerComponent,
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        ChartViewToolbarComponent,
        MatDrawerContainer,
        MatDrawer,
        SidebarResizeComponent,
        ChartDesignerPanelComponent,
        MatDrawerContent,
        ChartContainerComponent,
        ChartDataPreviewComponent,
        TranslatePipe,
    ],
})
export class ChartViewComponent
    implements OnInit, OnDestroy, SupportsUnsavedChangeDialog
{
    dataViewLoaded = false;
    timeSettings: TimeSettings;
    readonly legacyMultiSourceWarningTitle = 'Legacy multi-source chart';
    readonly legacyMultiSourceWarningDescription =
        'This chart uses multiple data sources and cannot be edited in this release. Please migrate it manually before making changes.';

    editMode = true;
    dataView: DataExplorerWidgetModel;
    originalDataView: DataExplorerWidgetModel;
    dataLakeMeasure: DataLakeMeasure;
    drawerWidth = 450;

    selectedAssets = [];
    deselectedAssets = [];
    originalAssets = [];

    resizeEchartsService = inject(ResizeEchartsService);
    private shortcutReg: ShortcutRegistration;
    resizeService = inject(ResizeService);

    private shortcutService = inject(KeyboardShortcutService);
    private dataExplorerSharedService = inject(ChartSharedService);
    private detectChangesService = inject(ChartDetectChangesService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private dialog = inject(MatDialog);
    private routingService = inject(ChartRoutingService);
    private dataViewService = inject(ChartService);
    private timeSelectionService = inject(TimeSelectionService);
    private translateService = inject(TranslateService);
    private dialogService = inject(DialogService);
    private currentUserService = inject(CurrentUserService);
    private authService = inject(AuthService);
    private fieldProvider = inject(ChartFieldProviderService);
    private assetSaveService = inject(AssetSaveService);
    private datalakeRestService = inject(DatalakeRestService);

    currentUser$: Subscription;
    queryParams$: Subscription;

    chartNotFound = false;
    legacyMultiSourceChart = false;
    latestQueryResults: SpQueryResult[] = [];

    observableGenerator =
        this.dataExplorerSharedService.defaultObservableGenerator();

    @ViewChild('panel', { static: false }) outerPanel: ElementRef;

    ngOnInit() {
        this.shortcutReg = this.shortcutService.register('chart-view', [
            {
                key: 's',
                ctrl: true,
                action: () => this.onShortcutSave(),
                allowInDialog: true,
            },
        ]);

        const dataViewId = this.route.snapshot.params.id;

        this.currentUser$ = this.currentUserService.user$.subscribe(() => {
            this.editMode = this.shouldEnableEditMode();
        });

        if (dataViewId) {
            this.loadDataView(dataViewId);
        } else {
            this.createWidget();
            this.timeSettings =
                this.getTimeSettingsFromQueryParams() ??
                this.makeDefaultTimeSettings();
            this.dataView.timeSettings = this.timeSettings;
            this.afterDataViewLoaded();
        }

        this.queryParams$ = this.route.queryParams.subscribe(queryParams => {
            this.applyTimeSettingsFromQueryParams(queryParams);
        });
    }

    onAddWidget(event: Tuple2<DataLakeMeasure, DataExplorerWidgetModel>) {
        if (!this.originalDataView?.visualizationConfig) {
            this.setDefaultValuesOnOriginalDataViewForNewCharts();
        }
    }

    setDefaultValuesOnOriginalDataViewForNewCharts() {
        //Change original Data View if default Config does not exist

        //Reset name as widget generation sets name to  datalakename - chart
        this.dataView.baseAppearanceConfig.widgetTitle =
            this.translateService.instant('New chart');
        this.originalDataView = JSON.parse(JSON.stringify(this.dataView));
        this.originalDataView.elementId = undefined;
        this.originalDataView.rev = undefined;
        this.originalDataView.widgetId = undefined;
        //Set default
        this.originalDataView.dataConfig.sourceConfigs[0].queryConfig.order ??=
            'DESC';
        this.addAllFields();
    }

    addAllFields() {
        this.originalDataView.dataConfig.sourceConfigs[0].measure.eventSchema.eventProperties.forEach(
            property => {
                if (this.fieldProvider.isDimensionProperty(property)) {
                    this.addField(property);
                }
            },
        );
    }

    addField(property: EventPropertyUnion) {
        const selection: FieldConfig = {
            runtimeName: property.runtimeName,
            selected: false,
            numeric: this.fieldProvider.isNumber(property),
        };
        this.originalDataView.dataConfig.sourceConfigs[0].queryConfig.groupBy.push(
            selection,
        );
    }

    loadDataView(dataViewId: string): void {
        this.dataViewLoaded = false;
        this.latestQueryResults = [];
        this.dataViewService
            .getChart(dataViewId)
            .pipe(
                catchError(() => {
                    this.chartNotFound = true;
                    return of(null);
                }),
                switchMap(res =>
                    res ? this.refreshDataViewMeasureSchemas(res) : of(null),
                ),
            )
            .subscribe(res => {
                if (!res) {
                    this.dataViewLoaded = true;
                    return;
                } else {
                    this.dataView = res;
                    this.originalDataView = JSON.parse(
                        JSON.stringify(this.dataView),
                    );
                    this.legacyMultiSourceChart = this.hasMultipleSourceConfigs(
                        this.dataView,
                    );
                    this.editMode = this.shouldEnableEditMode();
                    this.timeSettings =
                        this.dataExplorerSharedService.makeChartTimeSettings(
                            this.dataView,
                        );
                    this.timeSettings =
                        this.getTimeSettingsFromQueryParams() ??
                        this.timeSettings;
                    this.afterDataViewLoaded();
                }
            });
    }

    private applyTimeSettingsFromQueryParams(queryParams: {
        [key: string]: any;
    }): void {
        if (!this.timeSettings) {
            return;
        }

        const startDate = Number(queryParams.startDate);
        const endDate = Number(queryParams.endDate);
        if (
            !Number.isFinite(startDate) ||
            !Number.isFinite(endDate) ||
            startDate >= endDate
        ) {
            return;
        }

        if (
            this.timeSettings.startTime === startDate &&
            this.timeSettings.endTime === endDate
        ) {
            return;
        }

        this.timeSettings = {
            ...this.timeSettings,
            startTime: startDate,
            endTime: endDate,
            timeSelectionId: TimeSelectionConstants.CUSTOM,
        };
        this.timeSelectionService.notify(this.timeSettings);
    }

    private getTimeSettingsFromQueryParams(): TimeSettings | undefined {
        const startDate = Number(this.route.snapshot.queryParams.startDate);
        const endDate = Number(this.route.snapshot.queryParams.endDate);

        if (
            !Number.isFinite(startDate) ||
            !Number.isFinite(endDate) ||
            startDate >= endDate
        ) {
            return undefined;
        }

        return {
            startTime: startDate,
            endTime: endDate,
            dynamicSelection: -1,
            timeSelectionId: TimeSelectionConstants.CUSTOM,
        };
    }

    afterDataViewLoaded(): void {
        this.dataViewLoaded = true;
        setTimeout(() => {
            this.timeSelectionService.notify(this.timeSettings);
            this.updateQueryParams(this.timeSettings);
        });
    }

    editDataView(): void {
        this.routingService.navigateToChart(true, this.dataView.elementId);
    }

    onQueryResultsChanged(results: SpQueryResult[]): void {
        this.latestQueryResults = results ?? [];
    }

    makeDefaultTimeSettings(): TimeSettings {
        return this.timeSelectionService.getDefaultTimeSettings();
    }

    setShouldShowConfirm(): boolean {
        let originalTimeSettings: TimeSettings;
        if (!this.dataView) {
            return false;
        }
        if (this.originalDataView?.timeSettings) {
            originalTimeSettings = this.originalDataView
                .timeSettings as TimeSettings;
        } else {
            originalTimeSettings = this.dataView.timeSettings as TimeSettings;
        }
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
        this.dataView.baseAppearanceConfig.widgetTitle =
            this.translateService.instant('New chart');
        this.dataView.dataConfig = {};
        this.dataView.dataConfig.ignoreMissingValues = false;
        this.dataView.baseAppearanceConfig.backgroundColor =
            'var(--color-bg-0)';
        this.dataView.baseAppearanceConfig.textColor =
            'var(--color-default-text)';
        this.dataView.metadata = {
            createdAtEpochMs: Date.now(),
            lastModifiedEpochMs: Date.now(),
        };

        this.dataView = { ...this.dataView };
    }

    saveDataView(): void {
        if (this.legacyMultiSourceChart) {
            return;
        }
        this.dataView.timeSettings = this.timeSettings;
        this.dataView.healthStatus = 'OK';
        this.dataView.metadata ??= {
            lastModifiedEpochMs: undefined,
            createdAtEpochMs: undefined,
        };
        this.dataView.metadata.lastModifiedEpochMs = Date.now();
        const observable =
            this.dataView.elementId !== undefined
                ? this.dataViewService.updateChart(this.dataView)
                : this.dataViewService.saveChart(this.dataView);
        observable.subscribe(data => {
            if (
                this.selectedAssets.length > 0 ||
                this.deselectedAssets.length > 0 ||
                this.originalAssets.length > 0
            ) {
                this.saveToAssets(data);
            }

            this.routingService.navigateToDataViewOverview(true);
        });
    }

    addAssetDialog(): void {
        const dialogRef = this.dialogService.open(AssetDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            width: '500px',
            title: this.translateService.instant(
                'Do you want to link the chart to an Asset?',
            ),
            data: {
                subtitle: this.translateService.instant(
                    'Update asset links or close.',
                ),
                cancelTitle: this.translateService.instant('Close'),
                confirmTitle: this.translateService.instant('Update'),
                editMode: this.editMode,
                selectedAssets: this.selectedAssets,
                deselectedAssets: this.deselectedAssets,
                originalAssets: this.originalAssets,
                dataViewId: this.route.snapshot.params.id,
            },
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.selectedAssets = result.selectedAssets;
                this.deselectedAssets = result.deselectedAssets;
                this.originalAssets = result.originalAssets;
            }
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
                    title: this.translateService.instant('Save changes?'),
                    subtitle: this.translateService.instant(
                        'Update all changes to chart or discard current changes.',
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
                        if (this.legacyMultiSourceChart) {
                            return of(true);
                        }
                        this.dataView.timeSettings = this.timeSettings;
                        this.dataView.healthStatus = 'OK';
                        return (
                            this.dataView.elementId !== undefined
                                ? this.dataViewService.updateChart(
                                      this.dataView,
                                  )
                                : this.dataViewService.saveChart(this.dataView)
                        ).pipe(map(() => true));
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

    discardChanges() {
        this.routingService.navigateToDataViewOverview(true);
    }

    updateDateRange(timeSettings: TimeSettings) {
        this.timeSettings = timeSettings;
        this.timeSelectionService.notify(timeSettings);
        this.updateQueryParams(timeSettings);
    }

    updateQueryParams(timeSettings: TimeSettings) {
        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
                startDate: timeSettings.startTime,
                endDate: timeSettings.endTime,
            },
            queryParamsHandling: 'merge',
            replaceUrl: true,
        });
    }

    downloadDataAsFile() {
        this.dataExplorerSharedService.downloadDataAsFile(
            this.timeSettings,
            this.dataView,
        );
    }

    onWidthChanged(newWidth: number) {
        this.drawerWidth = newWidth;
        this.scheduleChartPanelResize(100);
    }

    onDataPreviewSizeChanged(): void {
        // Preview height animates; send resize updates during and after transition.
        [0, 100, 220, 350].forEach(delay =>
            this.scheduleChartPanelResize(delay),
        );
    }

    private scheduleChartPanelResize(delayMs = 0): void {
        setTimeout(
            () => requestAnimationFrame(() => this.notifyChartPanelResize()),
            delayMs,
        );
    }

    private notifyChartPanelResize(): void {
        const panel = this.outerPanel?.nativeElement;
        if (!panel) {
            return;
        }

        const widgetContent = panel.querySelector(
            '.widget-content',
        ) as HTMLDivElement | null;
        const width = widgetContent?.clientWidth ?? panel.offsetWidth;
        const height = widgetContent?.clientHeight ?? panel.offsetHeight;

        this.resizeService.notify({
            width,
            height,
            widgetId: undefined,
        });
        this.resizeEchartsService.notify(width);
    }

    private async saveAssets(linkageData: LinkageData[]): Promise<void> {
        await this.assetSaveService.saveSelectedAssets(
            this.selectedAssets,
            linkageData,
            this.deselectedAssets,
            this.originalAssets,
        );
    }

    saveToAssets(data: DataExplorerWidgetModel): void {
        let linkageData: LinkageData[];
        try {
            linkageData = this.createLinkageData(data);

            this.saveAssets(linkageData);
        } catch (err) {
            console.error('Error in addToAsset:', err);
        }
    }
    private createLinkageData(data: DataExplorerWidgetModel): LinkageData[] {
        return [
            {
                type: 'chart',
                id: data.elementId,
                name: data.baseAppearanceConfig.widgetTitle,
            },
        ];
    }

    private onShortcutSave(): void {
        if (this.editMode) {
            this.saveDataView();
        }
    }

    ngOnDestroy() {
        this.shortcutReg?.unregister();
        this.currentUser$?.unsubscribe();
        this.queryParams$?.unsubscribe();
    }

    private refreshDataViewMeasureSchemas(
        dataView: DataExplorerWidgetModel,
    ): Observable<DataExplorerWidgetModel> {
        const sourceConfigs = this.getSourceConfigs(dataView);
        if (sourceConfigs.length === 0) {
            return of(dataView);
        }

        return this.datalakeRestService.getAllMeasurementSeries().pipe(
            map(measures => {
                const measuresByName = new Map(
                    measures.map(measure => [measure.measureName, measure]),
                );

                sourceConfigs.forEach(sourceConfig => {
                    const latestMeasure = measuresByName.get(
                        sourceConfig.measureName,
                    );
                    if (latestMeasure) {
                        sourceConfig.measure = latestMeasure;
                    }
                });

                return dataView;
            }),
            catchError(() => of(dataView)),
        );
    }

    private getSourceConfigs(
        dataView: DataExplorerWidgetModel,
    ): SourceConfig[] {
        return dataView?.dataConfig?.sourceConfigs ?? [];
    }

    private hasMultipleSourceConfigs(widget: DataExplorerWidgetModel): boolean {
        return (widget?.dataConfig?.sourceConfigs?.length ?? 0) > 1;
    }

    private shouldEnableEditMode(): boolean {
        return (
            this.authService.hasRole(UserRole.ROLE_DATA_EXPLORER_ADMIN) &&
            !!this.route.snapshot.queryParams.editMode &&
            !this.legacyMultiSourceChart
        );
    }
}
