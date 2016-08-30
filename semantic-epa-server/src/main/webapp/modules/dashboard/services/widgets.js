'use strict';

angular.module('streamPipesApp').factory('Widgets', ['$http', 'WidgetDefinitions',
	function ($http, WidgetDefinitions) {

	var widgets = new Array();
	var client;

	var createNewWidget = function(widget) {
		var id = widgets.length + 1;
		widgets[id] = widget;
	}

	var getWidgetById = function(id) {
		return widgets[id];	
	}

	var getWidgetDashboardDefinition = function(id) {
		var widget = widgets[id];

		var name = widget.vis.name + '[' + widget.visType + ']';
		var directive = WidgetDefinitions.getDirectiveName(widget.visType);
		var dataModel = WidgetDefinitions.getDataModel(widget.visType);

		return {
			name: name,
			directive: directive,
			title: widget.id,
			dataAttrName: 'data',
			dataModelType: dataModel,
			dataModelArgs: widget.id,
			attrs: {
			          'widget-id': id
			        },
			style: {
				width: '30%'
			}
		}
	}

	var getAllWidgetDefinitions = function() {
		var result = [];

		angular.forEach(widgets, function(w, key) {
			result.push(getWidgetDashboardDefinition(key));	
		});

		return result;
	}

		return {
		add: createNewWidget,
		get: getWidgetById,	
		getAllWidgetDefinitions: getAllWidgetDefinitions
	};
}]);
