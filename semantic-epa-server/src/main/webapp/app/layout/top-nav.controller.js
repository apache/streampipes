TopNavCtrl.$inject = ['$scope', '$rootScope', 'restApi', '$sce', '$http', '$state'];

export default function TopNavCtrl($scope, $rootScope, restApi, $sce, $http, $state) {
	//.controller('TopNavCtrl', function($scope, $rootScope, restApi, $sce, $http, $state) {

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
		$http.get("/semantic-epa-backend/api/v2/admin/logout").then(function() {
			$scope.user = undefined;
			$rootScope.authenticated = false;
			$state.go("streampipes.login");
		});
	};	

	$scope.loadConfig();


};
