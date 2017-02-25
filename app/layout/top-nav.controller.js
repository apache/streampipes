TopNavCtrl.$inject = ['$scope', '$rootScope', 'restApi', '$sce', '$state'];

export default function TopNavCtrl($scope, $rootScope, restApi, $sce, $state) {

	$scope.panddaUrl = "";
	$scope.streamStoryUrl = "";
	$scope.hippoUrl = "";
	$scope.humanMaintenanceReportUrl = "";
	$scope.humanInspectionReportUrl = "";

	$scope.trustSrc = function(src) {
		return $sce.trustAsResourceUrl(src);
	}

	$scope.loadConfig = function() {
		restApi.getConfiguration().success(function(msg) {
			$scope.panddaUrl = msg.panddaUrl;
			$scope.streamStoryUrl = msg.streamStoryUrl;
			$scope.hippoUrl = msg.hippoUrl;
			$scope.humanInspectionReportUrl = msg.humanInspectionReportUrl;
			$scope.humanMaintenanceReportUrl = msg.humanMaintenanceReportUrl;
		});
	}

	$scope.logout = function() {
			restApi.logout().then(function() {
			$scope.user = undefined;
			$rootScope.authenticated = false;
			$state.go("login");
		});
	};	

	$scope.loadConfig();
};
