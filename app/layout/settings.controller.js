SettingsCtrl.$inject = ['$rootScope', '$scope', 'rhrtApi', '$mdToast'];

export default function SettingsCtrl($rootScope, $scope, RestApi, $mdToast) {

		$scope.loading = false;

		$scope.setup = {};

		$scope.loadConfig = function() {
			RestApi.getConfiguration().success(function(msg) {
				$scope.setup = msg;
			});
		}

		$scope.configure = function() {
			$scope.loading = true;
			RestApi.updateConfiguration($scope.setup).success(function(data) {
				$rootScope.appConfig = $scope.setup.appConfig;
				$scope.loading = false;
				$scope.showToast(data.notifications[0].title);
			}).error(function(data) {
				$scope.loading = false;
				$scope.showToast("Fatal error, contact administrator");
			});
		}
		
		$scope.addPod = function(podUrls) {
			if (podUrls == undefined) podUrls = [];
			podUrls.push("localhost");
		}
		
		$scope.removePod = function(podUrls, index) {
			podUrls.splice(index, 1);
		}

		$scope.showToast = function(string) {
			$mdToast.show(
				$mdToast.simple()
					.content(string)
					.position("right")
					.hideDelay(3000)
			);
		};

		$scope.loadConfig();

	}
