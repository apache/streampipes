export class AppFileDownloadCtrl {

    appFileDownloadRestApiService: any;
    newFile: any;
    allFiles: any;
    availableIndices: any;
    $rootScope: any;
    searchIndex: any;

    constructor($rootScope, AppFileDownloadRestApi) {
        this.$rootScope = $rootScope;
        this.appFileDownloadRestApiService = AppFileDownloadRestApi;
        this.newFile = {};
        this.allFiles = [];
        this.availableIndices = [];
        this.getFiles();
        this.getAvailableIndices();


        $rootScope.newFile = {
            allData: 'true',
            output: 'json'
        }

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
        if(file.index !== null) {
            var start = new Date(file.timestampFrom).getTime();
            var end = new Date(file.timestampTo).getTime();
            var output = file.output;
            var allData = file.allData;
            this.appFileDownloadRestApiService.createFile(file.index.indexName, start, end, output, allData).success((err, res) => {
                this.getFiles();
            });
        }
    };

    querySearch (query) {
        var filterValue = query.toLowerCase();

        return this.availableIndices.filter(index => index.indexName.toLowerCase().indexOf(filterValue) >= 0);
    }
}

AppFileDownloadCtrl.$inject = ['$rootScope', 'AppFileDownloadRestApi'];
