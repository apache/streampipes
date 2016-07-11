
angular.module('streamPipesApp')
	.controller('DashCtrl', ['$rootScope', '$scope', 'RandomTopNDataModel', function($rootScope, $scope, RandomTopNDataModel) {
		// var widget = {"name" : "pipelineName", "consumerUrl" : "pipelineURL", "size" : {"width" :"50%", "height" : "100%"}, "style" : { }, "template" : "<div>Template from templateUrl<div >{{percentage}}</div></div>"};

		var widgetDefinitions = [
			{
				name: 'wt-time',
				style: {
					width: '33%'
				}
			}, {
				name: 'wt-random',
				style: {
					width: '33%'
				}
			}, {
				name: 'wt-top-n',
				dataAttrName: 'data',
				dataModelType: RandomTopNDataModel,
				style: {
					width: '30%'
				}
			 }
		];


		var defaultWidgets = _.map(widgetDefinitions, function (widgetDef) {
			return {
				name: widgetDef.name
			};
		});
		$scope.dashboardOptions = {
			widgetButtons: true,
			widgetDefinitions: widgetDefinitions,
			defaultWidgets: defaultWidgets
		};
		
	}]);
