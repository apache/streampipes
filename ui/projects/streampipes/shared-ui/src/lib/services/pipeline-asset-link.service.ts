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

import { inject, Injectable } from '@angular/core';
import {
    AssetLink,
    AssetManagementService,
    ChartService,
    DatalakeRestService,
    LinkageData,
    Pipeline,
    SpAsset,
} from '@streampipes/platform-services';
import { firstValueFrom } from 'rxjs';
import { SpAssetBrowserService } from '../components/asset-browser/asset-browser.service';

@Injectable({ providedIn: 'root' })
export class PipelineAssetLinkService {
    private chartService = inject(ChartService);
    private datalakeService = inject(DatalakeRestService);
    private assetService = inject(AssetManagementService);
    private assetBrowserService = inject(SpAssetBrowserService);

    async getLinkageData(
        pipelines: Pick<Pipeline, '_id' | 'name'>[],
    ): Promise<LinkageData[]> {
        const linkageData: LinkageData[] = pipelines.map(pipeline => ({
            type: 'pipeline',
            id: pipeline._id,
            name: pipeline.name,
        }));
        const pipelineIds = new Set(pipelines.map(pipeline => pipeline._id));
        if (pipelineIds.size === 0) {
            return linkageData;
        }

        return [
            ...linkageData,
            ...(await this.getDatasetLinkageData(pipelineIds)),
        ];
    }

    async getDatasetIds(pipelineId: string): Promise<Set<string>> {
        return new Set(
            (await this.getDatasetLinkageData(new Set([pipelineId]))).map(
                link => link.id,
            ),
        );
    }

    async assignNewDatasetsToPipelineAssets(
        pipelineId: string,
        existingDatasetIds: Set<string>,
    ): Promise<void> {
        const datasetLinks = (
            await this.getDatasetLinkageData(new Set([pipelineId]))
        ).filter(link => !existingDatasetIds.has(link.id));
        if (datasetLinks.length === 0) {
            return;
        }

        const assetModels = await firstValueFrom(
            this.assetService.getAllAssets(),
        );
        let assetsChanged = false;
        for (const assetModel of assetModels) {
            if (this.addDatasetLinks(assetModel, pipelineId, datasetLinks)) {
                await firstValueFrom(this.assetService.updateAsset(assetModel));
                assetsChanged = true;
            }
        }
        if (assetsChanged) {
            this.assetBrowserService.refreshBrowserAssetData();
        }
    }

    private async getDatasetLinkageData(
        pipelineIds: Set<string>,
    ): Promise<LinkageData[]> {
        if (pipelineIds.size === 0) {
            return [];
        }
        const streams = await firstValueFrom(
            this.chartService.getAllPersistedDataStreams(),
        );
        const measurementNames = new Set(
            streams
                .filter(stream => pipelineIds.has(stream.pipelineId))
                .map(stream => stream.measureName),
        );
        if (measurementNames.size === 0) {
            return [];
        }

        const measurements = await firstValueFrom(
            this.datalakeService.getAllMeasurementSeries(),
        );
        const linkedIds = new Set<string>();
        return measurements
            .filter(measurement => {
                const include =
                    measurementNames.has(measurement.measureName) &&
                    !linkedIds.has(measurement.elementId);
                if (include) {
                    linkedIds.add(measurement.elementId);
                }
                return include;
            })
            .map(measurement => ({
                type: 'measurement',
                id: measurement.elementId,
                name: measurement.measureName,
            }));
    }

    private addDatasetLinks(
        asset: SpAsset,
        pipelineId: string,
        datasetLinks: LinkageData[],
    ): boolean {
        let changed = false;
        const assetLinks = asset.assetLinks ?? [];
        if (
            assetLinks.some(
                link =>
                    link.linkType === 'pipeline' &&
                    link.resourceId === pipelineId,
            )
        ) {
            const linkedIds = new Set(assetLinks.map(link => link.resourceId));
            const linksToAdd = datasetLinks
                .filter(link => !linkedIds.has(link.id))
                .map(link => this.makeAssetLink(link));
            if (linksToAdd.length > 0) {
                asset.assetLinks = [...assetLinks, ...linksToAdd];
                changed = true;
            }
        }
        for (const child of asset.assets ?? []) {
            changed =
                this.addDatasetLinks(child, pipelineId, datasetLinks) ||
                changed;
        }
        return changed;
    }

    private makeAssetLink(link: LinkageData): AssetLink {
        return {
            editingDisabled: false,
            linkLabel: link.name,
            linkType: link.type,
            navigationActive: true,
            queryHint: link.type,
            resourceId: link.id,
        };
    }
}
