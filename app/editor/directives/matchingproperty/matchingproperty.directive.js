matchingProperty.$inject = [];

export default function matchingProperty() {

	return {
		restrict: 'E',
		templateUrl: 'app/editor/directives/matchingproperty/matchingproperty.tmpl.html',
		scope: {
			staticProperty: "="
		},
		link: function (scope) {

		}
	}

};
