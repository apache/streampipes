streamRestriction.$inject = [];

export default function streamRestriction() {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/restriction/stream-restriction.tmpl.html',
		scope : {
			streams : "=element",
			disabled : "=disabled"
		},
		link: function($scope, element, attrs) {

			$scope.addStreamRestriction = function(streams) {
				if (streams == undefined) streams = [];
				streams.push({"eventSchema" : {"eventProperties" : []}});
			}

			$scope.removeStreamRestriction = function(streamIndex, streams) {
				streams.splice(streamIndex, 1);
			};
		}
	}
};
