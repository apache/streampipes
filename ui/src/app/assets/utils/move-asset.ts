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

import { SpAsset, SpAssetModel } from '@streampipes/platform-services';

export function moveAssetToParent(
    targetAsset: SpAssetModel,
    targetParentAssetId: string,
    assetToMove: SpAsset,
    removeMovedAssetSite = false,
): boolean {
    const targetParent = findAssetById(targetAsset, targetParentAssetId);
    if (!targetParent) {
        return false;
    }

    targetParent.assets ??= [];
    if (removeMovedAssetSite) {
        assetToMove.assetSite = undefined;
    }
    targetParent.assets.push(assetToMove);
    return true;
}

export function removeAssetFromParent(
    sourceAsset: SpAsset,
    assetId: string,
): boolean {
    const children = sourceAsset.assets ?? [];
    const assetIndex = children.findIndex(asset => asset.assetId === assetId);
    if (assetIndex >= 0) {
        children.splice(assetIndex, 1);
        return true;
    }

    return children.some(child => removeAssetFromParent(child, assetId));
}

function findAssetById(asset: SpAsset, assetId: string): SpAsset | undefined {
    if (asset.assetId === assetId) {
        return asset;
    }

    for (const child of asset.assets ?? []) {
        const foundAsset = findAssetById(child, assetId);
        if (foundAsset) {
            return foundAsset;
        }
    }

    return undefined;
}
