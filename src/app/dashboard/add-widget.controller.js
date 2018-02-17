AddWidget.$inject = ['WidgetInstances', '$compile', 'WidgetTemplates', '$http', 'ElementIconText'];

export default function AddWidget(WidgetInstances, $compile, WidgetTemplates, $http, ElementIconText) {


	function AddWidget($scope, $mdDialog, visualizablePipelines, rerenderDashboard, layoutId) {
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

		$scope.iconText = function (elementName) {
			return ElementIconText.getElementIconText(elementName);
		}
		
		$scope.selectPipeline = function(vis) {
			$scope.selectedVisualisation = vis;
		}

		$scope.selectVisType = function(type) {
			$scope.selectedType = type;
		}

		$scope.getSelectedPipelineCss = function(vis) {
			return getSelectedCss($scope.selectedVisualisation, vis);
		}

		$scope.getSelectedVisTypeCss = function(type) {
			return getSelectedCss($scope.selectedType, type);
		}

		var getSelectedCss = function(selected, current) {
			if (selected == current) {
				return "wizard-preview wizard-preview-selected";
			} else {
				return "wizard-preview";
			}
		}

		$scope.getTabCss = function(page) {
			if (page == $scope.page) return "md-fab md-accent";
			else return "md-fab md-accent wizard-inactive";
		}

		$scope.visualizablePipelines = angular.copy(visualizablePipelines);

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
				widget.visualisation = $scope.selectedVisualisation;
				widget.layoutId = layoutId;


				widget.visualisationId = $scope.selectedVisualisation._id;
				WidgetInstances.add(widget);
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
