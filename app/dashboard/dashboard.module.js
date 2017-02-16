import angular from 'npm/angular';

import _ from 'npm/lodash';

import 'npm/lodash';
import stomp from 'legacy/stomp';
import 'npm/angular-sanitize';
import 'legacy/mlhr-table';
//import 'legacy/malhar-angular-widgets';
import 'legacy/malhar-angular-dashboard';
import 'npm/d3';
import 'npm/epoch-charting';
import 'npm/ngmap'


import DashboardCtrl from './dashboard.controller';
import AddWidgetController from './add-widget.controller';
import SocketConnectionDataModel from './socket-connection-data-model.service';
import WidgetInstances from './widget-instances.service';
import WidgetTemplates from './templates/widget-templates.service';

import soFilter from './templates/so.filter';

import spNumberWidget from './templates/number/number.directive';
import spNumberWidgetConfig from './templates/number/number-config.directive';
import NumberDataModel from './templates/number/number-data-model.service';

import spVerticalbarWidget from './templates/verticalbar/verticalbar.directive';
import spVerticalbarWidgetConfig from './templates/verticalbar/verticalbar-config.directive';
import VerticalbarDataModel from './templates/verticalbar/verticalbar-data-model.service';

import spTableWidget from './templates/table/table.directive';
import spTableWidgetConfig from './templates/table/table-config.directive';
import TableDataModel from './templates/table/table-data-model.service';

import spLineWidget from './templates/line/line.directive';
import spLineWidgetConfig from './templates/line/line-config.directive';
import LineDataModel from './templates/line/line-data-model.service';

import spGaugeWidget from './templates/gauge/gauge.directive';
import spGaugeWidgetConfig from './templates/gauge/gauge-config.directive';
import GaugeDataModel from './templates/gauge/gauge-data-model.service';

import spTrafficlightWidget from './templates/trafficlight/trafficlight.directive';
import spTrafficlightWidgetConfig from './templates/trafficlight/trafficlight-config.directive';
import TrafficlightDataModel from './templates/trafficlight/trafficlight-data-model.service';

import spRawWidget from './templates/raw/raw.directive';
import spRawWidgetConfig from './templates/raw/raw-config.directive';
import RawDataModel from './templates/raw/raw-data-model.service';

import spMapWidget from './templates/map/map.directive';
import spMapWidgetConfig from './templates/map/map-config.directive';
import MapDataModel from './templates/map/map-data-model.service';

import spHeatmapWidget from './templates/heatmap/heatmap.directive';
import spHeatmapWidgetConfig from './templates/heatmap/heatmap-config.directive';
import HeatmapDataModel from './templates/heatmap/heatmap-data-model.service';

export default angular.module('sp.dashboard', ['ui.dashboard', 'datatorrent.mlhrTable', 'ngMap'])
	.controller('DashboardCtrl', DashboardCtrl)
	.factory('AddWidgetController', AddWidgetController)
	.factory('SocketConnectionDataModel', SocketConnectionDataModel)
	.factory('WidgetInstances', WidgetInstances)
	.factory('WidgetTemplates', WidgetTemplates)

	.filter('soNumber', soFilter.soNumber)
	.filter('soDateTime', soFilter.soDateTime)
	.filter('numberFilter', soFilter.nu)
	.filter('geoLat', soFilter.geoLat)
	.filter('geoLng', soFilter.geoLng)

	.directive('spNumberWidget', spNumberWidget)
	.directive('spNumberWidgetConfig', spNumberWidgetConfig)
	.factory('NumberDataModel', NumberDataModel)

	.directive('spVerticalbarWidget', spVerticalbarWidget)
	.directive('spVerticalbarWidgetConfig', spVerticalbarWidgetConfig)
	.factory('VerticalbarDataModel', VerticalbarDataModel)

	.directive('spTableWidget', spTableWidget)
	.directive('spTableWidgetConfig', spTableWidgetConfig)
	.factory('TableDataModel', TableDataModel)
	
	.directive('spLineWidget', spLineWidget)
	.directive('spLineWidgetConfig', spLineWidgetConfig)
	.factory('LineDataModel', LineDataModel)

	.directive('spGaugeWidget', spGaugeWidget)
	.directive('spGaugeWidgetConfig', spGaugeWidgetConfig)
	.factory('GaugeDataModel', GaugeDataModel)

	.directive('spTrafficlightWidget', spTrafficlightWidget)
	.directive('spTrafficlightWidgetConfig', spTrafficlightWidgetConfig)
	.factory('TrafficlightDataModel', TrafficlightDataModel)

	.directive('spRawWidget', spRawWidget)
	.directive('spRawWidgetConfig', spRawWidgetConfig)
	.factory('RawDataModel', RawDataModel)

	.directive('spMapWidget', spMapWidget)
	.directive('spMapWidgetConfig', spMapWidgetConfig)
	.factory('MapDataModel', MapDataModel)

	.directive('spHeatmapWidget', spHeatmapWidget)
	.directive('spHeatmapWidgetConfig', spHeatmapWidgetConfig)
	.factory('HeatmapDataModel', HeatmapDataModel)

	.name;
