AppLinksCtrl.$inject = ['$scope', 'restApi', '$window'];

export default function AppLinksCtrl($scope, restApi, $window) {

    $scope.applicationLinks = [];

    var loadApplicationLinks = function() {
        restApi.getApplicationLinks()
            .success(function (applicationLinks) {
                $scope.applicationLinks = applicationLinks;
            })
            .error(function (error) {
                console.log(error);
            });
    }

    $scope.openApp = function(applicationUrl) {
        $window.open(applicationUrl, "_blank");
    }

    loadApplicationLinks();
}