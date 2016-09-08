requiredPropertyValues.$inject = [];

export default function requiredPropertyValues() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/property/required-property-values.tmpl.html',
		scope : {
			property : "=",
			disabled : "=disabled"
		}
	}
}; 
