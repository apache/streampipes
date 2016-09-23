'use strict';
WidgetInstances.$inject = ['$http', 'WidgetTemplates', '$q'];

function getId() {
	return Math.floor((1 + Math.random()) * 0x10000);
}

export default function WidgetInstances($http, WidgetTemplates, $q) {
	//var storedData = {};



	var getWidgets = function() {
		return $http.get('/dashboard/_all_docs?include_docs=true').then(function(data) {
				var result = [];
				angular.forEach(data.data.rows, function(d) {
					result.push(d.doc);
				});

				return result;
			});

		//return {};
		//return storedData;

		//return $q(function(resolve, reject){
		//resolve(storedData);	
		//});
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
		$http.post('/dashboard', widget).then(function() {
			//console.log('Yeah')	;
		}, function(err) {
			console.log(err);	
		});
	}

	var removeWidget = function(widget) {
		//$http.post('/dashboard', widget)
		return $http.delete('/dashboard/'+ widget._id + '?rev=' + widget._rev);
	}

	var getWidgetById = function(id) {
		return getWidgets().then(function(data) {
			var result = _.filter(data, function(d) {
				return d.id == id;	
			});

			return result[0];
		});	
	}

	var getWidgetDashboardDefinition = function(widget) {
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
			attrs: {
				'widget-id': widget.id
			},
			style: {
				width: '30%'
			},
			layoutId: widget.layoutId
		}
	}

	var getAllWidgetDefinitions = function() {
		var result = [];

		return getWidgets().then(function(data) {
			angular.forEach(data, function(w, key) {
				result.push(getWidgetDashboardDefinition(w));	
			});

			return result;
		});

	}

	return {
		add: createNewWidget,
		remove: removeWidget,
		get: getWidgetById,	
		getAllWidgetDefinitions: getAllWidgetDefinitions
	};
};
