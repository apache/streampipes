export class AppFileDownloadCtrl {

    appFileDownloadRestApiService: any;
    newFile: any;
    allFiles: any;
    availableIndices: any;
    $rootScope: any;

    constructor($rootScope, AppFileDownloadRestApi) {
        this.$rootScope = $rootScope;
        this.appFileDownloadRestApiService = AppFileDownloadRestApi;
        this.newFile = {};
        this.allFiles = [];
        this.availableIndices = [];

    }

    $onInit() {
        this.getFiles();
        this.getAvailableIndices();

        this.$rootScope.$on("UpdateFiles", (event, item) => {
            this.getFiles();
        });
    }

    getAvailableIndices() {
        this.appFileDownloadRestApiService.getIndices()
            .then(indices => {
                this.availableIndices = indices.data;
                console.log(indices.data);
            });
    }

    getFiles() {
        this.appFileDownloadRestApiService.getAll()
            .then(allFiles => {
                this.allFiles = allFiles.data;
            });

    };

    createNewFile(file) {
        var start = new Date(file.timestampFrom).getTime();
        var end = new Date(file.timestampTo).getTime();
        var output = file.output;
        this.appFileDownloadRestApiService.createFile(file.index, start, end, output).then(msg => {
            this.getFiles();
        });
    };
}

AppFileDownloadCtrl.$inject = ['$rootScope', 'AppFileDownloadRestApi'];
