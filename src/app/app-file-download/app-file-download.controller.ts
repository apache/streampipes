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
        this.getFiles();
        this.getAvailableIndices();

        $rootScope.$on("UpdateFiles", (event, item) => {
           this.getFiles();
        });
    }

    getAvailableIndices() {
        this.appFileDownloadRestApiService.getIndices()
            .success(indices => {
                this.availableIndices = indices;
                console.log(indices);
            })
            .error(msg => {
                console.log(msg);
            });
    }

    getFiles() {
        this.appFileDownloadRestApiService.getAll()
            .success(allFiles => {
                this.allFiles = allFiles;
            })
            .error(msg => {
                console.log(msg);
            });

    };

    createNewFile(file) {
        var start = new Date(file.timestampFrom).getTime();
        var end = new Date(file.timestampTo).getTime();
        var output = file.output;
        this.appFileDownloadRestApiService.createFile(file.index, start, end, output).success((err, res) => {
            this.getFiles();
        });
    };
}

AppFileDownloadCtrl.$inject = ['$rootScope', 'AppFileDownloadRestApi'];
