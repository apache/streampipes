'use strict';

spNumberWidgetConfig.$inject = [];

export default function spNumberWidgetConfig() {
	return {
		restrict: 'E',
		templateUrl: 'app/dashboard/templates/number/numberConfig.html',
		scope: {
			wid: '='
		}
	};
};
