export default function deploymentType() {
	return {
		restrict : 'E',
		templateUrl : 'modules/sensors/directives/deployment-type.tmpl.html',
		scope : {
			disabled : "=",
			deployment : "="
		},
		controller: function($scope, $element) {


		}
	}
};
