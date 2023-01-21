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

import { Component, OnInit } from '@angular/core';
import {
    Content,
    icon,
    LatLng,
    latLng,
    LatLngExpression,
    Map,
    marker,
    Marker,
    polyline,
    Polyline,
    tileLayer,
} from 'leaflet';

import { BaseDataExplorerWidgetDirective } from '../base/base-data-explorer-widget.directive';
import { MapWidgetModel } from './model/map-widget.model';
import {
    DataExplorerField,
    SpQueryResult,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-data-explorer-map-widget',
    templateUrl: './map-widget.component.html',
    styleUrls: ['./map-widget.component.scss'],
})
export class MapWidgetComponent
    extends BaseDataExplorerWidgetDirective<MapWidgetModel>
    implements OnInit
{
    item: any;

    selectedLatitudeField: string;
    selectedLongitudeField: string;
    selectedMarkerIcon: string;
    idsToDisplay: string[];
    additionalItemsToDisplay: string[];
    centerMap: boolean;

    showMarkers = false;

    layers: Marker[] & Polyline[];

    markerIds: string[];

    mapWidth: number;
    mapHeight: number;

    lastDataResults: SpQueryResult;

    map: Map;

    defaultCenter = latLng(46.879966, -121.726909);

    options = {
        layers: [
            tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 18,
                attribution:
                    "© <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> Contributors",
            }),
        ],
        zoom: 1,
        center: this.defaultCenter,
    };

    ngOnInit(): void {
        super.ngOnInit();
        this.layers = [];
        this.markerIds = [];
        this.showMarkers = true;
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

    makeMarker(point: LatLngExpression, markerType: string): Marker {
        let newIconUrl = '';

        newIconUrl =
            markerType === 'pin'
                ? this.markerImage('Default')
                : this.markerImage('Car');

        return marker(point, {
            icon: icon({
                iconSize: [25, 41],
                iconAnchor: [13, 41],
                iconUrl: newIconUrl,
                shadowUrl: 'assets/img/marker-shadow.png',
            }),
        });
    }

    public refreshView() {
        const lastCoordinate = this.getLastCoordinate(this.lastDataResults);

        const zoom =
            this.dataExplorerWidget.visualizationConfig.selectedZoomValue;
        const currentCenter = this.defaultCenter;
        const usedCenter = this.dataExplorerWidget.visualizationConfig
            .useLastEventCoordinates
            ? lastCoordinate
            : currentCenter;

        this.makeLayers(this.lastDataResults);
        if (this.map) {
            this.map.setView(usedCenter, zoom);
            this.map.invalidateSize();
        }
    }

    onResize(width: number, height: number) {
        this.mapWidth = width;
        this.mapHeight = height;
        if (this.map) {
            this.map.invalidateSize();
        }
    }

    handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ) {}

    beforeDataFetched() {
        this.setShownComponents(false, false, true);
    }

    onDataReceived(spQueryResult: SpQueryResult[]) {
        this.setShownComponents(false, true, false, false);
        this.lastDataResults = spQueryResult[0];
        this.makeLayers(spQueryResult[0]);
    }

    transform(rows, index: number): any[] {
        return rows.map(row => row[index]);
    }

    getLastCoordinate(spQueryResults: SpQueryResult) {
        if (spQueryResults && spQueryResults.total > 0) {
            const result = spQueryResults.allDataSeries;

            const latitudeIndex = this.getColumnIndex(
                this.dataExplorerWidget.visualizationConfig
                    .selectedLatitudeProperty,
                spQueryResults,
            );
            const longitudeIndex = this.getColumnIndex(
                this.dataExplorerWidget.visualizationConfig
                    .selectedLongitudeProperty,
                spQueryResults,
            );

            const latitudeValues = this.transform(
                result[0].rows,
                latitudeIndex,
            );
            const longitudeValues = this.transform(
                result[0].rows,
                longitudeIndex,
            );

            return new LatLng(
                latitudeValues[latitudeValues.length - 1],
                longitudeValues[longitudeValues.length - 1],
            );
        }

        return this.defaultCenter;
    }

    makeLayers(spQueryResult: SpQueryResult) {
        this.layers = [];

        if (spQueryResult && spQueryResult.total > 0) {
            for (let i = 0; i <= spQueryResult.allDataSeries.length - 1; i++) {
                const result = spQueryResult.allDataSeries[i];

                const latitudeIndex = this.getColumnIndex(
                    this.dataExplorerWidget.visualizationConfig
                        .selectedLatitudeProperty,
                    spQueryResult,
                );
                const longitudeIndex = this.getColumnIndex(
                    this.dataExplorerWidget.visualizationConfig
                        .selectedLongitudeProperty,
                    spQueryResult,
                );

                const latitudeValues = this.transform(
                    result.rows,
                    latitudeIndex,
                );
                const longitudeValues = this.transform(
                    result.rows,
                    longitudeIndex,
                );

                if (
                    this.dataExplorerWidget.visualizationConfig
                        .selectedMarkerOrTrace === 'marker'
                ) {
                    latitudeValues.map((latitude, index) => {
                        const longitude = longitudeValues[index];
                        const tmpMarker = this.makeMarker(
                            [latitude, longitude],
                            this.dataExplorerWidget.visualizationConfig
                                .selectedMarkerType,
                        );

                        let text =
                            '<b>Time</b>' +
                            ': ' +
                            result.rows[index][0] +
                            '<br>';
                        this.dataExplorerWidget.visualizationConfig.selectedToolTipContent.forEach(
                            item => {
                                const subIndex = this.getColumnIndex(
                                    item,
                                    spQueryResult,
                                );
                                text = text.concat(
                                    '<b>' +
                                        item.fullDbName +
                                        '</b>' +
                                        ': ' +
                                        result.rows[index][subIndex] +
                                        '<br>',
                                );
                            },
                        );

                        const content: Content = text;
                        tmpMarker.bindTooltip(content);

                        this.layers.push(tmpMarker);
                    });
                } else {
                    const coordinates = [];
                    latitudeValues.map((latitude, index) => {
                        coordinates.push([latitude, longitudeValues[index]]);
                    });

                    const poly = polyline(coordinates, { color: 'red' });
                    this.layers.push(poly);
                }
            }
        }
    }
}
