AppLinksCtrl.$inject = ['$scope', 'RestApi', '$window'];

export default function AppLinksCtrl($scope, RestApi, $window) {

    $scope.applicationLinks = [];

    var loadApplicationLinks = function() {
        RestApi.getApplicationLinks()
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