options.$inject = [];

export default function options() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/general/options.tmpl.html',
		scope : {
			options : "=element",
			disabled : "=disabled"
		},
		link: function($scope, element, attrs) {

			$scope.addOption = function(options) {   
				if (options == undefined) options = [];
				options.push({"name" : ""});
			}

			$scope.removeOption = function(options, index) {
				options.splice(index, 1);
			};
		}		
	}
};
