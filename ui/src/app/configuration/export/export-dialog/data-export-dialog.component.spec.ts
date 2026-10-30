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

import { TestBed } from '@angular/core/testing';
import { DialogRef } from '@streampipes/shared-ui';
import {
    AssetExportConfiguration,
    ExportConfiguration,
} from '@streampipes/platform-services';
import { of } from 'rxjs';
import { describe, expect, it } from 'vitest';
import { DataExportService } from '../data-export.service';
import { SpDataExportDialogComponent } from './data-export-dialog.component';

function asset(assetId: string, assetName: string): AssetExportConfiguration {
    return Object.assign(new AssetExportConfiguration(), {
        assetId,
        assetName,
        adapters: [],
        dashboards: [],
        dataViews: [],
        dataSources: [],
        dataLakeMeasures: [],
        files: [],
        pipelines: [],
        labels: [],
        sites: [],
        genericStorageDocuments: [],
    });
}

function component(preview: ExportConfiguration): SpDataExportDialogComponent {
    TestBed.configureTestingModule({
        providers: [
            { provide: DialogRef, useValue: {} },
            {
                provide: DataExportService,
                useValue: { getExportPreview: () => of(preview) },
            },
        ],
    });
    return TestBed.runInInjectionContext(
        () => new SpDataExportDialogComponent(),
    );
}

function preview(assets: AssetExportConfiguration[]): ExportConfiguration {
    return Object.assign(new ExportConfiguration(), {
        assetExportConfiguration: assets,
        genericStorageAppDocTypes: [],
        genericStorageDocuments: [
            {
                resourceId: 'shared',
                label: 'Shared pipeline',
                category: 'AI Pipelines',
                selected: false,
            },
            {
                resourceId: 'unrelated',
                label: 'Other pipeline',
                category: 'AI Pipelines',
                selected: false,
            },
        ],
    });
}

describe('generic export asset rows', () => {
    it('shows named asset rows with independent selections for a shared resource', () => {
        const config = preview([asset('a', 'Asset A'), asset('b', 'Asset B')]);
        const dialog = component(config);
        dialog.selectedAssets = ['a', 'b'];
        for (const assetConfig of config.assetExportConfiguration) {
            assetConfig.genericStorageDocuments = [
                { ...config.genericStorageDocuments[0] },
            ];
        }
        config.genericStorageDocuments = [];
        dialog.ngOnInit();

        const rows = dialog.getAssetsForSection('genericStorage::AI Pipelines');
        expect(rows.map(row => row.assetName)).toEqual(['Asset A', 'Asset B']);
        expect(rows.map(row => row.items.map(item => item.resourceId))).toEqual(
            [['shared'], ['shared']],
        );
        expect(config.genericStorageDocuments).toEqual([]);
        dialog.updateSelectedItems('genericStorage::AI Pipelines', rows[0], []);
        expect(
            config.assetExportConfiguration[0].genericStorageDocuments[0]
                .selected,
        ).toBe(false);
        expect(
            config.assetExportConfiguration[1].genericStorageDocuments[0]
                .selected,
        ).toBe(true);
    });

    it('retains the global category when no assets are selected', () => {
        const config = preview([]);
        const dialog = component(config);
        dialog.selectedAssets = [];
        dialog.ngOnInit();
        expect(
            dialog.getAssetsForSection('genericStorage::AI Pipelines')[0].items,
        ).toHaveLength(2);
        expect(config.genericStorageDocuments).toHaveLength(2);
    });
});
