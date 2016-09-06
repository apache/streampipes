replaceOutput.$inject = [];

export default function replaceOutput() {
	return {
		restrict : 'E',
		templateUrl : 'modules/editor/templates/replaceoutput/replaceoutput.tmpl.html',
		scope : {
			outputStrategy : "="
		},
		link: function (scope) {

		}
	}

};
