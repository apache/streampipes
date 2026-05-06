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
    Component,
    EnvironmentInjector,
    inject,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    SimpleChanges,
} from '@angular/core';
import {
    AssetLinkType,
    AssetSiteDesc,
    LatLng,
    LocationConfig,
    SpAssetModel,
} from '@streampipes/platform-services';
import {
    featureGroup,
    FeatureGroup,
    latLng,
    LeafletMouseEvent,
    Map as LeafletMap,
    MapOptions,
} from 'leaflet';
import { MapLayerProviderService } from '../../../core-ui/services/map-layer-provider.service';
import { LeafletDirective } from '@bluehalo/ngx-leaflet';
import { NgStyle } from '@angular/common';
import { StyleDirective } from '@ngbracket/ngx-layout/extended';
import { TranslatePipe } from '@ngx-translate/core';
import Supercluster from 'supercluster';
import { HomeAssetMapPopupService } from './home-asset-map-popup.service';
import {
    createAssetMarker,
    createClusterMarker,
    createSpiderfyLeg,
    createSpiderfyMarker,
    getSpiderfyLatLng,
} from './home-asset-map-marker.utils';
import {
    buildAssetBounds,
    buildClusterPoints,
    isClusterFeature,
    shouldSpiderfy,
} from './home-asset-map.utils';
import {
    AssetClusterFeature,
    AssetPointFeature,
    AssetPointProperties,
    AssetPopupEntry,
} from './home-asset-map.types';

@Component({
    selector: 'sp-home-asset-map',
    templateUrl: './home-asset-map.component.html',
    styleUrls: ['./home-asset-map.component.scss'],
    imports: [LeafletDirective, NgStyle, StyleDirective, TranslatePipe],
    providers: [HomeAssetMapPopupService],
})
export class HomeAssetMapComponent implements OnInit, OnChanges, OnDestroy {
    @Input()
    locationConfig: LocationConfig;

    @Input()
    assets: SpAssetModel[] = [];

    @Input()
    sites: Record<string, AssetSiteDesc> = {};

    @Input()
    assetLinkTypes: Record<string, AssetLinkType> = {};

    map: LeafletMap;
    mapOptions: MapOptions;
    markersGroup: FeatureGroup = featureGroup();
    spiderfyGroup: FeatureGroup = featureGroup();
    assetsWithoutLocationCount = 0;
    assetsWithLocationCount = 0;
    private readonly defaultCenter = latLng(0, 0);

    private clusterIndex: Supercluster<
        AssetPointProperties,
        Record<string, never>
    > | null = null;
    private readonly clusterMaxZoom = 18;

    private mapLayerProviderService = inject(MapLayerProviderService);
    private popupService = inject(HomeAssetMapPopupService);
    private injector = inject(EnvironmentInjector);

    ngOnInit() {
        this.mapOptions = {
            layers: this.mapLayerProviderService.getMapLayers(
                this.locationConfig,
            ),
            zoom: 3,
            zoomControl: true,
            center: this.defaultCenter,
        };
    }

    ngOnChanges(changes: SimpleChanges) {
        if ((changes['assets'] || changes['sites']) && this.map) {
            this.refreshMarkersAndView();
        }
    }

    ngOnDestroy() {
        this.map?.off('moveend', this.onMapViewportChanged);
        this.map?.off('zoomend', this.onMapViewportChanged);
        this.popupService.destroyPopup();
        this.clearSpiderfy();
    }

    onMapReady(map: LeafletMap) {
        this.map = map;
        this.map.attributionControl.setPrefix('');
        this.markersGroup.addTo(this.map);
        this.spiderfyGroup.addTo(this.map);
        this.map.on('moveend', this.onMapViewportChanged);
        this.map.on('zoomend', this.onMapViewportChanged);
        this.refreshMarkersAndView();

        setTimeout(() => {
            map.invalidateSize();
        }, 0);
    }

    onMarkerClicked(_event: LeafletMouseEvent) {
        this.clearSpiderfy();
    }

    openPopup(event: LeafletMouseEvent, entries: AssetPopupEntry[]) {
        this.popupService.openPopup(
            this.map,
            this.injector,
            event,
            entries,
            this.assetLinkTypes,
        );
    }

    private refreshMarkersAndView(): void {
        this.popupService.destroyPopup();
        this.clearSpiderfy();

        const assetPoints = buildClusterPoints(this.assets, this.sites);
        this.assetsWithLocationCount = assetPoints.length;
        this.assetsWithoutLocationCount = Math.max(
            this.assets.length - this.assetsWithLocationCount,
            0,
        );
        this.clusterIndex =
            assetPoints.length > 0
                ? new Supercluster<AssetPointProperties, Record<string, never>>(
                      {
                          maxZoom: this.clusterMaxZoom,
                          radius: 60,
                      },
                  ).load(assetPoints)
                : null;

        this.updateViewport(assetPoints);

        if (this.isMapLoaded()) {
            this.renderVisibleMarkers();
        }
    }

