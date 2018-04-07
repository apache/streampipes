
export class FileDetailsController {

    file: any;
    appFileDownloadRestApiService: any;
    $rootScope: any;

    constructor($rootScope, AppFileDownloadRestApi) {
        this.$rootScope = $rootScope;
        this.appFileDownloadRestApiService = AppFileDownloadRestApi;
    }

    deleteFile(fileName) {
        this.appFileDownloadRestApiService.removeFile(fileName).success(() => {
            this.$rootScope.$broadcast("UpdateFiles");
        });
    }

    downloadFile(fileName) {
        this.appFileDownloadRestApiService.getFile(fileName);
    }
}

FileDetailsController.$inject = ['$rootScope', 'AppFileDownloadRestApi'];