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
import { Router } from '@angular/router';
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
import { DatasetOverviewEntry } from './dataset-overview-entry';
import {
    DatasetRestService,
    DataLakeMeasure,
    DatasetSummaryDto,
    ExportProviderService,
    ExportProviderSettings,
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
    SpBasicHeaderTitleComponent,
    SpBasicViewComponent,
    SpBreadcrumbService,
    SpTableAssetContextConfig,
    SpTableActionsDirective,
    SpTableComponent,
    SpSpinnerComponent,
} from '@streampipes/shared-ui';
import { DeleteDatasetDialogComponent } from '../../dialog/delete-dataset/delete-dataset-dialog.component';
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
import { NgStyle } from '@angular/common';
import { StyleDirective } from '@ngbracket/ngx-layout/extended';
import { MatMenuItem } from '@angular/material/menu';
import { catchError, of, Subscription } from 'rxjs';
import { DatasetLastEventLabelComponent } from './dataset-last-event-label/dataset-last-event-label.component';

@Component({
    selector: 'sp-dataset-overview',
    templateUrl: './dataset-overview.component.html',
    styleUrls: ['./dataset-overview.component.scss'],
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
        SpSpinnerComponent,
        FlexOrderDirective,
        NgStyle,
        StyleDirective,
        MatMenuItem,
        MatButton,
        MatTable,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        TranslatePipe,
        SpTableComponent,
        SpBasicHeaderTitleComponent,
        SpBasicViewComponent,
        SpTableActionsDirective,
        DatasetLastEventLabelComponent,
    ],
})
export class DatasetOverviewComponent
    implements OnInit, AfterViewInit, OnDestroy
{
    paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(SpTableComponent)
    spTable!: SpTableComponent<DatasetOverviewEntry>;

    private datasetRestService = inject(DatasetRestService);
    private dialogService = inject(DialogService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private exportProviderRestService = inject(ExportProviderService);
    private translateService = inject(TranslateService);
    private currentUserService = inject(CurrentUserService);
    private assetFilterService = inject(SpAssetBrowserService);
    private router = inject(Router);
    dataSource: MatTableDataSource<DatasetOverviewEntry> =
        new MatTableDataSource([]);
    availableDatasets: DatasetOverviewEntry[] = [];
    filteredDatasets: DatasetOverviewEntry[] = [];
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
        'lastEvent',
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
    currentTime = Date.now();
    currentFilterIds: Set<string> = new Set<string>();

    constructor() {
        this.dataSource.sortingDataAccessor = (configurationEntry, column) => {
            if (column === 'pipeline') {
                return configurationEntry.pipelines.join(', ');
            } else if (column === 'lastEvent') {
                return configurationEntry.lastEvent ?? 0;
            }

            return configurationEntry[column];
        };
    }

    ngOnInit(): void {
        this.assetFilterService.applyAssetLinkType('measurement');
        this.assetFilter$ =
            this.assetFilterService.currentAssetFilter$.subscribe(filter => {
                this.currentFilterIds = filter?.activeElementIds;
                this.applyDatasetFilters(this.currentFilterIds);
            });
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: 'Datasets' },
        ]);
        this.loadAvailableDatasets();
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
            this.receiveLastEventTimes(this.pageIndex);
        });
    }

    ngOnDestroy(): void {
        this.assetFilter$?.unsubscribe();
    }

    loadAvailableExportProvider(): void {
        this.availableExportProvider = [];
        this.exportProviderRestService
            .getAllExportProviders()
            .subscribe(allExportProviders => {
                this.availableExportProvider = allExportProviders;
                this.dataSourceExport.data = this.availableExportProvider;
            });
    }

    loadAvailableDatasets(): void {
        this.availableDatasets = [];
        this.datasetRestService
            .getMeasurementSummary()
            .subscribe(datasetSummary => {
                this.availableDatasets = datasetSummary.resources.map(dataset =>
                    this.toOverviewEntry(dataset),
                );

                this.availableDatasets.sort((a, b) =>
                    a.name.localeCompare(b.name),
                );
                this.applyDatasetFilters(this.currentFilterIds);
            });
    }

    applyDatasetFilters(elementIds: Set<string>): void {
        this.currentFilterIds = elementIds;
        if (elementIds === undefined) {
            this.filteredDatasets = [];
        } else if (elementIds.size === 0) {
            this.filteredDatasets = this.availableDatasets;
        } else {
            this.filteredDatasets = this.availableDatasets.filter(dataset =>
                elementIds.has(dataset.elementId),
            );
        }

        this.dataSource.data = this.filteredDatasets;
        this.updatePaginatorAfterFiltering();
        this.receiveLastEventTimes(this.pageIndex);

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
            Math.ceil(this.filteredDatasets.length / this.pageSize) - 1,
            0,
        );
        if (this.pageIndex > maxPageIndex) {
            this.pageIndex = maxPageIndex;
            this.paginator.pageIndex = maxPageIndex;
        }
    }

    createExportProvider(provider: ExportProviderSettings | null): void {
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

    truncateDataset(datasetName: string): void {
        const dialogRef: DialogRef<DeleteDatasetDialogComponent> =
            this.dialogService.open(DeleteDatasetDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Truncate data'),
                width: '70vw',
                data: {
                    datasetName,
                    deleteDialog: false,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableDatasets();
            }
        });
    }

    deleteDataset(datasetName: string): void {
        const dialogRef: DialogRef<DeleteDatasetDialogComponent> =
            this.dialogService.open(DeleteDatasetDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Delete data'),
                width: '70vw',
                data: {
                    datasetName,
                    deleteDialog: true,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.loadAvailableDatasets();
            }
        });
    }

    deleteExportProvider(providerId: string): void {
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
    testExportProvider(providerId: string): void {
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

    openDownloadDialog(datasetName: string): void {
        this.dialogService.open(DataDownloadDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Download data'),
            width: '50vw',
            data: {
                dataDownloadDialogModel: {
                    measureName: datasetName,
                },
            },
        });
    }

    openDatasetDetails(elementId: string): void {
        this.router.navigate(['datasets', elementId]);
    }

    openRetentionDialog(datasetName: string): void {
        const dialogRef: DialogRef<DataRetentionDialogComponent> =
            this.dialogService.open(DataRetentionDialogComponent, {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Set Data Retention'),
                width: '50vw',
                data: {
                    dataRetentionDialogModel: {
                        measureName: datasetName,
                    },
                    measurementIndex: datasetName,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                setTimeout(() => {
                    this.loadAvailableDatasets();
                }, 1000);
            }
        });
    }

    openRetentionLog(datasetId: string): void {
        this.datasetRestService.getMeasurement(datasetId).subscribe({
            next: dataset => {
                this.openRetentionLogDialog(dataset);
            },
        });
    }

    onPageChange(event: any): void {
        this.pageIndex = event.pageIndex;
        this.pageSize = event.pageSize;
    }

    receiveLastEventTimes(pageIndex: number): void {
        const start = pageIndex * this.pageSize;
        const end = start + this.pageSize;
        const datasets = this.filteredDatasets
            .slice(start, end)
            .filter(dataset => dataset.lastEvent === null);
        if (datasets.length > 0) {
            this.queryLastEventTimes(datasets);
        }
    }
    showPermissionsDialog(element: DatasetOverviewEntry): void {
        this.dialogService.open(ObjectPermissionDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage permissions'),
            width: '50vw',
            data: {
                objectInstanceId: element.elementId,
                headerTitle:
                    this.translateService.instant(
                        'Manage permissions for dataset ',
                    ) + element.name,
            },
        });
    }

    openCsvImportDialog(): void {
        const dialogRef: DialogRef<CsvImportDialogComponent> =
            this.dialogService.open(CsvImportDialogComponent, {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Import CSV'),
                width: '60vw',
                data: {
                    measurementNames: this.availableDatasets.map(
                        dataset => dataset.name,
                    ),
                },
            });

        dialogRef.afterClosed().subscribe(refresh => {
            const importCompleted =
                dialogRef.componentInstance?.instance?.hasImportResult?.() ===
                true;
            if (refresh || importCompleted) {
                this.loadAvailableDatasets();
            }
        });
    }

    queryLastEventTimes(datasets: DatasetOverviewEntry[]): void {
        this.applyLastEventLoadingStatus(datasets, true);
        this.datasetRestService
            .getLatestMeasurementEvents(datasets.map(dataset => dataset.name))
            .pipe(catchError(() => of({} as Record<string, number>)))
            .subscribe(latestEvents => {
                this.applyLastEventLoadingStatus(datasets, false);
                datasets.forEach(dataset => {
                    dataset.lastEvent = latestEvents[dataset.name] ?? 0;
                });
            });
    }

    applyLastEventLoadingStatus(
        datasets: DatasetOverviewEntry[],
        status: boolean,
    ): void {
        datasets.forEach(dataset => {
            dataset.lastEventLoading = status;
        });
    }

    private toOverviewEntry(dataset: DatasetSummaryDto): DatasetOverviewEntry {
        const entry = new DatasetOverviewEntry();
        entry.elementId = dataset.elementId;
        entry.name = dataset.measureName;
        entry.pipelines = dataset.pipelines;
        entry.retentionConfigured = dataset.retentionConfigured;
        entry.lastExport = dataset.lastExport;
        entry.lastRetentionStatus = dataset.lastRetentionStatus;
        entry.remove = dataset.removable;
        entry.lastEvent = null;
        return entry;
    }

    private openRetentionLogDialog(dataset: DataLakeMeasure): void {
        const dialogRef: DialogRef<DataRetentionLogDialogComponent> =
            this.dialogService.open(DataRetentionLogDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translateService.instant('Retention Log'),
                width: '100vw',
                data: {
                    retentionLog:
                        dataset.retentionTime?.retentionExportConfig
                            ?.retentionLog ?? [],
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                setTimeout(() => {
                    this.loadAvailableDatasets();
                }, 1000);
            }
        });
    }
}
