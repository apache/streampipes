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

import * as angular from 'angular';

export class WidgetTemplates {

    widgetTypes: any;

    constructor(TableDataModel,
                NumberDataModel,
                LineDataModel,
                VerticalbarDataModel,
                GaugeDataModel,
                TrafficLightDataModel,
                RawDataModel,
                MapDataModel,
                HeatmapDataModel,
                ImageDataModel,
                HtmlDataModel) {


        //Register the new widgets here
        this.widgetTypes = {
            table: {
                name: 'table',
                label: 'Table Visualisation',
                icon: 'format_list_numbered',
                directive: 'sp-table-widget',
                dataModel: TableDataModel,
            },
            number: {
                name: 'number',
                label: 'Single Value Visualisation',
                directive: 'sp-number-widget',
                icon: 'exposure_plus_2',
                dataModel: NumberDataModel,
            },
            line: {
                name: 'line',
                label: 'Line Chart',
                icon: 'show_chart',
                directive: 'sp-line-widget',
                dataModel: LineDataModel,
            },
            verticalbar: {
                name: 'verticalbar',
                label: 'Vertical Bar Chart',
                icon: 'insert_chart',
                directive: 'sp-verticalbar-widget',
                dataModel: VerticalbarDataModel,
            },
            gauge: {
                name: 'gauge',
                label: 'Gauge',
                icon: 'network_check',
                directive: 'sp-gauge-widget',
                dataModel: GaugeDataModel,
            },
            trafficlight: {
                name: 'trafficlight',
                label: 'Traffic Light',
                icon: 'traffic',
                directive: 'sp-trafficlight-widget',
                dataModel: TrafficLightDataModel,
            },
            raw: {
                name: 'raw',
                label: 'Raw Data',
                icon: '',
                directive: 'sp-raw-widget',
                dataModel: RawDataModel,
            },
            map: {
                name: 'map',
                label: 'Map',
                icon: 'map',
                directive: 'sp-map-widget',
                dataModel: MapDataModel,
            },
            // heatmap: {
            //     name: 'heatmap',
            //     label: 'Heatmap',
            //     icon: '',
            //     directive: 'sp-heatmap-widget',
            //     dataModel: HeatmapDataModel,
            // },
            image: {
                name: 'image',
                label: 'Image',
                icon: 'image',
                directive: 'sp-image-widget',
                dataModel: ImageDataModel,
            },
            html: {
                name: 'html',
                label: 'HTML',
                icon: 'web_asset',
                directive: 'sp-html-widget',
                dataModel: HtmlDataModel,
            }

        }
    }

    getDataModel(name) {
        return this.widgetTypes[name].dataModel;
    }

    getDirectiveName(name) {
        return this.widgetTypes[name].directive;
    }

    getAllNames() {
        var result = [];
        angular.forEach(this.widgetTypes, function (w) {
            var vis = {};
            vis['name'] = w.name;
            vis['label'] = w.label;
            vis['icon'] = w.icon;
            result.push(vis);
        });

        return result;
    }

};

WidgetTemplates.$inject = [
    'TableDataModel',
    'NumberDataModel',
    'LineDataModel',
    'VerticalbarDataModel',
    'GaugeDataModel',
    'TrafficLightDataModel',
    'RawDataModel',
    'MapDataModel',
    'HeatmapDataModel',
    'ImageDataModel',
    'HtmlDataModel'
];

