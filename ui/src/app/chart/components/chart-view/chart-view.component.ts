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
    EventPropertyUnion,
    FieldConfig,
    LinkageData,
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
    ConfirmDialogComponent,
    CurrentUserService,
    DialogService,
    KeyboardShortcutService,
    PanelType,
    ShortcutRegistration,
    SidebarResizeComponent,
    SpBasicViewComponent,
    TimeSelectionService,
} from '@streampipes/shared-ui';
import { ChartRoutingService } from '../../../chart-shared/services/chart-routing.service';
import { ChartSharedService } from '../../../chart-shared/services/chart-shared.service';
import { ChartDetectChangesService } from '../../services/chart-detect-changes.service';
import { SupportsUnsavedChangeDialog } from '../../../chart-shared/models/dataview-dashboard.model';
import { Observable, of, Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { catchError, map } from 'rxjs/operators';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { ResizeEchartsService } from '../../../chart-shared/services/resize-echarts.service';
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

    currentUser$: Subscription;
    queryParams$: Subscription;

    chartNotFound = false;
    latestQueryResults: SpQueryResult[] = [];

    observableGenerator =
        this.dataExplorerSharedService.defaultObservableGenerator();

    @ViewChild('panel', { static: false }) outerPanel: ElementRef;

    ngOnInit() {
        this.shortcutReg = this.shortcutService.register('chart-view', [
            { key: 's', ctrl: true, action: () => this.onShortcutSave() },
        ]);

        const dataViewId = this.route.snapshot.params.id;

        this.currentUser$ = this.currentUserService.user$.subscribe(user => {
            if (!this.authService.hasRole(UserRole.ROLE_DATA_EXPLORER_ADMIN)) {
                this.editMode = false;
            } else {
                this.editMode = this.route.snapshot.queryParams.editMode;
            }
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
        this.dataView.baseAppearanceConfig.backgroundColor = '#FFFFFF';
        this.dataView.baseAppearanceConfig.textColor = '#3e3e3e';
        this.dataView.metadata = {
            createdAtEpochMs: Date.now(),
            lastModifiedEpochMs: Date.now(),
        };

        this.dataView = { ...this.dataView };
    }

    saveDataView(): void {
        this.dataView.timeSettings = this.timeSettings;
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
                okTitle: this.translateService.instant('Update'),
                confirmAndCancel: true,
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
                    cancelTitle:
                        this.translateService.instant('Discard changes'),
                    okTitle: this.translateService.instant('Update'),
                    confirmAndCancel: true,
                },
            });
            return dialogRef.afterClosed().pipe(
                map(shouldUpdate => {
                    if (shouldUpdate) {
                        this.dataView.timeSettings = this.timeSettings;
                        const observable =
                            this.dataView.elementId !== undefined
                                ? this.dataViewService.updateChart(
                                      this.dataView,
                                  )
                                : this.dataViewService.saveChart(this.dataView);
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
        setTimeout(() => {
            this.resizeEchartsService.notify(
                this.outerPanel.nativeElement.offsetWidth,
            );
        }, 100);
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
}
