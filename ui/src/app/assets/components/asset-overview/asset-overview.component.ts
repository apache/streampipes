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

import { Component, inject, OnInit, ViewChild } from '@angular/core';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import {
    AssetManagementService,
    SpAssetModel,
} from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    CurrentUserService,
    DialogService,
    ObjectPermissionDialogComponent,
    PanelType,
    SpAssetBrowserService,
    SpBasicHeaderTitleComponent,
    SpBasicViewComponent,
    SpBreadcrumbService,
    SpTableActionsDirective,
    SpTableComponent,
} from '@streampipes/shared-ui';
import { SpAssetRoutes } from '../../assets.breadcrumb';
import { Router } from '@angular/router';
import { SpCreateAssetDialogComponent } from '../../dialog/create-asset/create-asset-dialog.component';
import { IdGeneratorService } from '../../../core-services/id-generator/id-generator.service';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatMenuItem } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';

@Component({
    selector: 'sp-asset-overview',
    templateUrl: './asset-overview.component.html',
    styleUrls: ['./asset-overview.component.scss'],
    imports: [
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        MatButton,
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
        SpTableActionsDirective,
        MatMenuItem,
        MatIcon,
        TranslatePipe,
    ],
})
export class SpAssetOverviewComponent implements OnInit {
    private assetService = inject(AssetManagementService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private dialogService = inject(DialogService);
    private router = inject(Router);
    private idGeneratorService = inject(IdGeneratorService);
    private assetBrowserService = inject(SpAssetBrowserService);
    private currentUserService = inject(CurrentUserService);
    private dialog = inject(MatDialog);
    private translateService = inject(TranslateService);

    existingAssets: SpAssetModel[] = [];
    filteredAssets: SpAssetModel[] = [];

    displayedColumns: string[] = ['assetName', 'actions'];

    @ViewChild(MatSort)
    sort: MatSort;

    dataSource: MatTableDataSource<SpAssetModel> =
        new MatTableDataSource<SpAssetModel>();

    hasWritePrivilege = false;

    assetFilter$: Subscription;
    currentFilterIds = new Set<string>();

    private assetFilterService = inject(SpAssetBrowserService);

    ngOnInit(): void {
        this.hasWritePrivilege = this.currentUserService.hasRole(
            UserPrivilege.PRIVILEGE_WRITE_ASSETS,
        );
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.getRootLink(SpAssetRoutes.BASE),
        );
        this.assetFilter$ =
            this.assetFilterService.currentAssetFilter$.subscribe(filter => {
                const elementIdsSet = new Set<string>();

                if (
                    filter?.selectedAssets &&
                    Array.isArray(filter.selectedAssets)
                ) {
                    filter.selectedAssets.forEach(asset => {
                        if ((asset as SpAssetModel)?.elementId) {
                            elementIdsSet.add(
                                (asset as SpAssetModel)?.elementId,
                            );
                        }
                    });
                }

                this.currentFilterIds =
                    elementIdsSet.size > 0 ? elementIdsSet : undefined;

                this.applyAssetFilters(this.currentFilterIds);
            });

        this.loadAssets();
    }

    loadAssets(): void {
        this.assetService.getAllAssets().subscribe(result => {
            this.existingAssets = (result as SpAssetModel[]).sort((a, b) =>
                a.assetName.localeCompare(b.assetName),
            );
            this.dataSource.sort = this.sort;
            this.dataSource.data = this.existingAssets;
        });
    }

    applyAssetFilters(elementIds: Set<string>): void {
        if (elementIds == undefined) {
            this.filteredAssets = [];
        } else if (elementIds.size == 0) {
            this.filteredAssets = this.existingAssets;
        } else {
            this.filteredAssets = this.existingAssets.filter(a =>
                elementIds.has(a.elementId),
            );
        }
        this.dataSource.sort = this.sort;
        this.dataSource.data = this.filteredAssets;
    }

    createNewAsset() {
        const assetModel: SpAssetModel = {
            assetName: 'New Asset',
            assetDescription: '',
            assetLinks: [],
            assetId: this.idGeneratorService.generate(6),
            elementId: this.idGeneratorService.generate(24),
            appDocType: 'asset-management',
            removable: true,
            rev: undefined,
            assets: [],
            assetType: undefined,
            assetSite: undefined,
            additionalData: {},
            labelIds: [],
        };
        const dialogRef = this.dialogService.open(
            SpCreateAssetDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Create asset'),
                width: '50vw',
                data: {
                    assetModel: assetModel,
                },
            },
        );

        dialogRef.afterClosed().subscribe(ev => {
            if (ev) {
                this.loadAssets();
                this.assetBrowserService.refreshBrowserAssetData();
                this.goToDetailsView(assetModel, true);
            }
        });
    }

    goToDetailsView(asset: SpAssetModel, editMode = false) {
        const mode = editMode && this.hasWritePrivilege ? 'edit' : 'view';
        this.router.navigate(['assets', 'details', asset.elementId, mode]);
    }

    deleteAsset(asset: SpAssetModel) {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to delete this asset?',
                ),
                subtitle: this.translateService.instant(
                    'This action cannot be reversed!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                confirmTitle: this.translateService.instant('Delete Asset'),
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result === 'confirm') {
                this.assetService.deleteAsset(asset.elementId).subscribe(() => {
                    this.loadAssets();
                    this.assetBrowserService.refreshBrowserAssetData();
                });
            }
        });
    }

    openPermissionsDialog(asset: SpAssetModel) {
        const dialogRef = this.dialogService.open(
            ObjectPermissionDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Manage permissions'),
                width: '70vw',
                data: {
                    objectInstanceId: asset.elementId,
                    headerTitle:
                        this.translateService.instant(
                            'Manage permissions for asset ',
                        ) + asset.assetName,
                },
            },
        );
    }
}
