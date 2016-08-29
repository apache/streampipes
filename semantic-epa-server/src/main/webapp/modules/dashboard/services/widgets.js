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

		var name = widget.vis.name + '_' + widget.visType;
		var directive = getDirectiveName(widget.visType);
		
		return {
			name: name,
			directive: directive,
			title: widget.id,
			dataAttrName: 'data',
			dataModelType: SocketConnectionDataModel,
			dataModelArgs: widget.id,
			attrs: {
			          'widget-id': widget.id
			        },
			style: {
				width: '30%'
			}
		}
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
		getWidgetDefinition: getWidgetDashboardDefinition
	};
}]);
