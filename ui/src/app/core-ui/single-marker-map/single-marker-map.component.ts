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

import { Component, forwardRef, inject, Input, OnInit } from '@angular/core';
import {
    icon,
    Layer,
    LeafletMouseEvent,
    Map,
    MapOptions,
    marker,
    Marker,
} from 'leaflet';
import {
    AssetLocation,
    LatLng,
    LocationConfig,
} from '@streampipes/platform-services';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { MapLayerProviderService } from '../services/map-layer-provider.service';

@Component({
    selector: 'sp-single-marker-map',
    templateUrl: './single-marker-map.component.html',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => SingleMarkerMapComponent),
            multi: true,
        },
    ],
    standalone: false,
})
export class SingleMarkerMapComponent implements OnInit, ControlValueAccessor {
    private mapLayerProviderService = inject(MapLayerProviderService);

    @Input()
    locationConfig: LocationConfig;

    @Input()
    assetLocation: AssetLocation;

    @Input()
    mapHeight: string = '400px';

    @Input()
    readonly = false;

    map: Map;
    mapOptions: MapOptions;
    layers: Layer[];
    marker: Marker;

    ngOnInit() {
        this.assetLocation ??= {
            coordinates: {
                latitude: 49.00689,
                longitude: 8.40365,
            },
            zoom: 8,
        };
        this.mapOptions = {
            layers: this.mapLayerProviderService.getMapLayers(
                this.locationConfig,
            ),
            zoom: this.assetLocation.zoom || 1,
            zoomControl: !this.readonly,
            center: {
                lat: this.assetLocation.coordinates.latitude,
                lng: this.assetLocation.coordinates.longitude,
            },
        };
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

    onMapReady(map: Map) {
        this.map = map;
        this.map.attributionControl.setPrefix('');
        this.addMarker(this.assetLocation.coordinates);
        setTimeout(() => {
            this.map.invalidateSize();
        }, 0);
    }

    onZoomChange(zoom: number): void {
        this.assetLocation.zoom = zoom;
        this.emitChange();
    }

    onMarkerAdded(e: LeafletMouseEvent) {
        if (!this.readonly) {
            this.addMarker({
                latitude: e.latlng.lat,
                longitude: e.latlng.lng,
            });
            this.emitChange();
        }
    }

    addMarker(location: LatLng): void {
        if (location) {
            if (!this.marker) {
                this.marker = this.makeMarker(location);
                this.marker.addTo(this.map);
            } else {
                this.marker.setLatLng({
                    lat: location.latitude,
                    lng: location.longitude,
                });
            }
            this.assetLocation.coordinates = location;
        }
    }

    private onChange: (_: AssetLocation) => void = () => {};
    private onTouched: () => void = () => {};

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState?(isDisabled: boolean): void {
        this.readonly = isDisabled;
    }

    private emitChange() {
        this.onChange(this.assetLocation);
        this.onTouched();
    }

    writeValue(value: AssetLocation): void {
        if (value) {
            this.assetLocation = value;
            if (this.map) {
                this.addMarker(value.coordinates);
                this.map.setView(
                    [value.coordinates.latitude, value.coordinates.longitude],
                    value.zoom || 1,
                );
            }
        }
    }
}
