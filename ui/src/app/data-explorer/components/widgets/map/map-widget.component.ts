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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Content, icon, latLng, LatLngExpression, Map, marker, Marker, polyline, Polyline, tileLayer } from "leaflet";
import { DataResult } from '../../../../core-model/datalake/DataResult';

import { BaseDataExplorerWidget } from '../base/base-data-explorer-widget';
import { MapWidgetModel } from './model/map-widget.model';
import { DataExplorerField } from '../../../models/dataview-dashboard.model';

@Component({
  selector: 'sp-data-explorer-map-widget',
  templateUrl: './map-widget.component.html',
  styleUrls: ['./map-widget.component.scss']
})
export class MapWidgetComponent extends BaseDataExplorerWidget<MapWidgetModel> implements OnInit, OnDestroy {

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

  lastDataResults: DataResult[];

  map: Map;

  options = {
    layers: [
      tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: "© <a href=\'https://www.openstreetmap.org/copyright\'>OpenStreetMap</a> Contributors" })
    ],
    zoom: 5,
    center: latLng(46.879966, -121.726909),
  };

  ngOnInit(): void {
    super.ngOnInit();
    this.layers = [];
    this.markerIds = [];
    this.showMarkers = true;

    // this.mapWidth = 1600;
    // this.mapHeight = 1200;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  markerImage(selectedMarker: string): string {
    return selectedMarker === 'Default' ? 'assets/img/marker-icon.png' : 'assets/img/pe_icons/car.png';
  }

  onMapReady(map: Map) {
    this.map = map;
    // this.map.invalidateSize();
  }

  makeMarker(point: LatLngExpression, markerType: string): Marker {
    var iconUrl = ''
    
    if (markerType == 'pin') {
      iconUrl = this.markerImage("Default");
    }
    else {
      iconUrl = this.markerImage("Car");
    }
    
    return marker(point, {
      icon: icon({
        iconSize: [25, 41],
        iconAnchor: [13, 41],
        iconUrl: iconUrl,
        shadowUrl: 'assets/img/marker-shadow.png'
      })
    });
  }

  public refreshView() {
    this.makeLayers(this.lastDataResults)
  }

  onResize(width: number, height: number) {
    this.mapWidth = width;
    this.mapHeight = height;
    // this.map.invalidateSize();
  }

  handleUpdatedFields(addedFields: DataExplorerField[],
    removedFields: DataExplorerField[]) {
  }

  beforeDataFetched() {
    this.setShownComponents(false, false, true);
  }

  onDataReceived(dataResults: DataResult[]) {
    this.lastDataResults = dataResults
    this.makeLayers(dataResults);    
  }

  transform(rows, index: number): any[] {
    return rows.map(row => row[index]);
  }

  makeLayers(dataResults: DataResult[]) {
    this.layers = []

    if (dataResults[0]['total'] > 1) {
      const result = dataResults[0];

      const latitudeIndex = this.getColumnIndex(this.dataExplorerWidget.visualizationConfig.selectedLatitudeProperty, result);
      const longitudeIndex = this.getColumnIndex(this.dataExplorerWidget.visualizationConfig.selectedLongitudeProperty, result);

      const latitudeValues = this.transform(result.rows, latitudeIndex);
      const longitudeValues = this.transform(result.rows, longitudeIndex);

      if (this.dataExplorerWidget.visualizationConfig.selectedMarkerOrTrace == 'marker') {
      
        latitudeValues.map((latitude, index) => {
          const longitude = longitudeValues[index];
          const marker = this.makeMarker([latitude, longitude], this.dataExplorerWidget.visualizationConfig.selectedMarkerType)
        
          let text = '';
          this.dataExplorerWidget.visualizationConfig.selectedToolTipContent.forEach(item => {
            const subIndex = this.getColumnIndex(item, result);
            text = text.concat('<b>' + item.fullDbName + '</b>' + ': ' + result.rows[index][subIndex] + '<br>');
          });

          const content: Content = text;
          marker.bindTooltip(content);

          this.layers.push(marker)

        });
      }
      else {
        const coordinates = [];
        latitudeValues.map((latitude, index) => {
          coordinates.push([latitude, longitudeValues[index]]);
        });
   
        const poly = polyline(coordinates, {color: 'red'})
        this.layers.push(poly);
      }
    }
  }

}
