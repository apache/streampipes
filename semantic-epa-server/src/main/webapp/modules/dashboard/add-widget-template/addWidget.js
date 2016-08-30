angular.module('streamPipesApp')
	.factory('AddWidget', ['Widgets', '$compile', function(Widgets, $compile) {

		function AddWidget($scope, $mdDialog, possibleVisualizations, rerenderDashboard) {
			$scope.page = 'select-viz';

			$scope.possibleVisualizations = angular.copy(possibleVisualizations);

			// This is the object that the user manipulates
			$scope.selectedVis = {};

			$scope.possibleVisTypes = ['table'];
			$scope.selectedVisType = '';

			$scope.next = function() {
				if ($scope.page == 'select-viz') {
					$scope.page = 'select-type';
				} else if ($scope.page == 'select-type') {
					$scope.page = 'select-scheme';

					var directiveName = 'sp-' + $scope.selectedType + '-widget-config'
					var widgetConfig = $compile( '<'+ directiveName + ' wid=selectedVis></' + directiveName + '>')( $scope );

					var schemaSelection = angular.element( document.querySelector( '#scheme-selection' ) );
					schemaSelection.append( widgetConfig );

				} else {

					var widget = {};
					widget.visType = $scope.selectedType;
					widget.vis = $scope.selectedVis;
					widget.id = $scope.selectedVis._id;

					Widgets.add(widget);

					//widgetDefinitions.push(
						//Widgets.getWidgetDefinition(widget.id)
					//);

					rerenderDashboard();

					$mdDialog.cancel();
				}
			}

			$scope.cancel = function() {
				$mdDialog.cancel();
			};
		};

		return AddWidget;
	}]);
