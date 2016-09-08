mappingPropertyUnary.$inject = [];

export default function mappingPropertyUnary() {

	return {
		restrict : 'E',
		templateUrl : 'app/editor/directives/mappingunary/mappingunary.tmpl.html',
		scope : {
			staticProperty : "="
		},
		link: function (scope) {


		}
	}

};
