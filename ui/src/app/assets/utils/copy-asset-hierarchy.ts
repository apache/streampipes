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
import cloneDeep from 'lodash.clonedeep';

/** Creates an independent hierarchy draft without resource links. */
export function copyAssetHierarchy(
    source: SpAssetModel,
    generateId: (length: number) => string,
): SpAssetModel {
    const copy = cloneDeep(source);
    const nodes: SpAsset[] = [];
    const collect = (asset: SpAsset): void => {
        nodes.push(asset);
        asset.assets?.forEach(collect);
    };
    collect(copy);
    const usedIds = new Set([
        source.elementId,
        ...nodes.map(asset => asset.assetId),
    ]);
    const freshId = (length: number): string => {
        let id: string;
        do {
            id = generateId(length);
        } while (usedIds.has(id));
        usedIds.add(id);
        return id;
    };
    copy.elementId = freshId(24);
    copy.rev = undefined;
    nodes.forEach(asset => {
        asset.assetId = freshId(6);
        asset.assetLinks = [];
    });
    return copy;
}
