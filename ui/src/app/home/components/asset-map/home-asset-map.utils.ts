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

import {
    AssetSiteDesc,
    LatLng,
    SpAssetModel,
} from '@streampipes/platform-services';
import { latLngBounds } from 'leaflet';
import { AssetClusterFeature, AssetPointFeature } from './home-asset-map.types';

export function buildClusterPoints(
    assets: SpAssetModel[],
    sites: Record<string, AssetSiteDesc>,
): AssetPointFeature[] {
    const points: AssetPointFeature[] = [];

    assets.forEach(asset => {
        const siteId = asset.assetSite?.siteId;
        if (!siteId) {
            return;
        }

        const site = sites[siteId];
        const coordinates =
            asset.assetSite?.hasExactLocation &&
            hasValidCoordinates(asset.assetSite.location?.coordinates)
                ? asset.assetSite.location.coordinates
                : site?.location?.coordinates;

        if (!hasValidCoordinates(coordinates)) {
            return;
        }

        points.push({
            type: 'Feature',
            properties: {
                asset,
                site,
            },
            geometry: {
                type: 'Point',
                coordinates: [coordinates.longitude, coordinates.latitude],
            },
        });
    });

    return points;
}

export function hasValidCoordinates(
    coordinates: LatLng | undefined | null,
): coordinates is LatLng {
    return (
        coordinates !== null &&
        coordinates !== undefined &&
        Number.isFinite(coordinates.latitude) &&
        Number.isFinite(coordinates.longitude)
    );
}

export function buildAssetBounds(points: AssetPointFeature[]) {
    if (points.length === 0) {
        return undefined;
    }

    const latitudes = points.map(point => point.geometry.coordinates[1]);
    const longitudes = points.map(point => point.geometry.coordinates[0]);

    return latLngBounds(
        [Math.min(...latitudes), Math.min(...longitudes)],
        [Math.max(...latitudes), Math.max(...longitudes)],
    );
}

export function isClusterFeature(
    feature: AssetClusterFeature | AssetPointFeature,
): feature is AssetClusterFeature {
    return Boolean((feature.properties as { cluster?: boolean }).cluster);
}

export function shouldSpiderfy(
    leaves: AssetPointFeature[],
    expansionZoom: number,
    currentZoom: number,
    maxZoom: number,
): boolean {
    if (leaves.length <= 1 || currentZoom < maxZoom) {
        return false;
    }

    return (
        expansionZoom > maxZoom ||
        leaves.every(leaf =>
            hasSameCoordinates(
                leaf.geometry.coordinates,
                leaves[0].geometry.coordinates,
            ),
        )
    );
}

function hasSameCoordinates(left: number[], right: number[]): boolean {
    return left[0] === right[0] && left[1] === right[1];
}
