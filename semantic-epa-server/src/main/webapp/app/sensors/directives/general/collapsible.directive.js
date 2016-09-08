collapsible.$inject = [];

export default function collapsible() {
		return {
			restrict : 'E',
			templateUrl : 'app/sensors/directives/general/collapsible.tmpl.html',
			transclude: true,
			scope : {
				list : "=list",
				index: "=index",
				disabled: "=disabled",
				removable: "=removable",
				titleLabel: "=titleLabel",
				collapsible: "=collapsible",
				subtitle: "="
			},

			controller: function($scope, $element) {

				$scope.hide = true;

				$scope.toggleVisibility = function() {
					$scope.hide = !$scope.hide;
				}

				$scope.removeProperty = function(list, index) {
					list.splice(index, 1);
				}
			}
		}
	};
