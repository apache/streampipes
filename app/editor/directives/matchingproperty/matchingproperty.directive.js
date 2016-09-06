matchingProperty.$inject = [];

export default function matchingProperty() {

	return {
		restrict: 'E',
		templateUrl: 'modules/editor/templates/matchingproperty/matchingproperty.tmpl.html',
		scope: {
			staticProperty: "="
		},
		link: function (scope) {

		}
	}

};
