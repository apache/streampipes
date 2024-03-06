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
import {
    AdapterDescription,
    AdapterMonitoringService,
    AdapterService,
    PipelineElementService,
    SpLogMessage,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import { MatTableDataSource } from '@angular/material/table';
import {
    CurrentUserService,
    DialogRef,
    DialogService,
    PanelType,
    SpBreadcrumbService,
    SpExceptionDetailsDialogComponent,
} from '@streampipes/shared-ui';
import { DeleteAdapterDialogComponent } from '../../dialog/delete-adapter-dialog/delete-adapter-dialog.component';
import { AllAdapterActionsComponent } from '../../dialog/start-all-adapters/all-adapter-actions-dialog.component';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ObjectPermissionDialogComponent } from '../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { UserRole } from '../../../_enums/user-role.enum';
import { HelpComponent } from '../../../editor/dialog/help/help.component';
import { Router } from '@angular/router';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';
import { AdapterFilterPipe } from '../../filter/adapter-filter.pipe';
import { SpConnectRoutes } from '../../connect.routes';
import { Subscription, zip } from 'rxjs';
import { RestApi } from '../../../services/rest-api.service';
import { ShepherdService } from '../../../services/tour/shepherd.service';

@Component({
    selector: 'sp-existing-adapters',
    templateUrl: './existing-adapters.component.html',
    styleUrls: ['./existing-adapters.component.scss'],
})
export class ExistingAdaptersComponent implements OnInit, OnDestroy {
    existingAdapters: AdapterDescription[] = [];
    filteredAdapters: AdapterDescription[] = [];

    currentFilter: AdapterFilterSettingsModel;

    @ViewChild(MatPaginator)
    paginator: MatPaginator;
    pageSize = 1;
    @ViewChild(MatSort)
    sort: MatSort;

    displayedColumns: string[] = [
        'start',
        'name',
        'adapterBase',
        'lastModified',
        'messagesSent',
        'lastMessage',
        'action',
    ];

    dataSource: MatTableDataSource<AdapterDescription>;
    isAdmin = false;

    adapterMetrics: Record<string, SpMetricsEntry> = {};
    tutorialActive = false;

    userSubscription: Subscription;
    tutorialActiveSubscription: Subscription;

