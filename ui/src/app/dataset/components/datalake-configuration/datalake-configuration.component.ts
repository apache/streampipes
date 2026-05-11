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
    AfterViewInit,
    Component,
    inject,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
    MatTableDataSource,
} from '@angular/material/table';
import { DataLakeConfigurationEntry } from './datalake-configuration-entry';
import {
    ChartService,
    DataLakeMeasure,
    DatalakeRestService,
    ExportProviderService,
    ExportProviderSettings,
    RetentionLog,
} from '@streampipes/platform-services';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    CurrentUserService,
    DataDownloadDialogComponent,
    DialogRef,
    DialogService,
    LocalStorageService,
    ObjectPermissionDialogComponent,
    PanelType,
    SpAssetBrowserService,
    SpAlertBannerComponent,
    SpBasicHeaderTitleComponent,
    SpBasicViewComponent,
    SpBreadcrumbService,
    SpLabelComponent,
    SpTableAssetContextConfig,
    SpTableActionsDirective,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { DeleteDatalakeIndexComponent } from '../../dialog/delete-datalake-index/delete-datalake-index-dialog.component';
import { SpConfigurationRoutes } from '../../../configuration/configuration.breadcrumb';
import { DataRetentionDialogComponent } from '../../dialog/data-retention-dialog/data-retention-dialog.component';
import { ExportProviderComponent } from '../../dialog/export-provider-dialog/export-provider-dialog.component';
import { DeleteExportProviderComponent } from '../../dialog/delete-export-provider/delete-export-provider-dialog.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { ExportProviderConnectionTestComponent } from '../../dialog/export-provider-connection-test/export-provider-connection-test.component';
import { DataRetentionLogDialogComponent } from '../../dialog/data-retention-log-dialog/data-retention-log-dialog.component';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import { UserRole } from '../../../core/auth/user-role.enum';
import { CsvImportDialogComponent } from '../../dialog/csv-import-dialog/csv-import-dialog.component';
import {
    FlexDirective,
    FlexOrderDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { DatePipe, DecimalPipe, NgIf, NgStyle } from '@angular/common';
import { StyleDirective } from '@ngbracket/ngx-layout/extended';
import { MatMenuItem } from '@angular/material/menu';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-datalake-configuration',
    templateUrl: './datalake-configuration.component.html',
    styleUrls: ['./datalake-configuration.component.scss'],
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        MatIconButton,
        MatTooltip,
        MatIcon,
        LayoutGapDirective,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        MatProgressSpinner,
        FlexOrderDirective,
        NgStyle,
        StyleDirective,
        MatMenuItem,
        MatButton,
        MatTable,
        NgIf,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        DecimalPipe,
        DatePipe,
        TranslatePipe,
        SpLabelComponent,
        SpTableComponent,
        SpBasicHeaderTitleComponent,
        SpBasicViewComponent,
        SpAlertBannerComponent,
        SpTableActionsDirective,
    ],
})
export class DatalakeConfigurationComponent
    implements OnInit, AfterViewInit, OnDestroy
{
    paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(SpTableComponent)
    spTable!: SpTableComponent<DataLakeConfigurationEntry>;

    private datalakeRestService = inject(DatalakeRestService);
    private dataViewDataExplorerService = inject(ChartService);
    private dialogService = inject(DialogService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private exportProviderRestService = inject(ExportProviderService);
    private translateService = inject(TranslateService);
    private currentUserService = inject(CurrentUserService);
    private assetFilterService = inject(SpAssetBrowserService);

    dataSource: MatTableDataSource<DataLakeConfigurationEntry> =
        new MatTableDataSource([]);
    availableMeasurements: DataLakeConfigurationEntry[] = [];
    filteredMeasurements: DataLakeConfigurationEntry[] = [];
    availableExportProvider: ExportProviderSettings[] = [];
    readonly assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'measurement',
        resourceIdKey: 'elementId',
    };

    dataSourceExport: MatTableDataSource<ExportProviderSettings> =
        new MatTableDataSource([]);

    displayedColumns: string[] = [
        'name',
        'assetContext',
        'pipeline',
        'eventsLatest',
        'eventsTotal',
        'retention',
        'actions',
    ];

    displayedColumnsExport: string[] = [
        'providertype',
        'endpoint',
        'bucket',
        'editExportProvider',
        'delete',
        'test',
    ];

    private localStorageService = inject(LocalStorageService);

    pageSize = this.localStorageService.get('paginator-page-size', 10);
    pageIndex = 0;
    isAdmin = false;
    writeAccess = false;
    assetFilter$: Subscription;
    currentFilterIds: Set<string> = new Set<string>();

    ngOnInit(): void {
        this.assetFilterService.applyAssetLinkType('measurement');
        this.assetFilter$ =
            this.assetFilterService.currentAssetFilter$.subscribe(filter => {
                this.currentFilterIds = filter?.activeElementIds;
                this.applyMeasurementFilters(this.currentFilterIds);
            });
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: 'Datasets' },
        ]);
        this.loadAvailableMeasurements();
        this.loadAvailableExportProvider();
        const currentUser = this.currentUserService.getCurrentUser();
        this.isAdmin = currentUser.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
        this.writeAccess =
            currentUser.roles.indexOf(UserPrivilege.PRIVILEGE_WRITE_DATASET) >
                -1 || this.isAdmin;
    }

    ngAfterViewInit() {
        this.paginator = this.spTable.paginator;
        this.dataSource.sort = this.sort;

        this.spTable.paginator.page.subscribe(event => {
            this.pageIndex = event.pageIndex;
            this.pageSize = event.pageSize;
            this.receiveMeasurementSizes(this.pageIndex);
        });
    }

    ngOnDestroy(): void {
        this.assetFilter$?.unsubscribe();
    }

    loadAvailableExportProvider() {
        this.availableExportProvider = [];
        this.exportProviderRestService
            .getAllExportProviders()
            .subscribe(allExportProviders => {
                this.availableExportProvider = allExportProviders;
                this.dataSourceExport.data = this.availableExportProvider;
            });
    }

    loadAvailableMeasurements() {
        this.availableMeasurements = [];
        // get all available measurements that are stored in the data lake
        this.datalakeRestService
            .getAllMeasurementSeries()
            .subscribe(allMeasurements => {
                // get all measurements that are still used in pipelines
                this.dataViewDataExplorerService
                    .getAllPersistedDataStreams()
                    .subscribe(inUseMeasurements => {
                        allMeasurements.forEach(measurement => {
                            const entry = new DataLakeConfigurationEntry();
                            entry.elementId = measurement.elementId;
                            entry.name = measurement.measureName;
                            entry.eventsLatest = -1;
                            entry.eventsTotal = -1;
                            if (measurement?.retentionTime != null) {
                                entry.retention = measurement.retentionTime;
                            }
                            inUseMeasurements.forEach(inUseMeasurement => {
                                if (
                                    inUseMeasurement.measureName ===
                                    measurement.measureName
                                ) {
                                    entry.pipelines.push(
                                        inUseMeasurement.pipelineName,
                                    );
                                    if (inUseMeasurement.pipelineIsRunning) {
                                        entry.remove = false;
                                    }
                                }
                            });
                            this.availableMeasurements.push(entry);
                        });

                        this.availableMeasurements.sort((a, b) =>
                            a.name.localeCompare(b.name),
                        );
                        this.applyMeasurementFilters(this.currentFilterIds);
                    });
            });
    }

    applyMeasurementFilters(elementIds: Set<string>) {
        this.currentFilterIds = elementIds;
        if (elementIds === undefined) {
            this.filteredMeasurements = [];
        } else if (elementIds.size === 0) {
            this.filteredMeasurements = this.availableMeasurements;
        } else {
            this.filteredMeasurements = this.availableMeasurements.filter(
                measurement => elementIds.has(measurement.elementId),
            );
        }

        this.dataSource.data = this.filteredMeasurements;
        this.updatePaginatorAfterFiltering();
        this.receiveMeasurementSizes(this.pageIndex);

        setTimeout(() => {
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
        });
    }

    updatePaginatorAfterFiltering(): void {
        if (!this.paginator) {
            return;
        }

        const maxPageIndex = Math.max(
            Math.ceil(this.filteredMeasurements.length / this.pageSize) - 1,
            0,
        );
        if (this.pageIndex > maxPageIndex) {
            this.pageIndex = maxPageIndex;
            this.paginator.pageIndex = maxPageIndex;
        }
    }

    createExportProvider(provider: ExportProviderSettings | null) {
        const dialogRef: DialogRef<ExportProviderComponent> =
            this.dialogService.open(ExportProviderComponent, {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('New Export Provider'),
                width: '50vw',
                data: {
                    provider: provider,
                },
            });

        dialogRef.afterClosed().subscribe(() => {
            this.loadAvailableExportProvider();
        });
    }

    cleanDatalakeIndex(measurementIndex: string) {
        const dialogRef: DialogRef<DeleteDatalakeIndexComponent> =
            this.dialogService.open(DeleteDatalakeIndexComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Truncate data'),
                width: '70vw',
                data: {
                    measurementIndex: measurementIndex,
                    deleteDialog: false,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableMeasurements();
            }
        });
    }

    deleteDatalakeIndex(measurementIndex: string) {
        const dialogRef: DialogRef<DeleteDatalakeIndexComponent> =
            this.dialogService.open(DeleteDatalakeIndexComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Delete data'),
                width: '70vw',
                data: {
                    measurementIndex: measurementIndex,
                    deleteDialog: true,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableMeasurements();
            }
        });
    }

    deleteExportProvider(providerId: string) {
        const dialogRef: DialogRef<DeleteExportProviderComponent> =
            this.dialogService.open(DeleteExportProviderComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Delete Export Provider'),
                width: '70vw',
                data: {
                    providerId: providerId,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableExportProvider();
            }
        });
    }
    testExportProvider(providerId: string) {
        const dialogRef: DialogRef<ExportProviderConnectionTestComponent> =
            this.dialogService.open(ExportProviderConnectionTestComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant(
                    'Test Export Provider Connection',
                ),
                width: '70vw',
                data: {
                    providerId: providerId,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableExportProvider();
            }
        });
    }

    openDownloadDialog(measurementName: string) {
        this.dialogService.open(DataDownloadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Download data'),
            width: '50vw',
            data: {
                dataDownloadDialogModel: {
                    measureName: measurementName,
                },
            },
        });
    }

    openRetentionDialog(measurementId: string) {
        const dialogRef: DialogRef<DataRetentionDialogComponent> =
            this.dialogService.open(DataRetentionDialogComponent, {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Set Data Retention'),
                width: '50vw',
                data: {
                    dataRetentionDialogModel: {
                        measureName: measurementId,
                    },
                    measurementIndex: measurementId,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                setTimeout(() => {
                    this.loadAvailableMeasurements();
                }, 1000);
            }
        });
    }

    openRetentionLog(retentionLog: RetentionLog[]) {
        const dialogRef: DialogRef<DataRetentionLogDialogComponent> =
            this.dialogService.open(DataRetentionLogDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Retention Log'),
                width: '100vw',
                data: {
                    retentionLog: retentionLog,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                setTimeout(() => {
                    this.loadAvailableMeasurements();
                }, 1000);
            }
        });
    }

    onPageChange(event: any) {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
        //this.receiveMeasurementSizes(this.pageIndex);
    }

    receiveTotalMeasurementSize(entry: DataLakeConfigurationEntry) {
        this.queryEntryCounts([entry.name], 'eventsTotal');
    }

    receiveMeasurementSizes(pageIndex: number) {
        const start = pageIndex * this.pageSize;
        const end = start + this.pageSize;
        const measurements = this.filteredMeasurements
            .slice(start, end)
            .filter(m => m.eventsLatest === -1)
            .map(m => m.name);
        if (measurements.length > 0) {
            this.queryEntryCounts(measurements, 'eventsLatest', 7);
        }
    }
    showPermissionsDialog(element: DataLakeMeasure) {
        this.dialogService.open(ObjectPermissionDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage permissions'),
            width: '50vw',
            data: {
                objectInstanceId: element.elementId,
                headerTitle:
                    this.translateService.instant(
                        'Manage permissions for dataset ',
                    ) + element.measureName,
            },
        });
    }

    openCsvImportDialog() {
        const dialogRef: DialogRef<CsvImportDialogComponent> =
            this.dialogService.open(CsvImportDialogComponent, {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Import CSV'),
                width: '60vw',
                data: {
                    measurementNames: this.availableMeasurements.map(
                        measurement => measurement.name,
                    ),
                },
            });

        dialogRef.afterClosed().subscribe(refresh => {
            const importCompleted =
                dialogRef.componentInstance?.instance?.hasImportResult?.() ===
                true;
            if (refresh || importCompleted) {
                this.loadAvailableMeasurements();
            }
        });
    }

    queryEntryCounts(
        measurements: string[],
        targetField: string,
        daysBack = -1,
    ): void {
        this.applyLoadingStatus(measurements, targetField, true);
        this.datalakeRestService
            .getMeasurementEntryCounts(measurements, daysBack)
            .subscribe(res => {
                this.applyLoadingStatus(measurements, targetField, false);
                this.availableMeasurements.forEach(m => {
                    if (res[m.name] !== undefined) {
                        m[targetField] = res[m.name];
                    }
                });
            });
    }

    applyLoadingStatus(
        measurements: string[],
        targetField: string,
        status: boolean,
    ): void {
        const loadingField = targetField + 'Loading';
        this.availableMeasurements.forEach(m => {
            if (measurements.includes(m.name)) {
                m[loadingField] = status;
            }
        });
    }
}
