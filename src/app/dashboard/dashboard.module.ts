import * as angular from 'angular';

import * as _ from 'lodash';

import 'angular-ui-sortable';
import 'angular-ui-bootstrap';

import 'npm/lodash';
import 'legacy/stomp';
import 'npm/angular-sanitize';
import 'legacy/mlhr-table';
import 'legacy/malhar-angular-dashboard';
//import 'npm/epoch-charting';
import 'npm/ngmap'


import { DashboardCtrl } from './dashboard.controller';
import { AddWidgetCtrl } from './add-widget.controller';
import { WidgetInstances } from './widget-instances.service';
import { WidgetTemplates } from './templates/widget-templates.service';

import {WidgetDataModel} from "./widget-data-model.service";

import { SocketConnectionDataModel } from './socket-connection-data-model.service';

import soFilter from './templates/so.filter';

import spNumberWidget from './templates/number/number.directive';
import { spNumberWidgetConfig }from './templates/number/number-config.component';
import { NumberDataModel } from './templates/number/number-data-model.service';

import spVerticalbarWidget from './templates/verticalbar/verticalbar.directive';
import { spVerticalbarWidgetConfig } from './templates/verticalbar/verticalbar-config.component';
import { VerticalbarDataModel } from './templates/verticalbar/verticalbar-data-model.service';

import spTableWidget from './templates/table/table.directive';
import { spTableWidgetConfig } from './templates/table/table-config.component';
import { TableDataModel } from './templates/table/table-data-model.service';

import spLineWidget from './templates/line/line.directive';
import { spLineWidgetConfig } from './templates/line/line-config.component';
import { LineDataModel } from './templates/line/line-data-model.service';

import spGaugeWidget from './templates/gauge/gauge.directive';
import { spGaugeWidgetConfig } from './templates/gauge/gauge-config.component';
import { GaugeDataModel } from './templates/gauge/gauge-data-model.service';

import spTrafficlightWidget from './templates/trafficlight/trafficlight.directive';
import { spTrafficlightWidgetConfig } from './templates/trafficlight/trafficlight-config.component';
import { TrafficLightDataModel } from './templates/trafficlight/trafficlight-data-model.service';

import spRawWidget from './templates/raw/raw.directive';
import { spRawWidgetConfig } from './templates/raw/raw-config.component';
import { RawDataModel } from './templates/raw/raw-data-model.service';
//
import spMapWidget from './templates/map/map.directive';
import { spMapWidgetConfig } from './templates/map/map-config.component';
import { MapDataModel } from './templates/map/map-data-model.service';

import spHeatmapWidget from './templates/heatmap/heatmap.directive';
import { spHeatmapWidgetConfig } from './templates/heatmap/heatmap-config.component';
import { HeatmapDataModel } from './templates/heatmap/heatmap-data-model.service';

import spImageWidget from './templates/image/image.directive';
import { spImageWidgetConfig } from './templates/image/image-config.component';
import { ImageDataModel } from './templates/image/image-data-model.service';

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
    .component('spNumberWidgetConfig', spNumberWidgetConfig)
    .service('NumberDataModel', NumberDataModel)

    .directive('spVerticalbarWidget', spVerticalbarWidget)
    .component('spVerticalbarWidgetConfig', spVerticalbarWidgetConfig)
    .service('VerticalbarDataModel', VerticalbarDataModel)

    .directive('spTableWidget', spTableWidget)
    .component('spTableWidgetConfig', spTableWidgetConfig)
    .service('TableDataModel', TableDataModel)

    .directive('spLineWidget', spLineWidget)
    .component('spLineWidgetConfig', spLineWidgetConfig)
    .service('LineDataModel', LineDataModel)

    .directive('spGaugeWidget', spGaugeWidget)
    .component('spGaugeWidgetConfig', spGaugeWidgetConfig)
    .service('GaugeDataModel', GaugeDataModel)

    .directive('spTrafficlightWidget', spTrafficlightWidget)
    .component('spTrafficlightWidgetConfig', spTrafficlightWidgetConfig)
    .service('TrafficLightDataModel', TrafficLightDataModel)

    .directive('spRawWidget', spRawWidget)
    .component('spRawWidgetConfig', spRawWidgetConfig)
    .service('RawDataModel', RawDataModel)

    .directive('spMapWidget', spMapWidget)
    .component('spMapWidgetConfig', spMapWidgetConfig)
    .service('MapDataModel', MapDataModel)

    .directive('spHeatmapWidget', spHeatmapWidget)
    .component('spHeatmapWidgetConfig', spHeatmapWidgetConfig)
    .service('HeatmapDataModel', HeatmapDataModel)

    .directive('spImageWidget', spImageWidget)
    .component('spImageWidgetConfig', spImageWidgetConfig)
    .service('ImageDataModel', ImageDataModel)

	.name;
