'use strict';
spTableWidgetConfig.$inject = [];

export default function spTableWidgetConfig() {
	return {
		restrict: 'E',
		templateUrl: 'app/dashboard/templates/table/tableConfig.html',
		scope: {
			wid: '='
		}
	};
};
