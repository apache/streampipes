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
import { EndpointInstallationComponent } from './dialogs/endpoint-installation/endpoint-installation.component';
import { SpAddRoutes } from './add.routes';
import { ExtensionItemDescription } from '@streampipes/platform-services';
import { MatSelectChange } from '@angular/material/select';

@Component({
    selector: 'sp-add',
    templateUrl: './add.component.html',
    styleUrls: ['./add.component.scss'],
})
export class AddComponent implements OnInit {
    activeLink: string;

    results: any[];
    loading: boolean;
    endpointItems: ExtensionItemDescription[];
    endpointItemsLoadingComplete: boolean;
    selectedTab: string;
    selectedCategory = 'all';

    selectedEndpointItems: ExtensionItemDescription[] = [];

    _filterTerm = '';
    _selectedInstallationStatus = 'all';

    constructor(
        private addService: AddService,
        private dialogService: DialogService,
        private changeDetectorRef: ChangeDetectorRef,
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

    toggleSelected(endpointItem: ExtensionItemDescription) {
        if (endpointItem.editable) {
            if (
                this.selectedEndpointItems.some(item => item === endpointItem)
            ) {
                this.selectedEndpointItems.splice(
                    this.selectedEndpointItems.indexOf(endpointItem),
                    1,
                );
            } else {
                this.selectedEndpointItems.push(endpointItem);
            }
            (endpointItem as any).selected = !(endpointItem as any).selected;
        }
    }

    isSelected(endpointItem: ExtensionItemDescription) {
        return (endpointItem as any).selected;
    }

    filterByCategory(category: MatSelectChange) {
        this.selectedTab = category.value;
    }

    selectAll(selected: boolean) {
        this.selectedEndpointItems = [];
        this.endpointItems.forEach(item => {
            if (item.editable) {
                if (
                    item.serviceTagPrefix === this.selectedTab ||
                    this.selectedTab === 'all'
                ) {
                    (item as any).selected = selected;
                    if (selected) {
                        this.selectedEndpointItems.push(item);
                    }
                }
            }
        });
        this.changeDetectorRef.detectChanges();
    }

    getEndpointItems() {
        this.endpointItemsLoadingComplete = false;
        this.addService.getExtensionItems().subscribe(endpointItems => {
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
            if (
                item.serviceTagPrefix === this.selectedTab ||
                this.selectedTab === 'all'
            ) {
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
}
