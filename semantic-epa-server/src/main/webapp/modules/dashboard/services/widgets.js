'use strict';

angular.module('streamPipesApp').factory('Widgets', ['$http','SocketConnectionDataModel', function ($http, SocketConnectionDataModel) {

		var widgets = new Array();
    var client;


		var createNewWidget = function(widget) {
			widgets[widget.id] = widget;
		}

		var getWidgetById = function(id) {
			return widgets[id];	
		}

		var getWidgetDashboardDefinition = function(id) {
			var widget = getWidgetById(id);
			

			return {
				name: 'wt-top-n',
				title: widget.id,
				dataAttrName: 'data',
				dataModelType: SocketConnectionDataModel,
				dataModelArgs: widget.id,
				style: {
					width: '30%'
				}
			}
		}

		return {
			add: createNewWidget,
			get: getWidgetById,	
			getWidgetDefinition: getWidgetDashboardDefinition
		};
}]);
