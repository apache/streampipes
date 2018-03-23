myDataBind.$inject = ['$rootScope'];

export default function myDataBind($rootScope) {
	return {
		restrict: 'A',
		link: function (scope, elem, attrs) {
			elem.data("JSON", scope.element);
			elem.attr({'data-toggle': "tooltip", 'data-placement': "top", 'title': scope.element.properties.name});
			elem.tooltip();
			if (scope.$last) {
				$rootScope.$broadcast("elements.loaded");
			}
		}
	}
};
