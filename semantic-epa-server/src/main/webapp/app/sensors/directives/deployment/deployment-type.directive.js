export default function deploymentType() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/deployment/deployment-type.tmpl.html',
		scope : {
			disabled : "=",
			deployment : "="
		},
		controller: function($scope, $element) {


		}
	}
};