    private renderVisibleMarkers(): void {
        this.markersGroup.clearLayers();

        if (!this.clusterIndex || !this.map || !this.isMapLoaded()) {
            return;
        }

        const bounds = this.map.getBounds();
        const visibleFeatures = this.clusterIndex.getClusters(
            [
                bounds.getWest(),
                bounds.getSouth(),
                bounds.getEast(),
                bounds.getNorth(),
            ],
            Math.round(this.map.getZoom()),
        );
        const pointGroups = new Map<string, AssetPointFeature[]>();

        visibleFeatures.forEach(feature => {
            const [longitude, latitude] = feature.geometry.coordinates;
            const location: LatLng = { latitude, longitude };

            if (isClusterFeature(feature)) {
                this.addClusterMarker(feature, location);
            } else {
                const pointGroupKey = `${latitude}:${longitude}`;
                if (!pointGroups.has(pointGroupKey)) {
                    pointGroups.set(pointGroupKey, []);
                }

                pointGroups.get(pointGroupKey).push(feature);
            }
        });

        pointGroups.forEach(features => {
            const [longitude, latitude] = features[0].geometry.coordinates;
            const location: LatLng = { latitude, longitude };

            if (features.length === 1) {
                this.addAssetMarker(features[0], location);
            } else {
                this.addGroupedAssetMarker(features, location);
            }
        });
    }

    private addClusterMarker(
        feature: AssetClusterFeature,
        location: LatLng,
    ): void {
        const clusterMarker = createClusterMarker(
            location,
            feature.properties.point_count,
        );
        clusterMarker.on('click', (event: LeafletMouseEvent) => {
            event.originalEvent?.stopPropagation();
            this.onClusterClicked(event, feature);
        });
        this.markersGroup.addLayer(clusterMarker);
    }

    private addAssetMarker(feature: AssetPointFeature, location: LatLng): void {
        const assetMarker = createAssetMarker(location);
        assetMarker.on('click', (event: LeafletMouseEvent) => {
            event.originalEvent?.stopPropagation();
            this.clearSpiderfy();
            this.openPopup(event, [feature.properties]);
        });
        this.markersGroup.addLayer(assetMarker);
    }

    private addGroupedAssetMarker(
        features: AssetPointFeature[],
        location: LatLng,
    ): void {
        const groupedMarker = createClusterMarker(location, features.length);
        groupedMarker.on('click', (event: LeafletMouseEvent) => {
            event.originalEvent?.stopPropagation();
            this.clearSpiderfy();
            this.openPopup(
                event,
                features.map(feature => feature.properties),
            );
        });
        this.markersGroup.addLayer(groupedMarker);
    }

    private onClusterClicked(
        event: LeafletMouseEvent,
        cluster: AssetClusterFeature,
    ): void {
        if (!this.clusterIndex) {
            return;
        }

        const clusterId = cluster.properties.cluster_id;
        const maxZoom = this.map.getMaxZoom() ?? this.clusterMaxZoom;
        const expansionZoom =
            this.clusterIndex.getClusterExpansionZoom(clusterId);
        const leaves = this.clusterIndex.getLeaves(clusterId, Infinity);

        if (
            shouldSpiderfy(leaves, expansionZoom, this.map.getZoom(), maxZoom)
        ) {
            this.popupService.destroyPopup();
            this.spiderfyCluster(event, leaves);
            return;
        }

        this.clearSpiderfy();
        this.map.setView(event.latlng, Math.min(expansionZoom, maxZoom), {
            animate: true,
        });
    }

    private spiderfyCluster(
        event: LeafletMouseEvent,
        leaves: AssetPointFeature[],
    ): void {
        this.clearSpiderfy();

        leaves.forEach((leaf, index) => {
            const spiderLatLng = getSpiderfyLatLng(
                this.map,
                event.latlng,
                index,
                leaves.length,
            );

            this.spiderfyGroup.addLayer(
                createSpiderfyLeg(event.latlng, spiderLatLng),
            );

            const spiderMarker = createSpiderfyMarker(spiderLatLng, index);
            spiderMarker.on('click', (spiderEvent: LeafletMouseEvent) => {
                spiderEvent.originalEvent?.stopPropagation();
                this.openPopup(spiderEvent, [leaf.properties]);
            });

            this.spiderfyGroup.addLayer(spiderMarker);
        });
    }

    private updateViewport(assetPoints: AssetPointFeature[]): void {
        const bounds = buildAssetBounds(assetPoints);

        if (!bounds || !bounds.isValid()) {
            this.map.setView(
                { lat: 0, lng: 0 },
                Math.min(this.map.getMaxZoom() ?? 3, 3),
            );
            return;
        }

        const sw = bounds.getSouthWest();
        const ne = bounds.getNorthEast();

        if (sw.equals(ne)) {
            this.map.setView(sw, Math.min(this.map.getMaxZoom() ?? 18, 18));
        } else {
            setTimeout(() => {
                this.map.fitBounds(bounds, { padding: [24, 24] });
            });
        }
    }

    private clearSpiderfy(): void {
        this.spiderfyGroup.clearLayers();
    }

    private isMapLoaded(): boolean {
        return Boolean(
            (this.map as LeafletMap & { _loaded?: boolean })?._loaded,
        );
    }

    private readonly onMapViewportChanged = () => {
        this.clearSpiderfy();
        this.renderVisibleMarkers();
    };
}
