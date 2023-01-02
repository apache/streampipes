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

import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AddService } from './services/add.service';
import {
    DialogRef,
    DialogService,
    PanelType,
    SpBreadcrumbService,
} from '@streampipes/shared-ui';
import { AddEndpointComponent } from './dialogs/add-endpoint/add-endpoint.component';
import { EndpointInstallationComponent } from './dialogs/endpoint-installation/endpoint-installation.component';
import { ExtensionsServiceEndpointItem } from '@streampipes/platform-services';
import { Router } from '@angular/router';
import { SpAddRoutes } from './add.routes';

@Component({
    selector: 'sp-add',
    templateUrl: './add.component.html',
    styleUrls: ['./add.component.scss'],
})
export class AddComponent implements OnInit {
    activeLink: string;

    results: any[];
    loading: boolean;
    endpointItems: ExtensionsServiceEndpointItem[];
    endpointItemsLoadingComplete: boolean;
    selectedTab: string;
    availableTypes: string[] = ['all', 'set', 'stream', 'sepa', 'action'];

    selectedCategoryIndex = 0;
    selectedCategory = 'all';

    selectedEndpointItems: any[] = [];

    _filterTerm = '';
    _selectedInstallationStatus = 'all';

    constructor(
        private addService: AddService,
        private dialogService: DialogService,
        private changeDetectorRef: ChangeDetectorRef,
        private router: Router,
        private breadcrumbService: SpBreadcrumbService,
    ) {
        this.results = [];
        this.loading = false;
        this.endpointItems = [];
        this.endpointItemsLoadingComplete = false;
    }

    ngOnInit() {
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpAddRoutes.BASE),
        );
        this.getEndpointItems();
        this.selectedTab = 'all';
    }

    toggleSelected(endpointItem) {
        if (endpointItem.editable) {
            if (
                this.selectedEndpointItems.some(
                    item => item === endpointItem.uri,
                )
            ) {
                this.selectedEndpointItems.splice(
                    this.selectedEndpointItems.indexOf(endpointItem.uri),
                    1,
                );
            } else {
                this.selectedEndpointItems.push(endpointItem.uri);
            }
            endpointItem.selected = !endpointItem.selected;
        }
    }

    setSelectedTab(index: number) {
        this.selectedEndpointItems = [];
        this.selectAll(false);
        this.selectedTab = this.availableTypes[index];
    }

    isSelected(endpointItem) {
        return endpointItem.selected;
    }

    filterByCatergory(category) {
        this.selectedTab = category.value;
    }

    selectAll(selected) {
        this.selectedEndpointItems = [];
        this.endpointItems.forEach(item => {
            if (item.editable) {
                if (
                    item.type === this.selectedTab ||
                    this.selectedTab === 'all'
                ) {
                    (item as any).selected = selected;
                    if (selected) {
                        this.selectedEndpointItems.push(item.uri);
                    }
                }
            }
        });
        this.changeDetectorRef.detectChanges();
    }

    showManageRdfEndpointsDialog() {
        const dialogRef: DialogRef<AddEndpointComponent> =
            this.dialogService.open(AddEndpointComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Manage Endpoints',
                width: '70vw',
                data: {},
            });
        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.getEndpointItems();
            }
        });
    }

    getEndpointItems() {
        this.endpointItemsLoadingComplete = false;
        this.addService.getRdfEndpointItems().subscribe(endpointItems => {
            this.endpointItems = endpointItems;
            this.endpointItemsLoadingComplete = true;
        });
    }

    installSelected() {
        this.installElements(this.getSelectedElements(true), true);
    }

    uninstallSelected() {
        this.installElements(this.getSelectedElements(false), false);
    }

    getSelectedElements(install) {
        const elementsToInstall = [];

        this.endpointItems.forEach(item => {
            if (item.type === this.selectedTab || this.selectedTab === 'all') {
                if (item.installed === !install && (item as any).selected) {
                    elementsToInstall.push(item);
                }
            }
        });

        return elementsToInstall;
    }

    triggerInstallation(installationInfo: any) {
        this.installElements(
            installationInfo.endpointItems,
            installationInfo.install,
        );
    }

    installElements(endpointItems, install) {
        const dialogRef: DialogRef<EndpointInstallationComponent> =
            this.dialogService.open(EndpointInstallationComponent, {
                panelType: PanelType.STANDARD_PANEL,
                title: 'Installation',
                width: '70vw',
                data: {
                    install: install,
                    endpointItemsToInstall: endpointItems,
                },
            });
        dialogRef.afterClosed().subscribe(data => {
            if (data) {
                this.getEndpointItems();
            }
        });
    }

    set filterTerm(filterTerm: string) {
        this._filterTerm = filterTerm;
        this.selectAll(false);
    }

    get filterTerm(): string {
        return this._filterTerm;
    }

    set selectedInstallationStatus(installationStatus: string) {
        this._selectedInstallationStatus = installationStatus;
        this.selectAll(false);
    }

    get selectedInstallationStatus(): string {
        return this._selectedInstallationStatus;
    }

    navigateTo(routeId: string): void {
        this.router.navigate(['add', routeId]);
        this.activeLink = routeId;
    }
}
