'use strict';
WidgetInstances.$inject = ['$http', 'WidgetTemplates', '$q'];

function getId() {
	return Math.floor((1 + Math.random()) * 0x10000);
}

export default function WidgetInstances($http, WidgetTemplates, $q) {
	var storedData = {};



	var getWidgets = function() {
		//return $http.get('/dashboard/_all_docs?include_docs=true')
		//.success(function(data) {
		//// TODO when collection is empty
		//storedData = data.rows[0].doc;
		//});

		//return {};
		//return storedData;

		return $q(function(resolve, reject){
			resolve(storedData);	
		});
	}

	//var persistWidgets = function() {
	//var toSave = {
	//_id: "1",	
	//data: getWidgets()};
	//$http.post('/dashboard', toSave).then(function() {
	//console.log('Yeah')	;
	//}, function(err) {
	//console.log(err);	
	//});

	//}

	var createNewWidget = function(widget) {
		var id = getId();
		widget.id = id;
		//TODO
		storedData[id] = widget;
		//persistWidgets();
	}

	//TODO
	var removeWidget = function(widgetId) {
		delete getWidgets()[widgetId];
	}

	var getWidgetById = function(id) {
		return getWidgets().then(function(data) {
			return data[id];	
		});	
	}

	var getWidgetDashboardDefinition = function(id) {
		var widget = storedData[id];

		var name = widget.visualisation.name + '[' + widget.visualisationType + ']';
		var directive = WidgetTemplates.getDirectiveName(widget.visualisationType);
		var dataModel = WidgetTemplates.getDataModel(widget.visualisationType);

		return {
			name: name,
			directive: directive,
			title: widget.id,
			dataAttrName: 'data',
			dataModelType: dataModel,
			dataModelArgs: widget.visualisationId,
			//attrs: {
			//'widget': widgetString 
			//},
			attrs: {
				'widget-id': widget.id
			},
			style: {
				width: '30%'
			}
		}
	}

	var getAllLayoutWidgetDefinitions = function(layoutId) {
		var result = [];
		//angular.forEach(storedData, function(w, key) {
		//if (w.layoutId == layoutId) {
		//result.push(getWidgetDashboardDefinition(key));	
		//}
		//});

		return result;
	}

	var getAllWidgetDefinitions = function() {
		var result = [];

		return getWidgets().then(function(data) {
			angular.forEach(data, function(w, key) {
				result.push(getWidgetDashboardDefinition(key));	
			});

			return result;
		});

	}

	return {
		add: createNewWidget,
		remove: removeWidget,
		get: getWidgetById,	
		getAllLayoutWidgetDefinitions: getAllLayoutWidgetDefinitions,
		getAllWidgetDefinitions: getAllWidgetDefinitions
	};
};
