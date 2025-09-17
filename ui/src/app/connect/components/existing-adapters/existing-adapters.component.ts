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
    OnDestroy,
    OnInit,
    ViewChild,
    ChangeDetectorRef,
} from '@angular/core';
import {
    AdapterDescription,
    AdapterMonitoringService,
    AdapterService,
    PipelineElementAssetService,
    SpLogMessage,
    SpMetricsEntry,
} from '@streampipes/platform-services';
import { MatSortHeader } from '@angular/material/sort';
import { Observable } from 'rxjs';
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
import { MatSort } from '@angular/material/sort';
import { ObjectPermissionDialogComponent } from '../../../core-ui/object-permission-dialog/object-permission-dialog.component';
import { UserRole } from '../../../_enums/user-role.enum';
import { Router } from '@angular/router';
import { AdapterFilterSettingsModel } from '../../model/adapter-filter-settings.model';
import { AdapterFilterPipe } from '../../filter/adapter-filter.pipe';
import { SpConnectRoutes } from '../../connect.routes';
import { Subscription, zip } from 'rxjs';
import { ShepherdService } from '../../../services/tour/shepherd.service';
import { BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
    selector: 'sp-existing-adapters',
    templateUrl: './existing-adapters.component.html',
    styleUrls: ['./existing-adapters.component.scss'],
    standalone: false,
})
export class ExistingAdaptersComponent implements OnInit, OnDestroy {
    existingAdapters: AdapterDescription[] = [];
    filteredAdapters: AdapterDescription[] = [];

    currentFilter: AdapterFilterSettingsModel;
    operationInProgressAdapterId: string | undefined;

    @ViewChild(MatSort)
    sort: MatSort;

    displayedColumns: string[] = [
        'status',
        'start',
        'name',
        'adapterBase',
        'lastModified',
        'messagesSent',
        'lastMessage',
        'action',
    ];

    isAdmin = false;
    refreshSwitch = new BehaviorSubject<boolean>(false);

    adapterMetrics: Record<string, SpMetricsEntry> = {};
    tutorialActive = false;

    userSubscription: Subscription;
    tutorialActiveSubscription: Subscription;
    currentFilterIds: Set<string> = new Set<string>();

    startAdapterErrorText = 'Could not start adapter';
    stopAdapterErrorText = 'Could not stop adapter';

    constructor(
        private cdRef: ChangeDetectorRef,
        private adapterService: AdapterService,
        private dialogService: DialogService,
        private currentUserService: CurrentUserService,
        private router: Router,
        private pipelineElementAssetService: PipelineElementAssetService,
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

        this.setDefaultSort();
    }

    ngAfterViewInit(): void {
        this.setDefaultSort();
    }

    private setDefaultSort(): void {
        if (this.sort) {
            this.sort.sort({
                id: 'lastModified',
                start: 'asc',
                disableClear: false,
            });
        }
        this.cdRef.detectChanges();
    }

    startAdapter(adapter: AdapterDescription) {
        this.operationInProgressAdapterId = adapter.elementId;
        this.adapterService.startAdapter(adapter).subscribe(
            _ => {
                this.getAdaptersRunning();
            },
            error => {
                this.operationInProgressAdapterId = undefined;
                this.openAdapterStatusErrorDialog(adapter, error.error, true);
            },
        );
    }

    stopAdapter(adapter: AdapterDescription, forceStop = false) {
        this.operationInProgressAdapterId = adapter.elementId;
        this.adapterService.stopAdapter(adapter, forceStop).subscribe(
            _ => {
                this.getAdaptersRunning();
            },
            error => {
                this.operationInProgressAdapterId = undefined;
                this.openAdapterStatusErrorDialog(adapter, error.error, false);
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

    openAdapterStatusErrorDialog(
        adapter: AdapterDescription,
        message: SpLogMessage,
        startAction: boolean,
    ) {
        const title = startAction
            ? this.startAdapterErrorText
            : this.stopAdapterErrorText;
        const dialogRef = this.dialogService.open(
            SpExceptionDetailsDialogComponent,
            {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Adapter Status',
                width: '70vw',
                data: {
                    message: message,
                    title: title,
                    additionalButton: !startAction,
                    additionalButtonText: 'Reset adapter state',
                },
            },
        );
        dialogRef.afterClosed().subscribe(forceStop => {
            if (forceStop) {
                this.stopAdapter(adapter, true);
            }
        });
    }

    getMonitoringInfos(adapters: AdapterDescription[]) {
        const filteredElementIds = adapters.map(adapter => adapter.elementId);

        this.adapterMonitoringService
            .getMetricsInfoForAdapters(filteredElementIds)
            .subscribe(metrics => {
                this.adapterMetrics = metrics;
            });
    }

    getIconUrl(adapter: AdapterDescription) {
        if (adapter.includedAssets.length > 0) {
            return (
                this.pipelineElementAssetService.getAssetUrl(adapter.appId) +
                '/icon'
            );
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

    getAdaptersRunning(): void {
        this.operationInProgressAdapterId = undefined;
        this.refreshSwitch.next(!this.refreshSwitch.value);
    }

    applyAdapterFilters(elementIds: Set<string>): void {
        this.currentFilterIds = elementIds;
        this.filteredAdapters = this.adapterFilter
            .transform(this.existingAdapters, this.currentFilter)
            .filter(a => {
                if (elementIds.size === 0) {
                    return true;
                } else {
                    return elementIds.has(a.elementId);
                }
            });
        //this.dataSource.data = this.filteredAdapters;
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
    }

    navigateToDetailsOverviewPage(adapter: AdapterDescription): void {
        this.router.navigate(['connect', 'details', adapter.elementId]);
    }

    ngOnDestroy() {
        this.userSubscription?.unsubscribe();
        this.tutorialActiveSubscription?.unsubscribe();
    }

    getViewKeysForSort(sortActive: string | undefined): string | string[] {
        // This is necessary in case the element names in the HTML and the keys used for sorting follow different naming conventions or are composite keys. (E.g., created in HTML and the database key is createdAt)
        const sortMap: { [key: string]: string | string[] } = {
            lastModified: 'createdAt',
            status: ['running', 'elementId'],
        };

        return sortMap[sortActive ?? ''] || sortActive || '';
    }

    fetchAdapters = (
        startKey?: number,
        endKey?: number,
        pageSize?: number,
    ): Observable<AdapterDescription[]> => {
        const sortBy = this.getSortView();
        return this.adapterService
            .getAdaptersPaginated(
                startKey,
                endKey,
                pageSize,
                sortBy,
                this.sort?.direction !== 'asc',
            )
            .pipe(
                tap(adapters => {
                    this.existingAdapters = adapters;
                    this.applyAdapterFilters(this.currentFilterIds);
                    this.operationInProgressAdapterId = undefined;
                    this.getMonitoringInfos(adapters);
                }),
            );
    };

    private getSortView(): string {
        // Parse naming of the view
        if (this.sort?.active === 'lastModified') {
            return 'createdAt';
        } else if (this.sort?.active === 'status') {
            return 'running';
        }
        return this.sort?.active || 'createdAt';
    }
}
