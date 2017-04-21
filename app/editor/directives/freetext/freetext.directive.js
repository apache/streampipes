freetext.$inject = [];

export default function freetext() {

	return {
		restrict: 'E',
		templateUrl: 'app/editor/directives/freetext/freetext.tmpl.html',
		scope: {
			staticProperty: "=",
			inputStreams : "="
		},
		link: function (scope) {
			if (scope.staticProperty.properties.valueSpecification) {
				scope.staticProperty.properties.value = (scope.staticProperty.properties.value*1);
			}
		}
	}

};
