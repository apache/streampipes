AppFileDownloadCtrl.$inject = ['$scope', 'appFileDownloadRestService', '$window'];

export default function AppFileDownloadCtrl($scope, appFileDownloadRestService, $window) {

    $scope.newFile = {};

    $scope.allFiles = [];

    $scope.deleteFile = function (fileName) {
        appFileDownloadRestService.removeFile(fileName).success(function () {
            $scope.getFiles();
        });
    }

    $scope.downloadFile = function (fileName) {
        appFileDownloadRestService.getFile(fileName);
    }

    $scope.getFiles = function () {
        appFileDownloadRestService.getAll()
            .success(function (allFiles) {
                $scope.allFiles = allFiles;
            })
            .error(function (msg) {
                console.log(msg);
            });

    };

    $scope.createNewFile = function(file) {
        var start = new Date(file.timestampFrom).getTime();
        var end = new Date(file.timestampTo).getTime();
        appFileDownloadRestService.createFile(file.index, start, end).success(function (err, res) {
            $scope.getFiles();
        });
    };

    $scope.getFiles();

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