import angular from 'npm/angular';

import stomp from '../../lib/stomp';
import '../../lib/mlhr-table';
//import '../../lib/malhar-angular-widgets';
import '../../lib/malhar-angular-dashboard';

import DashboardCtrl from './dashboard.controller';
import AddWidgetService from './add-widget.service';
import SocketConnectionDataModel from './socket-connection-data-model.service';
import Widgets from './widgets.service';
import WidgetDefinitions from './templates/widget-definitions.service';
import sonumber from './templates/so-number.filter';


import spNumberWidget from './templates/number/number.directive';
import spNumberWidgetConfig from './templates/number/number-config.directive';
import NumberDataModel from './templates/number/number-data-model.service';

import spTableWidget from './templates/table/table.directive';
import spTableWidgetConfig from './templates/table/table-config.directive';
import TableDataModel from './templates/table/table-data-model.service';



export default angular.module('sp.dashboard', ['ui.dashboard'])
	.controller('DashboardCtrl', DashboardCtrl)
	.factory('AddWidget', AddWidgetService)
	.factory('SocketConnectionDataModel', SocketConnectionDataModel)
	.factory('Widgets', Widgets)
	.factory('WidgetDefinitions', WidgetDefinitions)
	.filter('sonumber', sonumber)

	.directive('spNumberWidget', spNumberWidget)
	.directive('spNumberWidgetConfig', spNumberWidgetConfig)
	.factory('NumberDataModel', NumberDataModel)
	.directive('spTableWidget', spTableWidget)
	.directive('spTableWidgetConfig', spTableWidgetConfig)
	.factory('TableDataModel', TableDataModel)

	.name;
