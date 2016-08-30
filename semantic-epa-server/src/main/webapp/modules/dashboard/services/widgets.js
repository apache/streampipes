'use strict';

angular.module('streamPipesApp').factory('Widgets', ['$http', 'TableDataModel', 
	function ($http, TableDataModel) {

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
		var directive = getDirectiveName(widget.visType);
		
		return {
			name: name,
			directive: directive,
			title: widget.id,
			dataAttrName: 'data',
			dataModelType: TableDataModel,
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

	var getDirectiveName = function(name) {
		switch(name) {
		    case "table":
						return 'table-widget'
		        break;
		    default:
		        console.log('Widget Directive not supported')
		}
	
	}

	return {
		add: createNewWidget,
		get: getWidgetById,	
		//getWidgetDefinition: getWidgetDashboardDefinition,
		getAllWidgetDefinitions: getAllWidgetDefinitions
	};
}]);
