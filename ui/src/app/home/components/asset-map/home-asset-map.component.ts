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
    ComponentRef,
    createComponent,
    EnvironmentInjector,
    inject,
    Input,
    OnChanges,
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
    icon,
    Layer,
    LeafletMouseEvent,
    Map,
    MapOptions,
    marker,
    Marker,
    popup,
} from 'leaflet';
import { MapLayerProviderService } from '../../../core-ui/services/map-layer-provider.service';
import {
    AssetMapPopupComponent,
    PopupAction,
} from './asset-map-popup/asset-map-popup.component';

@Component({
    selector: 'sp-home-asset-map',
    templateUrl: './home-asset-map.component.html',
    styleUrls: ['./home-asset-map.component.scss'],
    standalone: false,
})
export class HomeAssetMapComponent implements OnInit, OnChanges {
    @Input()
    locationConfig: LocationConfig;

    @Input()
    assets: SpAssetModel[] = [];

    @Input()
    sites: Record<string, AssetSiteDesc> = {};

    @Input()
    assetLinkTypes: Record<string, AssetLinkType> = {};

    map: Map;
    mapOptions: MapOptions;
    layers: Layer[];
    marker: Marker;
    markersGroup: FeatureGroup = featureGroup();

    private currentPopupRef: ComponentRef<AssetMapPopupComponent> | null = null;

    private mapLayerProviderService = inject(MapLayerProviderService);
    private injector = inject(EnvironmentInjector);

    ngOnInit() {
        this.mapOptions = {
            layers: this.mapLayerProviderService.getMapLayers(
                this.locationConfig,
            ),
            zoom: 10,
            zoomControl: true,
        };
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['assets'] && this.map) {
            this.refreshMarkersAndView();
        }
    }

    onMapReady(map: Map) {
        this.map = map;
        this.map.attributionControl.setPrefix('');
        this.markersGroup.addTo(this.map);
        this.refreshMarkersAndView();

        setTimeout(() => {
            map.invalidateSize();
        }, 0);
    }

    refreshMarkersAndView(): void {
        this.markersGroup.clearLayers();
        const assetsWithSite = this.assets.filter(a => a.assetSite !== null);

        assetsWithSite.forEach(asset => {
            const site = this.sites[asset.assetSite.siteId];
            const assetLocation = site.location.coordinates;
            const marker = this.makeMarker({
                latitude: assetLocation.latitude,
                longitude: assetLocation.longitude,
            });
            marker.on('click', (e: LeafletMouseEvent) => {
                this.openPopup(e, asset, site);
            });
            this.markersGroup.addLayer(marker);
        });
        const bounds = (this.markersGroup as any).getBounds?.();
        if (!bounds || !bounds.isValid()) {
            this.map.setView(
                { lat: 0, lng: 0 },
                Math.min(this.map.getMaxZoom() ?? 3, 3),
            );
        } else {
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
    }

    makeMarker(location: LatLng): Marker {
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

    openPopup(
        event: LeafletMouseEvent,
        asset: SpAssetModel,
        site: AssetSiteDesc,
    ) {
        this.currentPopupRef = createComponent(AssetMapPopupComponent, {
            environmentInjector: this.injector,
        });

        this.currentPopupRef.instance.asset = asset;
        this.currentPopupRef.instance.site = site;
        this.currentPopupRef.instance.assetLinkTypes = this.assetLinkTypes;

        this.currentPopupRef.instance.actionClicked.subscribe(
            (action: PopupAction) => {
                this.handlePopupAction(action, asset);
            },
        );

        this.currentPopupRef.changeDetectorRef.detectChanges();
        const popupContent = this.currentPopupRef.location.nativeElement;

        popup({
            offset: [0, -20],
            minWidth: 380,
            closeButton: false,
            className: 'sp-leaflet-popup-clean',
        })
            .setLatLng(event.latlng)
            .setContent(popupContent)
            .openOn(this.map);
    }

    handlePopupAction(action: PopupAction, asset: SpAssetModel) {}

    onMarkerClicked(e: LeafletMouseEvent) {}
}
