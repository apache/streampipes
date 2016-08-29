
angular.module('streamPipesApp')
	.controller('DashCtrl', ['$rootScope', '$scope', '$http', '$mdDialog', 'Widgets', function($rootScope, $scope, $http, $mdDialog, Widgets) {
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
				controller: AddWidgetController,
				templateUrl: 'modules/dashboard/add-widget-template/add-widget-template.html',
				parent: angular.element(document.body),
				clickOutsideToClose:true,
				locals : {
					possibleVisualizations: possibleVisualizations
				}
			});

		};

		// TODO Helper to add new Widgets to the dashboard
		// A better solution is needed
		var rerenderDashboard = function() {
			$scope.rerender = false;
			setTimeout(function() {
				$scope.$apply(function () {
					$scope.rerender = true;
				});
			}, 100);
		}

		function AddWidgetController($scope, $mdDialog, possibleVisualizations) {

			$scope.page = 'select-viz';

			$scope.possibleVisualizations = possibleVisualizations;
			$scope.selectedVis = {};

			$scope.possibleVisTypes = ['table'];
			$scope.selectedVisType = '';

			$scope.next = function() {
				if ($scope.page == 'select-viz') {
					$scope.page = 'select-type';
				} else if ($scope.page == 'select-type') {
					$scope.page = 'select-scheme';
				} else {
					var selectedProperties = [];
					$scope.selectedVis.schema.eventProperties.forEach(function(entry) {
						if (entry.isSelected) {
							selectedProperties.push(entry);
						}
					});

					var widget = {};
					widget.selectedProperties = selectedProperties;
					widget.visType = $scope.selectedType;
					widget.vis = $scope.selectedVis;
					widget.id = $scope.selectedVis._id;

					Widgets.add(widget);

					widgetDefinitions.push(
						Widgets.getWidgetDefinition(widget.id)
						);

					rerenderDashboard();

					$mdDialog.cancel();
				}
			}

			$scope.cancel = function() {
				$mdDialog.cancel();
			};
		}


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
