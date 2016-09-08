any.$inject = [];

export default function any() {
	return {
		restrict : 'E',
		templateUrl : 'app/editor/directives/any/any.tmpl.html',
		scope : {
			staticProperty : "="
		},
		link: function (scope) {

			scope.toggle = function(option, options) {
				console.log(option.name);
				angular.forEach(options, function(o) {
					if (o.elementId === option.elementId) {
						o.selected = !o.selected;
					}
				});
			}

			scope.exists = function(option, options) {
				angular.forEach(options, function(o) {
					if (o.elementId === option.elementId) {
						return option.selected;
					}
				});
			}

		}
	}

};
