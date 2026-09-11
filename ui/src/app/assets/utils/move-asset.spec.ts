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

import { describe, expect, it } from 'vitest';
import { SpAssetModel } from '@streampipes/platform-services';
import { moveAssetToParent } from './move-asset';

describe('moveAssetToParent', () => {
    it('keeps asset links and asset information on the moved asset', () => {
        const assetToMove = {
            assetId: 'pump-1',
            assetName: 'Pump 1',
            assetDescription: 'Primary feed pump',
            assetLinks: [
                {
                    linkName: 'Pump documentation',
                    linkUrl: 'https://example.org/pump-1',
                },
            ],
            assetType: {
                assetTypeLabel: 'Pump',
                assetTypeCategory: 'Machine',
            },
            assetSite: {
                siteId: 'plant-a',
                area: 'Production',
                hasExactLocation: true,
                location: { coordinates: [8.1, 49.1] },
            },
            additionalData: {
                customFields: [{ key: 'Manufacturer', value: 'Acme' }],
            },
            labelIds: ['critical'],
            assets: [
                {
                    assetId: 'pump-1-motor',
                    assetName: 'Pump 1 motor',
                    assetLinks: [],
                    assets: [],
                },
            ],
        } as SpAssetModel;
        const targetAsset = {
            assetId: 'line-1',
            assetName: 'Line 1',
            assets: [
                {
                    assetId: 'line-1-station-1',
                    assetName: 'Station 1',
                    assetLinks: [],
                    assets: [],
                },
            ],
        } as SpAssetModel;

        const moved = moveAssetToParent(
            targetAsset,
            'line-1-station-1',
            assetToMove,
        );

        const movedAsset = targetAsset.assets[0].assets[0];
        expect(moved).toBe(true);
        expect(movedAsset.assetLinks).toEqual(assetToMove.assetLinks);
        expect(movedAsset.assetDescription).toBe('Primary feed pump');
        expect(movedAsset.assetType).toEqual(assetToMove.assetType);
        expect(movedAsset.assetSite).toEqual(assetToMove.assetSite);
        expect(movedAsset.additionalData).toEqual(assetToMove.additionalData);
        expect(movedAsset.labelIds).toEqual(['critical']);
        expect(movedAsset.assets).toEqual(assetToMove.assets);
    });

    it('removes the moved asset site when requested', () => {
        const assetToMove = {
            assetId: 'pump-1',
            assetName: 'Pump 1',
            assetSite: { siteId: 'plant-a' },
            assets: [],
        } as SpAssetModel;
        const targetAsset = {
            assetId: 'line-1',
            assetName: 'Line 1',
            assets: [],
        } as SpAssetModel;

        moveAssetToParent(targetAsset, 'line-1', assetToMove, true);

        expect(assetToMove.assetSite).toBeUndefined();
        expect(targetAsset.assets).toContain(assetToMove);
    });
});
