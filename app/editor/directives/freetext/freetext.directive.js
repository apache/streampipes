freetext.$inject = [];

export default function freetext() {

	return {
		restrict: 'E',
		templateUrl: 'app/editor/directives/freetext/freetext.tmpl.html',
		scope: {
			staticProperty: "="
		},
		link: function (scope) {

		}
	}

};
