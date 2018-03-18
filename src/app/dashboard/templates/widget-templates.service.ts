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
                HeatmapDataModel) {


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
            heatmap: {
                name: 'heatmap',
                label: 'Heatmap',
                icon: '',
                directive: 'sp-heatmap-widget',
                dataModel: HeatmapDataModel,
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
    'HeatmapDataModel'
];

