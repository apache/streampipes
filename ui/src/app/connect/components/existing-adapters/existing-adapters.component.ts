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
    AdapterDescription,
    AdapterMonitoringService,
    AdapterService,
    AdapterSummaryDto,
    PipelineElementAssetService,
    SpLogMessage,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import {
    CurrentUserService,
    DialogRef,
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    PanelType,
    SpAssetBrowserService,
    SpBasicHeaderTitleComponent,
    SpBasicViewComponent,
    SpBreadcrumbService,
    SpExceptionDetailsDialogComponent,
    SpLabelComponent,
    SpTableAssetContextConfig,
    SpTableMultiActionExecuteEvent,
    SpTableMultiActionOption,
    SpTableActionsDirective,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { DeleteAdapterDialogComponent } from '../../dialog/delete-adapter-dialog/delete-adapter-dialog.component';
import { AllAdapterActionsComponent } from '../../dialog/start-all-adapters/all-adapter-actions-dialog.component';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { Router } from '@angular/router';
import { SpConnectRoutes } from '../../connect.breadcrumb';
import { Subscription } from 'rxjs';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { AdapterStatusLightComponent } from './adapter-status-light/adapter-status-light.component';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatMenuItem } from '@angular/material/menu';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'sp-existing-adapters',
    templateUrl: './existing-adapters.component.html',
    styleUrls: ['./existing-adapters.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        LayoutGapDirective,
        MatButton,
        MatIcon,
        MatIconButton,
        MatTooltip,
        SpBasicHeaderTitleComponent,
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        AdapterStatusLightComponent,
        MatProgressSpinner,
        SpLabelComponent,
        SpTableActionsDirective,
        MatMenuItem,
        DatePipe,
        TranslatePipe,
    ],
})
export class ExistingAdaptersComponent implements OnInit, OnDestroy {
    existingAdapters: AdapterSummaryDto[] = [];
    filteredAdapters: AdapterSummaryDto[] = [];
    operationInProgressAdapterId: string | undefined;

    @ViewChild(MatSort)
    sort: MatSort;

    displayedColumns: string[] = [
        'status',
        'start',
        'name',
        'assetContext',
        'adapterBase',
        'lastModified',
        'messagesSent',
        'lastMessage',
        'actions',
    ];
    readonly assetContextConfig: SpTableAssetContextConfig = {
        resourceLinkType: 'adapter',
        resourceIdKey: 'elementId',
    };

    dataSource: MatTableDataSource<AdapterSummaryDto> =
        new MatTableDataSource();

    adapterMetrics: Record<string, SpMetricsEntry> = {};
    tutorialActive = false;
    readonly bulkAdapterActionOptions: SpTableMultiActionOption[] = [
        { value: 'start', label: 'Start selected', icon: 'play_arrow' },
        { value: 'stop', label: 'Stop selected', icon: 'stop' },
    ];

    assetFilter$: Subscription;
    user$: Subscription;
    tutorial$: Subscription;
    currentFilterIds: Set<string> = new Set<string>();

    startAdapterErrorText = 'Could not start adapter';
    stopAdapterErrorText = 'Could not stop adapter';

