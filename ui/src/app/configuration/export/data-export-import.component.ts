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

import { Component, OnInit, inject } from '@angular/core';
import {
    DialogService,
    PanelType,
    SpBasicNavTabsComponent,
    SpBreadcrumbService,
    SplitSectionComponent,
    SpNavigationItem,
} from '@streampipes/shared-ui';
import { SpConfigurationRoutes } from '../configuration.breadcrumb';
import { SpConfigurationTabsService } from '../configuration-tabs.service';
import {
    AssetConstants,
    AssetManagementService,
    AssetSiteDesc,
    ExportItem,
    GenericStorageService,
    LabelsService,
    SpAsset,
    SpAssetModel,
    SpLabel,
} from '@streampipes/platform-services';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { SpDataExportDialogComponent } from './export-dialog/data-export-dialog.component';
import { SpDataImportDialogComponent } from './import-dialog/data-import-dialog.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface AssetReferenceExportItems {
    referencedLabels: Record<string, ExportItem[]>;
    referencedSites: Record<string, ExportItem[]>;
}

@Component({
    selector: 'sp-data-export-import',
    templateUrl: './data-export-import.component.html',
    styleUrls: ['./data-export-import.component.scss'],
    imports: [
        SpBasicNavTabsComponent,
        LayoutDirective,
        FlexDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        SplitSectionComponent,
        MatCheckbox,
        MatButton,
        TranslatePipe,
    ],
})
export class SpDataExportImportComponent implements OnInit {
    private breadcrumbService = inject(SpBreadcrumbService);
    private assetManagementService = inject(AssetManagementService);
    private genericStorageService = inject(GenericStorageService);
    private labelsService = inject(LabelsService);
    private dialogService = inject(DialogService);
    private tabService = inject(SpConfigurationTabsService);
    private translateService = inject(TranslateService);

    tabs: SpNavigationItem[] = [];

    assets: SpAssetModel[] = [];
    selectedAssets: string[] = [];

    ngOnInit(): void {
        this.tabs = this.tabService.getTabs();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('export') },
        ]);
        this.loadAssets();
    }

    loadAssets(): void {
        this.assetManagementService
            .getAllAssets()
            .subscribe(
                assets =>
                    (this.assets = assets.sort((a, b) =>
                        a.assetName.localeCompare(b.assetName),
                    )),
            );
    }

    handleSelectionChange(event: MatCheckboxChange, assetId: string) {
        if (event.checked) {
            this.selectedAssets.push(assetId);
        } else {
            this.selectedAssets.splice(this.selectedAssets.indexOf(assetId), 1);
        }
    }

    selectAllAssets(select: boolean): void {
        this.selectedAssets = select
            ? this.assets.map(asset => asset.elementId)
            : [];
    }

    isSelected(assetId: string): boolean {
        return this.selectedAssets.includes(assetId);
    }

    openExportDialog(): void {
        this.getReferencedAssetDocuments().subscribe(
            referencedAssetDocuments => {
                this.openExportPreviewDialog(referencedAssetDocuments);
            },
        );
    }

    openExportPreviewDialog(
        referencedAssetDocuments: AssetReferenceExportItems,
    ): void {
        const dialogRef = this.dialogService.open(SpDataExportDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Export resources'),
            width: '50vw',
            data: {
                selectedAssets: this.selectedAssets,
                referencedLabels: referencedAssetDocuments.referencedLabels,
                referencedSites: referencedAssetDocuments.referencedSites,
            },
        });

        dialogRef.afterClosed().subscribe(() => {});
    }

    openImportDialog(): void {
        const dialogRef = this.dialogService.open(SpDataImportDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Import resources'),
            width: '50vw',
            data: {},
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === 'import') {
                this.loadAssets();
            }
        });
    }

    private getReferencedAssetDocuments(): Observable<AssetReferenceExportItems> {
        return forkJoin({
            assets: this.assetManagementService.getAllAssets(),
            labels: this.labelsService.getAllLabels(),
            sites: this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_SITES_APP_DOC_NAME,
            ),
        }).pipe(
            map(({ assets, labels, sites }) =>
                this.toReferencedAssetDocuments(
                    assets.filter(asset =>
                        this.selectedAssets.includes(asset.elementId),
                    ),
                    labels,
                    sites as AssetSiteDesc[],
                ),
            ),
        );
    }

    private toReferencedAssetDocuments(
        assets: SpAssetModel[],
        labels: SpLabel[],
        sites: AssetSiteDesc[],
    ): AssetReferenceExportItems {
        const labelsById = new Map(
            labels.filter(label => label._id).map(label => [label._id!, label]),
        );
        const sitesById = new Map(
            sites.filter(site => site._id).map(site => [site._id, site]),
        );
        const referencedLabels: Record<string, ExportItem[]> = {};
        const referencedSites: Record<string, ExportItem[]> = {};

        assets.forEach(asset => {
            const labelIds = new Set<string>();
            const siteIds = new Set<string>();
            this.collectAssetReferences(asset, labelIds, siteIds);

            referencedLabels[asset.elementId] = [...labelIds]
                .map(labelId => labelsById.get(labelId))
                .filter((label): label is SpLabel => label !== undefined)
                .map(label => ({
                    resourceId: label._id!,
                    label: label.label,
                    selected: true,
                }));
            referencedSites[asset.elementId] = [...siteIds]
                .map(siteId => sitesById.get(siteId))
                .filter((site): site is AssetSiteDesc => site !== undefined)
                .map(site => ({
                    resourceId: site._id,
                    label: site.label,
                    selected: true,
                }));
        });

        return { referencedLabels, referencedSites };
    }

    private collectAssetReferences(
        asset: SpAsset,
        labelIds: Set<string>,
        siteIds: Set<string>,
    ): void {
        asset.labelIds?.forEach(labelId => labelIds.add(labelId));

        if (asset.assetSite?.siteId) {
            siteIds.add(asset.assetSite.siteId);
        }

        asset.assets?.forEach(subAsset =>
            this.collectAssetReferences(subAsset, labelIds, siteIds),
        );
    }
}
