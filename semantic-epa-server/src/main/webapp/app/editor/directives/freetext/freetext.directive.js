freetext.$inject = [];

export default function freetext() {

	return {
		restrict: 'E',
		templateUrl: 'modules/editor/templates/freetext/freetext.tmpl.html',
		scope: {
			staticProperty: "="
		},
		link: function (scope) {

		}
	}

};
