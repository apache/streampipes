export class AppFileDownloadCtrl {

    constructor(AppFileDownloadRestApi) {
        this.appFileDownloadRestApiService = AppFileDownloadRestApi;
        this.newFile = {};
        this.allFiles = [];
        this.getFiles();
    }


    deleteFile(fileName) {
        this.appFileDownloadRestApiService.removeFile(fileName).success(function () {
            this.getFiles();
        });
    }

    downloadFile(fileName) {
        this.appFileDownloadRestApiService.getFile(fileName);
    }

    getFiles() {
        const self = this;
        this.appFileDownloadRestApiService.getAll()
            .success(function (allFiles) {
                self.allFiles = allFiles;
            })
            .error(function (msg) {
                console.log(msg);
            });

    };

    createNewFile(file) {
        var start = new Date(file.timestampFrom).getTime();
        var end = new Date(file.timestampTo).getTime();
        this.appFileDownloadRestApiService.createFile(file.index, start, end).success(function (err, res) {
            $scope.getFiles();
        });
    };
}

AppFileDownloadCtrl.$inject = ['AppFileDownloadRestApi'];
