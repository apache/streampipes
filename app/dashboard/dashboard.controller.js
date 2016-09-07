DashCtrl.$inject = ['$scope', '$http', '$mdDialog', 'Widgets', 'AddWidget'];

export default function DashCtrl($scope, $http, $mdDialog, Widgets, AddWidget) {
	var couchDbServer = 'http://127.0.0.1:5984';

	$scope.rerender = true;
	var possibleVisualizations = [];

	$http.get(couchDbServer + '/visualization/_all_docs?include_docs=true')
		.success(function(data) {
			possibleVisualizations = data.rows;

			// get the names for each pipeline
			angular.forEach(possibleVisualizations, function(vis) {
				$http.get(couchDbServer + '/pipeline/' + vis.doc.pipelineId)
					.success(function(pipeline) {
						vis.doc.name = pipeline.name;
					});
			});
		});


	$scope.addWidget = function() {
		$mdDialog.show({
			controller: AddWidget,
			templateUrl: 'modules/dashboard/add-widget-template/add-widget-template.html',
			parent: angular.element(document.body),
			clickOutsideToClose:true,
			locals : {
				possibleVisualizations: possibleVisualizations,
				rerenderDashboard: rerenderDashboard
			}
		});

	};

	var widgetDefinitions = Widgets.getAllWidgetDefinitions();

	// TODO Helper to add new Widgets to the dashboard
	// Find a better solution
	var rerenderDashboard = function() {
		$scope.rerender = false;
		setTimeout(function() {
			$scope.$apply(function () {
				$scope.dashboardOptions.widgetDefinitions = Widgets.getAllWidgetDefinitions();
				$scope.rerender = true;
			});
		}, 100);
	}


	//var widgetDefinitions = [
	//{
	//name: 'wt-time',
	//style: {
	//width: '33%'
	//}
	//}, {
	//name: 'wt-random',
	//style: {
	//width: '33%'
	//}
	//}
	//];


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

};
