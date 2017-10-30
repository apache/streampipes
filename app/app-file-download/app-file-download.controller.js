AppFileDownloadCtrl.$inject = ['$scope', 'appFileDownloadRestService', '$window'];

export default function AppFileDownloadCtrl($scope, appFileDownloadRestService, $window) {

    $scope.allFiles = appFileDownloadRestService.getAll();


    // $scope.res = appFileDownloadRestService.createFile("testfeld",1506416708, 1506418628);

    // $scope.applicationLinks = [];

    // var loadApplicationLinks = function() {
    //     restApi.getApplicationLinks()
    //         .success(function (applicationLinks) {
    //             $scope.applicationLinks = applicationLinks;
    //         })
    //         .error(function (error) {
    //             console.log(error);
    //         });
    // }

    // $scope.openApp = function(applicationUrl) {
    //     $window.open(applicationUrl, "_blank");
    // }

    // loadApplicationLinks();
}