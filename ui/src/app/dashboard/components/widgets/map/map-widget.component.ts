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

import {Component, OnDestroy, OnInit} from "@angular/core";
import {RxStompService} from "@stomp/ng2-stompjs";
import {BaseStreamPipesWidget} from "../base/base-widget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {MapConfig} from "./map-config";
import {latLng, marker, Marker, tileLayer, Map, LatLngExpression, LatLng, icon, Content} from "leaflet";
import {ResizeService} from "../../../services/resize.service";
import {DashboardService} from "../../../services/dashboard.service";

@Component({
    selector: 'map-widget',
    templateUrl: './map-widget.component.html',
    styleUrls: ['./map-widget.component.css']
})
export class MapWidgetComponent extends BaseStreamPipesWidget implements OnInit, OnDestroy {

    item: any;

    selectedLatitudeField: string;
    selectedLongitudeField: string;
    selectedMarkerIcon: string;
    additionalItemsToDisplay: Array<string>;

    map: Map;
    showMarkers: boolean = false;
    markerLayer: Marker;

    mapWidth: number;
    mapHeight: number;

    options = {
        layers: [
            tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: "© <a href=\'https://www.openstreetmap.org/copyright\'>OpenStreetMap</a> Contributors" })
        ],
        zoom: 5,
        center: latLng(46.879966, -121.726909)
    };

    constructor(rxStompService: RxStompService, dashboardService: DashboardService, resizeService: ResizeService) {
        super(rxStompService, dashboardService, resizeService, false);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.markerLayer = this.makeMarker([0, 0]);
        this.showMarkers = true;
        this.mapWidth = this.computeCurrentWidth(this.gridsterItemComponent);
        this.mapHeight = this.computeCurrentHeight(this.gridsterItemComponent);
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    extractConfig(extractor: StaticPropertyExtractor) {
        this.selectedLatitudeField = extractor.mappingPropertyValue(MapConfig.LATITUDE_MAPPING_KEY);
        this.selectedLongitudeField = extractor.mappingPropertyValue(MapConfig.LONGITUDE_MAPPING_KEY);
        this.selectedMarkerIcon = this.markerImage(extractor.selectedSingleValue(MapConfig.MARKER_TYPE_KEY));
        this.additionalItemsToDisplay = extractor.mappingPropertyValues(MapConfig.ITEMS_MAPPING_KEY);
    }

    markerImage(selectedMarker: string): string {
        return selectedMarker === "Default" ? 'assets/img/marker-icon.png' : 'assets/img/pe_icons/car.png';
    }

    onMapReady(map: Map) {
        this.map = map;
        this.map.invalidateSize();
    }

    protected onEvent(event: any) {
        this.updatePosition(event);
    }

    updatePosition(event) {
        var lat = event[this.selectedLatitudeField];
        var long = event[this.selectedLongitudeField];
        var text = "";
        this.additionalItemsToDisplay.forEach(item => {
            text =  text.concat("<b>" +item +"</b>" +  ": " + event[item] + "<br>");
        });

        let content : Content = text;
        let point: LatLngExpression = new LatLng(lat, long);
        this.markerLayer.setLatLng(point);
        this.markerLayer.bindTooltip(content);
        this.map.panTo(point);

    };

    makeMarker(point: LatLngExpression): Marker {
        return marker(point, { icon: icon({
                iconSize: [ 25, 41 ],
                iconAnchor: [ 13, 41 ],
                iconUrl: this.selectedMarkerIcon,
                shadowUrl: 'assets/img/marker-shadow.png'
            })});
    }

    protected onSizeChanged(width: number, height: number) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.map.invalidateSize();
    }

}