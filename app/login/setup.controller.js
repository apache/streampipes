SetupCtr.$inject = ['$scope', '$rootScope', '$location', 'restApi', '$mdToast'];

export default function SetupCtr($scope, $rootScope, $location, restApi, $mdToast) {

    $scope.installationFinished = false;
    $scope.installationSuccessful = false;
    $scope.installationResults = [{}];
    $scope.loading = false;
		$scope.showAdvancedSettings = false;


    $scope.setup = {
        couchDbHost: '',
        sesameHost: '',
        kafkaHost: '', 
        zookeeperHost: '',
        jmsHost: '',
        adminEmail: '',
        adminPassword: '',
    };


    $scope.configure = function () {
        $scope.loading = true;
        restApi.setupInstall($scope.setup).success(function (data) {
            $scope.installationResults = data;

            restApi.configured()
                .then(function (response) {
                    if (response.data.configured) {
                        $rootScope.appConfig = response.data.appConfig;
                        $scope.installationFinished = true;
                        $scope.loading = false;
                    }
                }).error(function (data) {
                $scope.loading = false;
                $scope.showToast("Fatal error, contact administrator");
            });
        });
    }

    $scope.showToast = function (string) {
        $mdToast.show(
            $mdToast.simple()
                .content(string)
                .position("right")
                .hideDelay(3000)
        );
    };

    $scope.addPod = function (podUrls) {
        if (podUrls == undefined) podUrls = [];
        podUrls.push("localhost");
    }

    $scope.removePod = function (podUrls, index) {
        podUrls.splice(index, 1);
    }
};
