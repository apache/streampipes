'use strict';

angular.module('streamPipesApp')
	.directive('spTableWidgetConfig',function () {
		return {
			restrict: 'E',
			templateUrl: 'modules/dashboard/templates/table/tableConfig.html',
			scope: {
				wid: '='
			}
		};
	});
