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

import { Component, Input, OnInit, ViewChild, inject } from '@angular/core';
import {
    AssetConstants,
    AssetSiteDesc,
    GenericStorageService,
    LocationConfig,
} from '@streampipes/platform-services';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { ManageSiteDialogComponent } from '../../dialog/manage-site/manage-site-dialog.component';
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
    SplitSectionComponent,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatTooltip } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';

interface AssetWithSite {
    assetSite?: {
        siteId?: string;
    };
    assets?: AssetWithSite[];
}

@Component({
    selector: 'sp-site-area-configuration',
    templateUrl: './site-area-configuration.component.html',
    imports: [
        SplitSectionComponent,
        MatButton,
        MatIcon,
        SpTableComponent,
        MatSort,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatSortHeader,
        MatCellDef,
        MatCell,
        LayoutDirective,
        LayoutAlignDirective,
        MatIconButton,
        MatTooltip,
        TranslatePipe,
    ],
})
export class SiteAreaConfigurationComponent implements OnInit {
    private genericStorageService = inject(GenericStorageService);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private dialog = inject(MatDialog);

    @Input()
    locationConfig: LocationConfig;

    allSites: AssetSiteDesc[] = [];
    dataSource: MatTableDataSource<AssetSiteDesc> =
        new MatTableDataSource<AssetSiteDesc>();

    allUsedSiteIds: string[] = [];

    @ViewChild(MatSort)
    sort: MatSort;
    displayedColumns = ['name', 'areas', 'actions'];

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

    extractSiteIds(assets: AssetWithSite[]): string[] {
        const allSiteIds = new Set<string>();

        const extractSiteFromAsset = (asset: AssetWithSite) => {
            if (asset.assetSite?.siteId) {
                allSiteIds.add(asset.assetSite.siteId);
            }
            asset.assets?.forEach(subAsset => extractSiteFromAsset(subAsset));
        };

        assets.forEach(asset => extractSiteFromAsset(asset));

        return Array.from(allSiteIds);
    }

    deleteSite(site: AssetSiteDesc): void {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_APP_DOC_NAME)
            .subscribe(res => {
                this.allUsedSiteIds = this.extractSiteIds(res);

                if (this.allUsedSiteIds.includes(site._id)) {
                    this.showSiteInUseWarning();
                } else {
                    this.showDeleteSiteDialog(site);
                }
            });
    }

    showSiteInUseWarning(): void {
        this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant('Site is still in use'),
                subtitle: this.translateService.instant(
                    'To delete a site, please remove the site from all assets.',
                ),
                confirmTitle: this.translateService.instant('Ok'),
            },
        });
    }

    showDeleteSiteDialog(site: AssetSiteDesc): void {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to delete this site?',
                ),
                subtitle: this.translateService.instant(
                    'This action cannot be reversed!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                confirmTitle: this.translateService.instant('Delete site'),
            },
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === 'confirm') {
                this.deleteSiteDocument(site);
            }
        });
    }

    deleteSiteDocument(site: AssetSiteDesc): void {
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