    private adapterService = inject(AdapterService);
    private dialogService = inject(DialogService);
    private currentUserService = inject(CurrentUserService);
    private router = inject(Router);
    private pipelineElementAssetService = inject(PipelineElementAssetService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private shepherdService = inject(ShepherdService);
    private translate = inject(TranslateService);
    private adapterMonitoringService = inject(AdapterMonitoringService);
    private assetFilterService = inject(SpAssetBrowserService);

    constructor() {
        this.dataSource.sortingDataAccessor = (adapter, column) => {
            if (column === 'status') {
                return Number(adapter.running);
            } else if (column === 'adapterBase') {
                return adapter.appId;
            } else if (column === 'lastModified') {
                return adapter.createdAt;
            } else if (column === 'messagesSent') {
                return (
                    this.adapterMetrics[adapter.elementId]?.messagesOut
                        ?.counter ?? 0
                );
            } else if (column === 'lastMessage') {
                return (
                    this.adapterMetrics[adapter.elementId]?.lastTimestamp ?? 0
                );
            }
            return adapter[column];
        };
    }

    ngOnInit(): void {
        this.assetFilterService.applyAssetLinkType('adapter');
        this.assetFilter$ =
            this.assetFilterService.currentAssetFilter$.subscribe(filter => {
                this.currentFilterIds = filter?.activeElementIds;
                this.applyAdapterFilters(this.currentFilterIds);
            });
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpConnectRoutes.BASE),
        );
        this.user$ = this.currentUserService.user$.subscribe(_user => {
            this.getAdaptersRunning();
        });
        this.tutorial$ = this.shepherdService.tutorialActive$.subscribe(
            tutorialActive => {
                this.tutorialActive = tutorialActive;
            },
        );
    }

    startAdapter(adapter: AdapterSummaryDto): void {
        this.operationInProgressAdapterId = adapter.elementId;
        this.adapterService
            .startAdapterByElementId(adapter.elementId)
            .subscribe(
                _ => {
                    this.getAdaptersRunning();
                },
                error => {
                    this.operationInProgressAdapterId = undefined;
                    this.openAdapterStatusErrorDialog(
                        adapter,
                        error.error,
                        true,
                    );
                },
            );
    }

    stopAdapter(adapter: AdapterSummaryDto, forceStop = false): void {
        this.operationInProgressAdapterId = adapter.elementId;
        this.adapterService
            .stopAdapterByElementId(adapter.elementId, forceStop)
            .subscribe(
                _ => {
                    this.getAdaptersRunning();
                },
                error => {
                    this.operationInProgressAdapterId = undefined;
                    this.openAdapterStatusErrorDialog(
                        adapter,
                        error.error,
                        false,
                    );
                },
            );
    }

    startStopSelectedAdapters(
        event: SpTableMultiActionExecuteEvent<AdapterSummaryDto>,
    ) {
        if (event.action !== 'start' && event.action !== 'stop') {
            return;
        }

        const selectedAdapters = event.selectedRows ?? [];
        if (!selectedAdapters.length) {
            return;
        }

        const action = event.action === 'start';
        const dialogRef: DialogRef<AllAdapterActionsComponent> =
            this.dialogService.open(AllAdapterActionsComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: action
                    ? this.translate.instant('Start selected adapters')
                    : this.translate.instant('Stop selected adapters'),
                width: '70vw',
                data: {
                    adapters: selectedAdapters,
                    action: action,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.getAdaptersRunning();
            }
        });
    }

    openAdapterStatusErrorDialog(
        adapter: AdapterSummaryDto,
        message: SpLogMessage,
        startAction: boolean,
    ): void {
        const title = startAction
            ? this.startAdapterErrorText
            : this.stopAdapterErrorText;
        const dialogRef = this.dialogService.open(
            SpExceptionDetailsDialogComponent,
            {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translate.instant('Adapter status'),
                width: '70vw',
                data: {
                    message: message,
                    title: title,
                    additionalButton: !startAction,
                    additionalButtonText: this.translate.instant(
                        'Reset adapter state',
                    ),
                },
            },
        );
        dialogRef.afterClosed().subscribe(forceStop => {
            if (forceStop) {
                this.stopAdapter(adapter, true);
            }
        });
    }

    getMonitoringInfos(adapters: AdapterSummaryDto[]): void {
        const filteredElementIds = adapters.map(adapter => adapter.elementId);

        this.adapterMonitoringService
            .getMetricsInfoForAdapters(filteredElementIds)
            .subscribe(metrics => {
                this.adapterMetrics = metrics;
                this.dataSource.data = [...this.filteredAdapters];
            });
    }

    getIconUrl(adapter: AdapterSummaryDto): string | undefined {
        if (adapter.includedAssets?.some(asset => asset.startsWith('icon.'))) {
            return (
                this.pipelineElementAssetService.getAssetUrl(adapter.appId) +
                '/icon'
            );
        }
    }

    showManageDialog(adapter: AdapterSummaryDto): void {
        this.adapterService.getAdapter(adapter.elementId).subscribe(fullAdapter => {
            const resourceConfig: ObjectManageDialogResourceConfig<AdapterDescription> =
                {
                    resourceLabel: 'Adapter',
                    nameLabel: 'Adapter name',
                    descriptionLabel: 'Adapter description',
                    nameProperty: 'name',
                    assetLinkType: 'adapter',
                    assetLinkCheckboxLabel:
                        'Add the current adapter to an existing asset',
                    saveResource: resource =>
                        this.adapterService.updateAdapter(resource),
                };

            const dialogRef = this.dialogService.open(
                ObjectManageDialogComponent,
                {
                    panelType: PanelType.SLIDE_IN_PANEL,
                    title: this.translate.instant('Manage'),
                    width: '50vw',
                    data: {
                        objectInstanceId:
                            fullAdapter.correspondingDataStreamElementId,
                        resource: fullAdapter,
                        saveMode: 'immediate',
                        resourceConfig,
                        headerTitle:
                            this.translate.instant('Manage Adapter ') +
                            adapter.name,
                    },
                },
            );

            dialogRef.afterClosed().subscribe(refresh => {
                if (refresh) {
                    this.getAdaptersRunning();
                }
            });
        });
    }

    /**
     * Start edit mode
     * @param adapter
     */
    editAdapter(adapter: AdapterSummaryDto): void {
        this.router.navigate(['connect', 'edit', adapter.elementId]);
    }

    deleteAdapter(adapter: AdapterSummaryDto): void {
        const dialogRef: DialogRef<DeleteAdapterDialogComponent> =
            this.dialogService.open(DeleteAdapterDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: this.translate.instant('Delete Adapter'),
                width: '70vw',
                data: {
                    adapter: adapter,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.getAdaptersRunning();
            }
        });
    }

    getAdaptersRunning(): void {
        this.adapterService.getAdapterSummary().subscribe(adapterSummary => {
            this.existingAdapters = adapterSummary.resources;
            this.existingAdapters.sort((a, b) => a.name.localeCompare(b.name));
            this.applyAdapterFilters(this.currentFilterIds);
            this.operationInProgressAdapterId = undefined;
            this.getMonitoringInfos(this.existingAdapters);
            setTimeout(() => {
                this.dataSource.sort = this.sort;
            });
        });
    }

    applyAdapterFilters(elementIds: Set<string>): void {
        this.currentFilterIds = elementIds;
        this.filteredAdapters = this.existingAdapters.filter(a => {
            if (elementIds === undefined) {
                return false;
            } else if (elementIds.size === 0) {
                return true;
            } else {
                return elementIds.has(a.elementId);
            }
        });
        this.dataSource.data = this.filteredAdapters;
    }

    startAdapterTutorial(): void {
        this.shepherdService.startAdapterTour();
    }

    createNewAdapter(): void {
        this.router.navigate(['connect', 'catalog']).then(() => {
            this.shepherdService.trigger('new-adapter-clicked');
        });
    }

    navigateToDetailsOverviewPage(adapter: AdapterSummaryDto): void {
        this.router.navigate(['connect', 'details', adapter.elementId]);
    }

    ngOnDestroy(): void {
        this.user$?.unsubscribe();
        this.tutorial$?.unsubscribe();
        this.assetFilter$?.unsubscribe();
    }
}
