import angular from 'npm/angular';

import _ from 'npm/lodash';

import 'npm/lodash';
import stomp from 'legacy/stomp';
import 'npm/angular-sanitize';
import 'legacy/mlhr-table';
import 'legacy/malhar-angular-dashboard';
import 'npm/epoch-charting';
import 'npm/ngmap'


import { DashboardCtrl } from './dashboard.controller';
import { AddWidgetCtrl } from './add-widget.controller';
import { WidgetInstances } from './widget-instances.service';
import { WidgetTemplates } from './templates/widget-templates.service';

import {WidgetDataModel} from "./widget-data-model.service";

import { SocketConnectionDataModel } from './socket-connection-data-model.service';

import soFilter from './templates/so.filter';

import spNumberWidget from './templates/number/number.directive';
import { SpNumberWidgetConfig }from './templates/number/number-config.component';
import { NumberDataModel } from './templates/number/number-data-model.service';

import spVerticalbarWidget from './templates/verticalbar/verticalbar.directive';
import { SpVerticalbarWidgetConfig } from './templates/verticalbar/verticalbar-config.component';
import { VerticalbarDataModel } from './templates/verticalbar/verticalbar-data-model.service';

import spTableWidget from './templates/table/table.directive';
import { SpTableWidgetConfig } from './templates/table/table-config.component';
import { TableDataModel } from './templates/table/table-data-model.service';

import spLineWidget from './templates/line/line.directive';
import { SspLineWidgetConfig } from './templates/line/line-config.component';
import { LineDataModel } from './templates/line/line-data-model.service';

import spGaugeWidget from './templates/gauge/gauge.directive';
import { SpGaugeWidgetConfig } from './templates/gauge/gauge-config.component';
import { GaugeDataModel } from './templates/gauge/gauge-data-model.service';

import spTrafficlightWidget from './templates/trafficlight/trafficlight.directive';
import { SspTrafficlightWidgetConfig } from './templates/trafficlight/trafficlight-config.component';
import { TrafficlightDataModel } from './templates/trafficlight/trafficlight-data-model.service';

import spRawWidget from './templates/raw/raw.directive';
import { SpRawWidgetConfig } from './templates/raw/raw-config.component';
import { RawDataModel } from './templates/raw/raw-data-model.service';

import spMapWidget from './templates/map/map.directive';
import { SpMapWidgetConfig } from './templates/map/map-config.component';
import { MapDataModel } from './templates/map/map-data-model.service';

import spHeatmapWidget from './templates/heatmap/heatmap.directive';
import { SpHeatmapWidgetConfig } from './templates/heatmap/heatmap-config.component';
import { HeatmapDataModel } from './templates/heatmap/heatmap-data-model.service';

export default angular.module('sp.dashboard', ['ui.dashboard', 'datatorrent.mlhrTable', 'ngMap'])
	.controller('DashboardCtrl', DashboardCtrl)
	.controller('AddWidgetCtrl', AddWidgetCtrl)
    .service('WidgetTemplates', WidgetTemplates)
    .service('WidgetDataModel', WidgetDataModel)
    .service('SocketConnectionDataModel', SocketConnectionDataModel)


	.service('WidgetInstances', WidgetInstances)

	.filter('soNumber', soFilter.soNumber)
	.filter('soDateTime', soFilter.soDateTime)
	.filter('numberFilter', soFilter.nu)
	.filter('geoLat', soFilter.geoLat)
	.filter('geoLng', soFilter.geoLng)

	.directive('spNumberWidget', spNumberWidget)
	.component('SpNumberWidgetConfig', SpNumberWidgetConfig)
	.service('NumberDataModel', NumberDataModel)

	.directive('spVerticalbarWidget', spVerticalbarWidget)
	.component('SpVerticalbarWidgetConfig', SpVerticalbarWidgetConfig)
	.service('VerticalbarDataModel', VerticalbarDataModel)

    .directive('spTableWidget', spTableWidget)
    .component('SpTableWidgetConfig', SpTableWidgetConfig)
    .service('TableDataModel', TableDataModel)

    .directive('spLineWidget', spLineWidget)
    .component('SpLineWidgetConfig', SpLineWidgetConfig)
    .service('LineDataModel', LineDataModel)

    .directive('spGaugeWidget', spGaugeWidget)
    .component('SpGaugeWidgetConfig', SpGaugeWidgetConfig)
    .service('GaugeDataModel', GaugeDataModel)

    .directive('spTrafficlightWidget', spTrafficlightWidget)
    .component('SpTrafficlightWidgetConfig', SpTrafficlightWidgetConfig)
    .service('TrafficlightDataModel', TrafficlightDataModel)

    .directive('spRawWidget', spRawWidget)
    .component('SpRawWidgetConfig', SpRawWidgetConfig)
    .service('RawDataModel', RawDataModel)

    .directive('spMapWidget', spMapWidget)
    .component('SpMapWidgetConfig', SpMapWidgetConfig)
    .service('MapDataModel', MapDataModel)

    .directive('spHeatmapWidget', spHeatmapWidget)
    .component('SpHeatmapWidgetConfig', SpHeatmapWidgetConfig)
    .service('HeatmapDataModel', HeatmapDataModel)

	.name;
