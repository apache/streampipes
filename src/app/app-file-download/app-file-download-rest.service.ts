export class AppFileDownloadRestApi {

    $http: any;
    $window: any;
    apiConstants: any;

    constructor($http, apiConstants, $window)   {
        this.$http = $http;
        this.$window = $window;
        this.apiConstants = apiConstants;
    }


    getServerUrl() {
        return this.apiConstants.contextPath + "/api/apps/v1/elasticsearch";
    }

    getIndices() {
        return this.$http.get(this.getServerUrl() + "/indices");
    }

    getAll() {
        return this.$http.get(this.getServerUrl() +'/files');
    }

    getFile(fileName) {
        this.$window.open(this.getServerUrl() + '/file/' + fileName);
    }

    removeFile(fileName) {
        return this.$http({
            method: 'DELETE',
            url: this.getServerUrl() + "/file/" + fileName
        })
    }
    
    createFile(index, timestampFrom, timestampTo, output) {

        var postObject = {};
        postObject['index'] = index;
        postObject['timestampFrom'] = timestampFrom;
        postObject['timestampTo'] = timestampTo;
        postObject['output'] = output;

        return this.$http({
            method: 'POST',
            dataType: 'json',
            url: this.getServerUrl() + "/file",
            data: postObject,
            headers: {'Content-Type': 'application/json'}
        })
    }

};

AppFileDownloadRestApi.$inject = ['$http', 'apiConstants', '$window'];
