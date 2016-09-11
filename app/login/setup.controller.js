SetupCtr.$inject = ['$scope', '$location', 'restApi', '$mdToast'];

export default function SetupCtr($scope, $location, restApi, $mdToast) {

		$scope.installationFinished = false;
		$scope.installationSuccessful = false;
		$scope.installationResults = [{}];
		$scope.loading = false;


		$scope.setup = {
			couchDbHost: 'localhost',
			couchDbProtocol : 'http',
			couchDbPort : 5984,
			couchDbUserDbName : 'users',
			couchDbPipelineDbName : 'pipeline',
			couchDbConnectionDbName : 'connection',
			couchDbMonitoringDbName : 'monitoring',
			couchDbNotificationDbName : 'notification',
			sesameUrl: 'http://localhost:8080/openrdf-sesame',
			sesameDbName : 'test-6',
			kafkaProtocol : 'http',
			kafkaHost : $location.host(),
			kafkaPort : 9092,
			zookeeperProtocol : 'http',
			zookeeperHost : $location.host(),
			zookeeperPort : 2181,
			jmsProtocol : 'tcp',
			jmsHost : $location.host(),
			jmsPort : 61616,
			adminUsername: '',
			adminEmail: '' ,
			adminPassword: '',
			streamStoryUrl : '',
			panddaUrl : '',
			hippoUrl : '',
			humanInspectionReportUrl : '',
			humanMaintenanceReportUrl : '',
			appConfig : 'StreamPipes',
			marketplaceUrl : '',
			podUrls : []
		};

		$scope.configure = function() {
			$scope.loading = true;
			restApi.setupInstall($scope.setup).success(function(data) {
				$scope.installationFinished = true;
				$scope.installationResults = data;
				$scope.loading = false;
			}).error(function(data) {
				$scope.loading = false;
				$scope.showToast("Fatal error, contact administrator");
			});
		}

		$scope.showToast = function(string) {
			$mdToast.show(
				$mdToast.simple()
					.content(string)
					.position("right")
					.hideDelay(3000)
			);
		};

		$scope.addPod = function(podUrls) {
			if (podUrls == undefined) podUrls = [];
			podUrls.push("localhost");
		}

		$scope.removePod = function(podUrls, index) {
			podUrls.splice(index, 1);
		}
	};
