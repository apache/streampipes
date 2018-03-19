nagPrism.$inject = ['$compile'];

declare const Prism: any;

export default function nagPrism($compile) {
	return {
		restrict: 'A',
		transclude: true,
		scope: {
			source: '@'
		},
		link: function(scope, element, attrs, controller, transclude) {
			scope.$watch('source', function(v) {
				v = scope.escape(v);
				element.find("code").html(v);

				Prism.highlightElement(element.find("code")[0]);
			});

			scope.entityMap = {
				"&": "&amp;",
				"<": "&lt;",
				">": "&gt;",
				'"': '&quot;',
				"'": '&#39;',
				"/": '&#x2F;'
			};

			scope.escape = function(str) {
				return String(str).replace(/[&<>"'\/]/g, function (s) {
					return scope.entityMap[s];
				});
			}

			transclude(function(clone) {
				if (clone.html() !== undefined) {
					element.find("code").html(clone.html());
					$compile(element.contents())(scope.$parent);
				}
			});
		},
		template: "<code></code>"
	};
};
