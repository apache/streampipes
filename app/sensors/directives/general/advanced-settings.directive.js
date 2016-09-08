advancedSettings.$inject = [];

export default function advancedSettings() {

	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/general/advanced-settings.tmpl.html',
		transclude: true,
		scope : {

		},

		controller: function($scope, $element) {

			$scope.visible = false;

			$scope.showLabel = function() {
				return $scope.visible == true ? "Hide" : "Show";
			}

			$scope.advancedSettingsVisible = function() {
				return $scope.visible;
			}

			$scope.toggleAdvancedSettingsVisibility = function() {
				$scope.visible = !$scope.visible;
			}

		}
	}
};
