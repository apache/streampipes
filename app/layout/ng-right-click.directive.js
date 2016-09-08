ngRightClick.$inject = ['$parse'];

export default function ngRightClick($parse) {
	return function(scope, element, attrs) {
		var fn = $parse(attrs.ngRightClick);
		element.bind('contextmenu', function(scope, event) {
			event.preventDefault();
			fn();

		});
	};
};
