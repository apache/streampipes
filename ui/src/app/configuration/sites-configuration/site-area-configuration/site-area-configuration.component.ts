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

import { Component, Input, OnInit, ViewChild } from '@angular/core';
import {
    AssetConstants,
    AssetSiteDesc,
    GenericStorageService,
    LocationConfig,
} from '@streampipes/platform-services';
import { MatTableDataSource } from '@angular/material/table';
import { ManageSiteDialogComponent } from '../../dialog/manage-site/manage-site-dialog.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { TranslateService } from '@ngx-translate/core';
import { MatSort } from '@angular/material/sort';

@Component({
    selector: 'sp-site-area-configuration',
    templateUrl: './site-area-configuration.component.html',
    standalone: false,
})
export class SiteAreaConfigurationComponent implements OnInit {
    @Input()
    locationConfig: LocationConfig;

    allSites: AssetSiteDesc[] = [];
    dataSource: MatTableDataSource<AssetSiteDesc> =
        new MatTableDataSource<AssetSiteDesc>();

    allUsedSiteIds = [];

    @ViewChild(MatSort)
    sort: MatSort;
    displayedColumns = ['name', 'areas', 'actions'];

    constructor(
        private genericStorageService: GenericStorageService,
        private dialogService: DialogService,
        private translateService: TranslateService,
    ) {}

    ngOnInit() {
        this.loadSites();
        this.dataSource.sortingDataAccessor = (site, column) => {
            if (column === 'name') {
                return site.label;
            } else if (column === 'areas') {
                return site.areas.toString();
            }
            return site[column];
        };
    }

    loadSites(): void {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_SITES_APP_DOC_NAME)
            .subscribe(res => {
                this.allSites = res;
                this.dataSource.data = this.allSites;
                setTimeout(() => {
                    this.dataSource.sort = this.sort;
                });
            });
        this.listSitesInUse();
    }

    listSitesInUse(): void {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_APP_DOC_NAME)
            .subscribe(res => {
                this.allUsedSiteIds = this.extractSiteIds(res);
            });
    }

    extractSiteIds(assets) {
        const allSiteIds = new Set<string>();

        assets.forEach(asset => allSiteIds.add(asset.assetSite.siteId));

        return Array.from(allSiteIds);
    }

    deleteSite(site: AssetSiteDesc): void {
        this.genericStorageService
            .deleteDocument(
                AssetConstants.ASSET_SITES_APP_DOC_NAME,
                site._id,
                site._rev,
            )
            .subscribe(() => this.loadSites());
    }

    openManageSitesDialog(site: AssetSiteDesc): void {
        const dialogRef = this.dialogService.open(ManageSiteDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage site'),
            width: '50vw',
            data: {
                site,
                locationConfig: this.locationConfig,
            },
        });

        dialogRef.afterClosed().subscribe(reload => {
            if (reload) {
                this.loadSites();
            }
        });
    }
}
