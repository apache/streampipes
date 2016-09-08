sepaBasics.$inject = [];

export default function sepaBasics() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/sepa/sepa-basics.tmpl.html',
		scope : {
			element : "=element",
			disabled : "=disabled"
		}
	}
}; 
