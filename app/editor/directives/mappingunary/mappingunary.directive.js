mappingPropertyUnary.$inject = [];

export default function mappingPropertyUnary() {

	return {
		restrict : 'E',
		templateUrl : 'modules/editor/templates/mappingunary/mappingunary.tmpl.html',
		scope : {
			staticProperty : "="
		},
		link: function (scope) {


		}
	}

};
