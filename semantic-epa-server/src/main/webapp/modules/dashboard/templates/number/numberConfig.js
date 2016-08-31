'use strict';

angular.module('streamPipesApp')
	.directive('spNumberWidgetConfig',function () {
		return {
			restrict: 'E',
			templateUrl: 'modules/dashboard/templates/number/numberConfig.html',
			scope: {
				wid: '='
			}
		};
	});
