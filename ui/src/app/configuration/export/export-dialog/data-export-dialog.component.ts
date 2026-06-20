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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    DialogRef,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { DataExportService } from '../data-export.service';
import {
    AssetExportConfiguration,
    ExportConfiguration,
    ExportItem,
} from '@streampipes/platform-services';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import {
    MatSlideToggle,
    MatSlideToggleChange,
} from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

interface SectionAssetConfiguration {
    assetId: string;
    assetName: string;
    items: ExportItem[];
}

type ExportSectionKey =
    | 'adapters'
    | 'dashboards'
    | 'dataViews'
    | 'dataSources'
    | 'dataLakeMeasures'
    | 'files'
    | 'pipelines'
    | 'labels'
    | 'sites';

@Component({
    selector: 'sp-data-export-dialog',
    templateUrl: './data-export-dialog.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatProgressSpinner,
        MatDivider,
        MatButton,
        MatCheckbox,
        MatSlideToggle,
        FormsModule,
        MatFormField,
        MatSelect,
        MatOption,
        SplitSectionComponent,
        TranslatePipe,
        SpAlertBannerComponent,
    ],
})
export class SpDataExportDialogComponent implements OnInit {
    private dialogRef =
        inject<DialogRef<SpDataExportDialogComponent>>(DialogRef);
    private dataExportService = inject(DataExportService);

    @Input()
    selectedAssets: string[];

    @Input()
    referencedLabels: Record<string, ExportItem[]> = {};

    @Input()
    referencedSites: Record<string, ExportItem[]> = {};

    exportSections = [
        { key: 'adapters', title: 'Adapters' },
        { key: 'dashboards', title: 'Dashboards' },
        { key: 'dataViews', title: 'Charts' },
        { key: 'dataSources', title: 'Data Streams' },
        { key: 'dataLakeMeasures', title: 'Data Lake Storage' },
        { key: 'files', title: 'Files' },
        { key: 'pipelines', title: 'Pipelines' },
        { key: 'labels', title: 'Labels' },
        { key: 'sites', title: 'Sites' },
    ] as const;

    preview: ExportConfiguration;
    exportInProgress = false;
    exportAllSelections: Record<string, boolean> = {};
    sectionAssets: Record<string, SectionAssetConfiguration[]> = {};
    selectedItemResourceIds: Record<string, string[]> = {};

    ngOnInit(): void {
        this.dataExportService
            .getExportPreview(this.selectedAssets)
            .subscribe(preview => {
                this.preview = preview;
                this.addReferencedAssetDocuments(this.preview);
                this.sortPreviewItems();
                this.initializeSectionAssets();
                this.initializeExportAllSelections();
            });
    }

    getExportItems(
        config: AssetExportConfiguration,
        key: (typeof this.exportSections)[number]['key'],
    ): ExportItem[] {
        return config[key];
    }

    getAssetsForSection(key: ExportSectionKey): SectionAssetConfiguration[] {
        return this.sectionAssets[key] ?? [];
    }

    selectAllItems(
        items: ExportItem[],
        select: boolean,
        selectionKey?: string,
    ): void {
        items.forEach(item => (item.selected = select));

        if (selectionKey) {
            this.selectedItemResourceIds[selectionKey] = select
                ? items.map(item => item.resourceId)
                : [];
        }
    }

    selectAllItemsForAsset(
        key: ExportSectionKey,
        assetConfig: SectionAssetConfiguration,
        select: boolean,
    ): void {
        this.selectAllItems(
            assetConfig.items,
            select,
            this.getExportAllSelectionKey(key, assetConfig),
        );
    }

    isExportAllSelected(
        key: ExportSectionKey,
        assetConfig: SectionAssetConfiguration,
    ): boolean {
        return this.exportAllSelections[
            this.getExportAllSelectionKey(key, assetConfig)
        ];
    }

    changeExportAllSelection(
        key: ExportSectionKey,
        assetConfig: SectionAssetConfiguration,
        event: MatSlideToggleChange,
    ): void {
        const selectionKey = this.getExportAllSelectionKey(key, assetConfig);
        this.exportAllSelections[selectionKey] = event.checked;

        if (event.checked) {
            this.selectAllItems(assetConfig.items, true, selectionKey);
        }
    }

