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

import { LatLng } from '@streampipes/platform-services';
import {
    divIcon,
    icon,
    LatLng as LeafletLatLng,
    Map as LeafletMap,
    marker,
    Marker,
    point as leafletPoint,
    polyline,
} from 'leaflet';

export function createClusterMarker(
    location: LatLng,
    assetCount: number,
): Marker {
    return marker(
        { lat: location.latitude, lng: location.longitude },
        {
            icon: divIcon({
                className: 'sp-map-cluster-marker',
                html: `<span>${assetCount}</span>`,
                iconSize: [36, 36],
                iconAnchor: [18, 18],
            }),
        },
    );
}

export function createAssetMarker(location: LatLng): Marker {
    return marker(
        { lat: location.latitude, lng: location.longitude },
        {
            icon: icon({
                iconSize: [25, 41],
                iconAnchor: [13, 41],
                iconUrl: 'assets/img/marker-icon.png',
                shadowUrl: 'assets/img/marker-shadow.png',
            }),
        },
    );
}

export function createSpiderfyLeg(
    origin: LeafletLatLng,
    target: LeafletLatLng,
) {
    return polyline([origin, target], {
        className: 'sp-map-spiderfy-leg',
        interactive: false,
    });
}

export function createSpiderfyMarker(
    position: LeafletLatLng,
    index: number,
): Marker {
    return marker(position, {
        icon: divIcon({
            className: 'sp-map-spiderfy-marker',
            html: `<span>${index + 1}</span>`,
            iconSize: [30, 30],
            iconAnchor: [15, 15],
        }),
    });
}

export function getSpiderfyLatLng(
    map: LeafletMap,
    center: LeafletLatLng,
    index: number,
    total: number,
): LeafletLatLng {
    const centerPoint = map.latLngToLayerPoint(center);
    const radius = Math.max(48, Math.min(36 + total * 6, 120));
    const angle = (Math.PI * 2 * index) / total - Math.PI / 2;
    const spiderPoint = centerPoint.add(
        leafletPoint(Math.cos(angle) * radius, Math.sin(angle) * radius),
    );

    return map.layerPointToLatLng(spiderPoint);
}
