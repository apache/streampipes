'use strict';

spLineWidgetConfig.$inject = [];

export default function spLineWidgetConfig() {
	return {
		restrict: 'E',
		templateUrl: 'app/dashboard/templates/line/lineConfig.html',
		scope: {
			wid: '='
		}
	};
};