    getGenericStorageAppDocTypes(): ExportItem[] {
        return this.preview?.genericStorageAppDocTypes ?? [];
    }

    getSelectedItemResourceIds(
        key: ExportSectionKey,
        assetConfig: SectionAssetConfiguration,
    ): string[] {
        return (
            this.selectedItemResourceIds[
                this.getExportAllSelectionKey(key, assetConfig)
            ] ?? []
        );
    }

    updateSelectedItems(
        key: ExportSectionKey,
        assetConfig: SectionAssetConfiguration,
        selectedResourceIds: string[],
    ): void {
        const selectionKey = this.getExportAllSelectionKey(key, assetConfig);
        this.selectedItemResourceIds[selectionKey] = selectedResourceIds ?? [];

        const items = assetConfig.items;
        items.forEach(exportItem => {
            exportItem.selected = (selectedResourceIds ?? []).includes(
                exportItem.resourceId,
            );
        });
    }

    changeItem(event: MatCheckboxChange, exportItem: ExportItem): void {
        exportItem.selected = event.checked;
    }

    close(): void {
        this.dialogRef.close();
    }

    generateDownloadPackage(): void {
        this.exportInProgress = true;
        this.dataExportService.triggerExport(this.preview).subscribe(result => {
            this.downloadFile(result);
        });
    }

    downloadFile(data: any) {
        const blob = new Blob([data], { type: 'application/zip' });
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');

        anchor.href = url;
        anchor.download = 'data_export';
        anchor.style.display = 'none';
        document.body.appendChild(anchor);

        anchor.click();

        window.URL.revokeObjectURL(url);
        this.dialogRef.close();
    }

    private initializeExportAllSelections(): void {
        this.exportSections.forEach(section => {
            this.getAssetsForSection(section.key).forEach(assetConfig => {
                const selectionKey = this.getExportAllSelectionKey(
                    section.key,
                    assetConfig,
                );
                this.exportAllSelections[selectionKey] = true;
                this.selectAllItems(assetConfig.items, true, selectionKey);
            });
        });
    }

    private initializeSectionAssets(): void {
        this.sectionAssets = {};

        this.exportSections.forEach(section => {
            this.sectionAssets[section.key] =
                this.preview.assetExportConfiguration
                    .map(config => ({
                        assetId: config.assetId,
                        assetName: config.assetName,
                        items: this.getExportItems(config, section.key),
                    }))
                    .filter(config => config.items.length > 0);
        });
    }

    private sortPreviewItems(): void {
        this.preview.assetExportConfiguration.forEach(config => {
            this.exportSections.forEach(section => {
                this.sortExportItems(config[section.key]);
            });
        });

        this.sortExportItems(this.preview.genericStorageAppDocTypes);
    }

    private sortExportItems(items: ExportItem[]): void {
        items.sort((left, right) => left.label.localeCompare(right.label));
    }

    private getExportAllSelectionKey(
        key: ExportSectionKey,
        assetConfig: SectionAssetConfiguration,
    ): string {
        return `${key}::${assetConfig.assetId}`;
    }

    private addReferencedAssetDocuments(preview: ExportConfiguration): void {
        preview.assetExportConfiguration?.forEach(assetExportConfig => {
            assetExportConfig.labels = this.mergeExportItems(
                assetExportConfig.labels,
                this.referencedLabels[assetExportConfig.assetId] ?? [],
            );
            assetExportConfig.sites = this.mergeExportItems(
                assetExportConfig.sites,
                this.referencedSites[assetExportConfig.assetId] ?? [],
            );
        });
    }

    private mergeExportItems(
        existingItems: ExportItem[] = [],
        newItems: ExportItem[],
    ): ExportItem[] {
        const existingItemIds = new Set(
            existingItems.map(item => item.resourceId),
        );
        const missingItems = newItems.filter(
            item => !existingItemIds.has(item.resourceId),
        );

        return [...existingItems, ...missingItems];
    }
}
