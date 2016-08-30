angular.module('streamPipesApp').factory('WidgetDefinitions', ['TableDataModel', function(TableDataModel) {
	//Register the new widgets here
	var widgetTypes = {
			table: {
				name: 'table',
				directive: 'table-widget',
				dataModel: TableDataModel,
			}	
		}	

	var getDataModel = function(name) {
		return widgetTypes[name].dataModel;	
	}

	var getDirectiveName = function(name) {
		return 	widgetTypes[name].directive;
	}

	var getAllNames = function() {
		var result = [];
		angular.forEach(widgetTypes, function(w) {
			result.push(w.name);
		});

		return result;
	}

	return {
		getDataModel: getDataModel,
		getDirectiveName: getDirectiveName,
		getAllNames: getAllNames
	}
}]);


