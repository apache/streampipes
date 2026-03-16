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

import { Injectable } from '@angular/core';
import { SpAsset, SpLabel } from '@streampipes/platform-services';
import { AssetBrowserData } from '../../asset-browser/asset-browser.model';
import {
    SpTableAssetContextValue,
    SpTableResolvedAssetContext,
} from '../sp-table.model';

@Injectable({ providedIn: 'root' })
export class SpTableAssetContextService {
    buildAssetContextIndex(
        assetData?: AssetBrowserData,
    ): Map<string, Map<string, SpTableResolvedAssetContext>> {
        const index = new Map<
            string,
            Map<string, SpTableResolvedAssetContext>
        >();
        if (!assetData) {
            return index;
        }

        const sitesById = new Map(
            assetData.sites.map(site => [site._id, site.label]),
        );
        const labelsById = new Map(
            assetData.labels
                .filter(
                    (label): label is SpLabel & { _id: string } => !!label._id,
                )
                .map(label => [label._id, label]),
        );

        assetData.assets.forEach(asset =>
            this.collectAssetContexts(
                asset,
                index,
                sitesById,
                labelsById,
                asset.assetId,
                asset.assetName,
                [],
                [],
                null,
            ),
        );

        return index;
    }

    private collectAssetContexts(
        asset: SpAsset,
        index: Map<string, Map<string, SpTableResolvedAssetContext>>,
        sitesById: Map<string, string>,
        labelsById: Map<string, SpLabel>,
        topLevelAssetId: string,
        topLevelAssetLabel: string,
        hierarchy: string[],
        inheritedLabels: SpLabel[],
        inheritedSiteLabel: string | null,
    ): void {
        const currentHierarchy = [...hierarchy, asset.assetName].filter(
            Boolean,
        );
        const currentLabels = this.mergeLabels(
            inheritedLabels,
            (asset.labelIds ?? [])
                .map(labelId => labelsById.get(labelId))
                .filter((label): label is SpLabel => !!label),
        );
        const siteLabel =
            (asset.assetSite?.siteId &&
                sitesById.get(asset.assetSite.siteId)) ??
            asset.assetSite?.area ??
            inheritedSiteLabel;

        (asset.assetLinks ?? []).forEach(link => {
            const contextsByResource =
                index.get(link.linkType) ??
                new Map<string, SpTableResolvedAssetContext>();
            const currentContext =
                contextsByResource.get(link.resourceId) ??
                new SpTableResolvedAssetContext();

            currentContext.assets = this.uniqueBy(
                [
                    ...currentContext.assets,
                    new SpTableAssetContextValue(
                        topLevelAssetId,
                        topLevelAssetLabel,
                        currentHierarchy.join(' / '),
                    ),
                ],
                item => item.id,
            );
            currentContext.sites = this.uniqueBy(
                siteLabel
                    ? [
                          ...currentContext.sites,
                          new SpTableAssetContextValue(
                              asset.assetSite?.siteId ?? siteLabel,
                              siteLabel,
                          ),
                      ]
                    : currentContext.sites,
                item => item.id,
            );
            currentContext.labels = this.uniqueBy(
                [...currentContext.labels, ...currentLabels],
                label => label._id ?? label.label,
            );
            currentContext.sortValue = [
                currentContext.sites.map(site => site.label).join(' '),
                currentContext.assets
                    .map(assetItem => assetItem.label)
                    .join(' '),
                currentContext.labels.map(label => label.label).join(' '),
            ].join(' ');

            contextsByResource.set(link.resourceId, currentContext);
            index.set(link.linkType, contextsByResource);
        });

        (asset.assets ?? []).forEach(child =>
            this.collectAssetContexts(
                child,
                index,
                sitesById,
                labelsById,
                topLevelAssetId,
                topLevelAssetLabel,
                currentHierarchy,
                currentLabels,
                siteLabel,
            ),
        );
    }

    private mergeLabels(base: SpLabel[], additional: SpLabel[]): SpLabel[] {
        return this.uniqueBy(
            [...base, ...additional],
            label => label._id ?? label.label,
        );
    }

    private uniqueBy<T>(
        items: T[],
        getKey: (item: T) => string | undefined,
    ): T[] {
        const seen = new Set<string>();
        return items.filter(item => {
            const key = getKey(item);
            if (!key || seen.has(key)) {
                return false;
            }

            seen.add(key);
            return true;
        });
    }
}
