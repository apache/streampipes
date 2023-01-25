/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseStreamPipesWidget } from '../base/base-widget';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { MapConfig } from './map-config';
import {
    Content,
    icon,
    latLng,
    LatLng,
    LatLngExpression,
    Map,
    Marker,
    marker,
    tileLayer,
} from 'leaflet';
import { ResizeService } from '../../../services/resize.service';
import { DatalakeRestService } from '@streampipes/platform-services';

@Component({
    selector: 'sp-map-widget',
    templateUrl: './map-widget.component.html',
    styleUrls: ['./map-widget.component.css'],
})
export class MapWidgetComponent
    extends BaseStreamPipesWidget
    implements OnInit, OnDestroy
{
    item: any;

    selectedLatitudeField: string;
    selectedLongitudeField: string;
    selectedMarkerIcon: string;
    idsToDisplay: string[];
    additionalItemsToDisplay: string[];
    centerMap: boolean;

    map: Map;
    showMarkers = false;
    markerLayers: Marker[];
    markerIds: string[];

    mapWidth: number;
    mapHeight: number;

    options = {
        layers: [
            tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 18,
                attribution:
                    "© <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> Contributors",
            }),
        ],
        zoom: 5,
        center: latLng(46.879966, -121.726909),
    };

    constructor(
        dataLakeService: DatalakeRestService,
        resizeService: ResizeService,
    ) {
        super(dataLakeService, resizeService, false);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.markerLayers = [];
        this.markerIds = [];
        this.showMarkers = true;
        this.mapWidth = this.computeCurrentWidth(this.itemWidth);
        this.mapHeight = this.computeCurrentHeight(this.itemHeight);
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedLatitudeField = extractor.mappingPropertyValue(
            MapConfig.LATITUDE_MAPPING_KEY,
        );
        this.selectedLongitudeField = extractor.mappingPropertyValue(
            MapConfig.LONGITUDE_MAPPING_KEY,
        );
        this.selectedMarkerIcon = this.markerImage(
            extractor.selectedSingleValue(MapConfig.MARKER_TYPE_KEY),
        );
        this.additionalItemsToDisplay = extractor.mappingPropertyValues(
            MapConfig.ITEMS_MAPPING_KEY,
        );
        this.idsToDisplay = extractor.mappingPropertyValues(
            MapConfig.ID_MAPPING_KEY,
        );
        const b = extractor.singleValueParameter(MapConfig.CENTER_MAP_KEY);
        this.centerMap =
            extractor.selectedSingleValue(MapConfig.CENTER_MAP_KEY) ===
            'Center';
    }

    getFieldsToQuery(): string[] {
        return [
            this.selectedLatitudeField,
            this.selectedLongitudeField,
            ...this.idsToDisplay,
        ];
    }

    markerImage(selectedMarker: string): string {
        return selectedMarker === 'Default'
            ? 'assets/img/marker-icon.png'
            : 'assets/img/pe_icons/car.png';
    }

    onMapReady(map: Map) {
        this.map = map;
        this.map.invalidateSize();
    }

    protected onEvent(events: any[]) {
        // TODO handle when user selected id field

        const tmpMarker = this.getMarker(events)[0];

        // Set one marker when no ids are selected
        if (this.idsToDisplay.length === 0) {
            this.markerLayers = [tmpMarker];
        } else {
            const id = this.getId(events[0]);
            const index = this.markerIds.indexOf(id);
            if (index > -1) {
                this.markerLayers[index] = tmpMarker;
            } else {
                this.markerIds.push(id);
                this.markerLayers.push(tmpMarker);
            }
        }
    }

    getMarker(event) {
        const lat = event[this.selectedLatitudeField];
        const long = event[this.selectedLongitudeField];
        let text = '';
        this.additionalItemsToDisplay.forEach(item => {
            text = text.concat(
                '<b>' + item + '</b>' + ': ' + event[item] + '<br>',
            );
        });

        const content: Content = text;
        const point: LatLngExpression = new LatLng(lat, long);
        const result = this.makeMarker(point);
        result.setLatLng(point);
        result.bindTooltip(content);

        if (this.centerMap) {
            this.map.panTo(point);
        }
        return result;
    }

    makeMarker(point: LatLngExpression): Marker {
        return marker(point, {
            icon: icon({
                iconSize: [25, 41],
                iconAnchor: [13, 41],
                iconUrl: this.selectedMarkerIcon,
                shadowUrl: 'assets/img/marker-shadow.png',
            }),
        });
    }

    getId(event) {
        let result = '';

        for (const id of this.idsToDisplay) {
            result = result + event[id];
        }

        return result;
    }

    protected onSizeChanged(width: number, height: number) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.map.invalidateSize();
    }

    protected getQueryLimit(extractor: StaticPropertyExtractor): number {
        return 1;
    }
}
