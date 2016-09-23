AddWidget.$inject = ['WidgetInstances', '$compile', 'WidgetTemplates'];

export default function AddWidget(WidgetInstances, $compile, WidgetTemplates) {

	function AddWidget($scope, $mdDialog, possibleVisualizations, rerenderDashboard, layoutId) {
		$scope.page = 'select-viz';

		$scope.possibleVisualizations = angular.copy(possibleVisualizations);

		// This is the object that the user manipulates
		$scope.selectedVisualisation = {};

		$scope.possibleVisualisationTypes = WidgetTemplates.getAllNames();
		$scope.selectedVisualisationType = '';

		$scope.next = function() {
			if ($scope.page == 'select-viz') {
				$scope.page = 'select-type';
			} else if ($scope.page == 'select-type') {
				$scope.page = 'select-scheme';

				var directiveName = 'sp-' + $scope.selectedType + '-widget-config'
				var widgetConfig = $compile( '<'+ directiveName + ' wid=selectedVisualisation></' + directiveName + '>')( $scope );

				var schemaSelection = angular.element( document.querySelector( '#scheme-selection' ) );
				schemaSelection.append( widgetConfig );

			} else {

				var widget = {};
				widget.visualisationType = $scope.selectedType;
				widget.visualisationId = $scope.selectedVisualisation._id;
				widget.visualisation = $scope.selectedVisualisation;
				//widget.id = $scope.selectedVisualisation._id;
				widget.layoutId = layoutId;

				WidgetInstances.add(widget);

				//widgetDefinitions.push(
				//WidgetInstances.getWidgetDefinition(widget.id)
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
};
