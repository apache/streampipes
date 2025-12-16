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
    OnInit,
} from '@angular/core';
import {
    AssetLinkType,
    AssetSiteDesc,
    LatLng,
    LocationConfig,
    SpAssetModel,
} from '@streampipes/platform-services';
import {
    icon,
    latLng,
    latLngBounds,
    LatLngBounds,
    LatLngExpression,
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
export class HomeAssetMapComponent implements OnInit {
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
    bounds: LatLngBounds;

    private currentPopupRef: ComponentRef<AssetMapPopupComponent> | null = null;

    private mapLayerProviderService = inject(MapLayerProviderService);
    private injector = inject(EnvironmentInjector);

    ngOnInit() {
        const result = this.getCenterAndBounds();
        this.bounds = result.bounds;

        this.mapOptions = {
            layers: this.mapLayerProviderService.getMapLayers(
                this.locationConfig,
            ),
            zoom: 10,
            zoomControl: true,
            center: result.center,
        };
    }

    getCenterAndBounds(): { center: LatLngExpression; bounds: L.LatLngBounds } {
        const latLngs = this.assets
            .filter(a => a.assetSite !== null)
            .map(s => s.assetSite.siteId)
            .map(s => this.sites[s])
            .map(s => s.location.coordinates)
            .map(coordinates =>
                latLng(coordinates.latitude, coordinates.longitude),
            );

        if (latLngs.length === 0) {
            return {
                center: { lat: 0, lng: 0 },
                bounds: latLngBounds([
                    [0, 0],
                    [0, 0],
                ]),
            };
        }

        const bounds = latLngBounds(latLngs);
        return { center: bounds.getCenter(), bounds };
    }

    onMapReady(map: Map) {
        this.map = map;
        this.map.attributionControl.setPrefix('');
        this.createMarkers();

        setTimeout(() => {
            map.invalidateSize();
            if (this.bounds && this.bounds.isValid()) {
                map.fitBounds(this.bounds, { padding: [24, 24] });
            }
        }, 0);
    }

    createMarkers(): void {
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
            marker.addTo(this.map);
        });
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
