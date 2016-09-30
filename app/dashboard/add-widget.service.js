AddWidget.$inject = ['Widgets', '$compile', 'WidgetDefinitions'];

export default function AddWidget(Widgets, $compile, WidgetDefinitions) {

	function AddWidget($scope, $mdDialog, possibleVisualizations, rerenderDashboard) {
		$scope.page = 'select-viz';

		$scope.pages = [{
			type : "select-viz",
			title : "Data Stream",
			description : "Select a data stream you'd like to visualize"
		},{
			type : "select-type",
			title : "Visualization Type",
			description : "Select a visualization type"
		},{
			type : "select-scheme",
			title : "Visualization Settings",
			description : "Customize your visualization"
		}];

		$scope.getTabCss = function(page) {
			if (page == $scope.page) return "md-fab md-accent";
			else return "md-fab md-accent wizard-inactive";
		}

		$scope.possibleVisualizations = angular.copy(possibleVisualizations);

		// This is the object that the user manipulates
		$scope.selectedVis = {};

		$scope.possibleVisTypes = WidgetDefinitions.getAllNames();
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
};