    constructor(
        private adapterService: AdapterService,
        private dialogService: DialogService,
        private currentUserService: CurrentUserService,
        private pipelineElementService: PipelineElementService,
        private router: Router,
        private restApi: RestApi,
        private adapterFilter: AdapterFilterPipe,
        private breadcrumbService: SpBreadcrumbService,
        private adapterMonitoringService: AdapterMonitoringService,
        private shepherdService: ShepherdService,
    ) {}

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpConnectRoutes.BASE),
        );
        this.userSubscription = this.currentUserService.user$.subscribe(
            user => {
                this.isAdmin = user.roles.indexOf(UserRole.ROLE_ADMIN) > -1;
                this.getAdaptersRunning();
            },
        );
        this.tutorialActiveSubscription =
            this.shepherdService.tutorialActive$.subscribe(tutorialActive => {
                this.tutorialActive = tutorialActive;
            });
    }

    startAdapter(adapter: AdapterDescription) {
        this.adapterService.startAdapter(adapter).subscribe(
            _ => {
                this.getAdaptersRunning();
            },
            error => {
                this.openAdapterStatusErrorDialog(
                    error.error,
                    'Could not start adapter',
                );
            },
        );
    }

    stopAdapter(adapter: AdapterDescription) {
        this.adapterService.stopAdapter(adapter).subscribe(
            _ => {
                this.getAdaptersRunning();
            },
            error => {
                this.openAdapterStatusErrorDialog(
                    error.error,
                    'Could not stop adapter',
                );
            },
        );
    }

    checkCurrentSelectionStatus(status) {
        let active = true;
        this.existingAdapters.forEach(adapter => {
            if (adapter.running == status) {
                active = false;
            }
        });
        return active;
    }

    startAllAdapters(action: boolean) {
        const dialogRef: DialogRef<AllAdapterActionsComponent> =
            this.dialogService.open(AllAdapterActionsComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: (action ? 'Start' : 'Stop') + ' all adapters',
                width: '70vw',
                data: {
                    adapters: this.existingAdapters,
                    action: action,
                },
            });

        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.getAdaptersRunning();
            }
        });
    }

    openAdapterStatusErrorDialog(message: SpLogMessage, title: string) {
        this.dialogService.open(SpExceptionDetailsDialogComponent, {
            panelType: PanelType.STANDARD_PANEL,
            title: 'Adapter Status',
            width: '70vw',
            data: {
                message: message,
                title: title,
            },
        });
    }

    getMonitoringInfos(adapters: AdapterDescription[]) {
        const observables = adapters
            .map(adapter => adapter.elementId)
            .map(elementId =>
                this.adapterMonitoringService.getMetricsInfoForAdapter(
                    elementId,
                ),
            );

        this.adapterMonitoringService
            .triggerMonitoringUpdate()
            .subscribe(() => {
                zip(...observables).subscribe(metrics => {
                    adapters.forEach((adapter, index) => {
                        this.adapterMetrics[adapter.elementId] = metrics[index];
                    });
                });
            });
    }

    getIconUrl(adapter: AdapterDescription) {
        if (adapter.includedAssets.length > 0) {
            return this.restApi.getAssetUrl(adapter.appId) + '/icon';
        }
    }

    showPermissionsDialog(adapter: AdapterDescription) {
        const dialogRef = this.dialogService.open(
            ObjectPermissionDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Manage permissions',
                width: '50vw',
                data: {
                    objectInstanceId: adapter.correspondingDataStreamElementId,
                    headerTitle:
                        'Manage permissions for adapter ' + adapter.name,
                },
            },
        );

        dialogRef.afterClosed().subscribe(refresh => {
            if (refresh) {
                this.getAdaptersRunning();
            }
        });
    }

    /**
     * Start edit mode
     * @param adapter
     */
    editAdapter(adapter: AdapterDescription) {
        this.router.navigate(['connect', 'edit', adapter.elementId]);
    }

    deleteAdapter(adapter: AdapterDescription): void {
        const dialogRef: DialogRef<DeleteAdapterDialogComponent> =
            this.dialogService.open(DeleteAdapterDialogComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Delete Adapter',
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

    openHelpDialog(adapter: AdapterDescription) {
        const streamId = adapter.correspondingDataStreamElementId;

        this.pipelineElementService
            .getDataStreamByElementId(streamId)
            .subscribe(stream => {
                if (stream) {
                    this.dialogService.open(HelpComponent, {
                        panelType: PanelType.STANDARD_PANEL,
                        title: stream.name,
                        width: '70vw',
                        data: {
                            pipelineElement: stream,
                        },
                    });
                }
            });
    }

    getAdaptersRunning(): void {
        this.adapterService.getAdapters().subscribe(adapters => {
            this.existingAdapters = adapters;
            this.existingAdapters.sort((a, b) => a.name.localeCompare(b.name));
            this.filteredAdapters = this.adapterFilter.transform(
                this.existingAdapters,
                this.currentFilter,
            );
            this.dataSource = new MatTableDataSource(this.filteredAdapters);
            this.getMonitoringInfos(adapters);
            setTimeout(() => {
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;
            });
        });
    }

    startAdapterTutorial() {
        this.shepherdService.startAdapterTour();
    }

    createNewAdapter(): void {
        this.router.navigate(['connect', 'create']).then(() => {
            this.shepherdService.trigger('new-adapter-clicked');
        });
    }

    applyFilter(filter: AdapterFilterSettingsModel) {
        this.currentFilter = filter;
        if (this.dataSource) {
            this.dataSource.data = this.adapterFilter.transform(
                this.filteredAdapters,
                this.currentFilter,
            );
        }
    }

    navigateToDetailsOverviewPage(adapter: AdapterDescription): void {
        this.router.navigate([
            'connect',
            'details',
            adapter.elementId,
            'metrics',
        ]);
    }

    ngOnDestroy() {
        this.userSubscription?.unsubscribe();
        this.tutorialActiveSubscription?.unsubscribe();
    }
}
