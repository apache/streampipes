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

import { IWidget } from '../models/dataview-dashboard.model';
import { TableWidgetComponent } from '../components/widgets/table/table-widget.component';
import { MapWidgetComponent } from '../components/widgets/map/map-widget.component';
import { HeatmapWidgetComponent } from '../components/widgets/heatmap/heatmap-widget.component';
import { TimeSeriesChartWidgetComponent } from '../components/widgets/time-series-chart/time-series-chart-widget.component';
import { ImageWidgetComponent } from '../components/widgets/image/image-widget.component';
import { IndicatorChartWidgetComponent } from '../components/widgets/indicator/indicator-chart-widget.component';
import { CorrelationChartWidgetComponent } from '../components/widgets/correlation-chart/correlation-chart-widget.component';
import { DistributionChartWidgetComponent } from '../components/widgets/distribution-chart/distribution-chart-widget.component';
import { SpPieRenderer } from '../echarts-renderer/sp-pie-renderer';
import { SpValueHeatmapRenderer } from '../echarts-renderer/sp-value-heatmap-renderer';
import { SpHistogramRenderer } from '../echarts-renderer/sp-histogram-renderer';

export enum WidgetType {
    Table,
    Map,
    Heatmap,
    LineChart,
    Image,
    IndicatorChart,
    CorrelationChart,
    DistributionChart,
}

export const WidgetTypeMap = new Map<number, IWidget<any>>([
    [
        WidgetType.Table,
        {
            id: 'table',
            label: 'Table',
            componentClass: TableWidgetComponent,
        },
    ],
    [
        WidgetType.Map,
        {
            id: 'map',
            label: 'Map',
            componentClass: MapWidgetComponent,
        },
    ],
    [
        WidgetType.Heatmap,
        {
            id: 'heatmap',
            label: 'Heatmap',
            componentClass: HeatmapWidgetComponent,
        },
    ],
    [
        WidgetType.LineChart,
        {
            id: 'time-series-chart',
            label: 'Time Series',
            componentClass: TimeSeriesChartWidgetComponent,
        },
    ],
    [
        WidgetType.Image,
        { id: 'image', label: 'Image', componentClass: ImageWidgetComponent },
    ],
    [
        WidgetType.IndicatorChart,
        {
            id: 'indicator-chart',
            label: 'Indicator',
            componentClass: IndicatorChartWidgetComponent,
        },
    ],
    [
        WidgetType.CorrelationChart,
        {
            id: 'correlation-chart',
            label: '2D Correlation',
            componentClass: CorrelationChartWidgetComponent,
        },
    ],
    [
        WidgetType.DistributionChart,
        {
            id: 'distribution-chart',
            label: 'Distribution',
            componentClass: DistributionChartWidgetComponent,
            renderers: [
                new SpHistogramRenderer(),
                new SpPieRenderer(),
                new SpValueHeatmapRenderer(),
            ],
        },
    ],
]);
