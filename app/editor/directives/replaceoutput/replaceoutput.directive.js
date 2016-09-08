replaceOutput.$inject = [];

export default function replaceOutput() {
	return {
		restrict : 'E',
		templateUrl : 'app/editor/directives/replaceoutput/replaceoutput.tmpl.html',
		scope : {
			outputStrategy : "="
		},
		link: function (scope) {

		}
	}

};
