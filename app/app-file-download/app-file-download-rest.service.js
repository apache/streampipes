appFileDownloadRestApi.$inject = ['$rootScope', '$http', 'apiConstants'];

export default function appFileDownloadRestApi($rootScope, $http, apiConstants) {

    var restApi = {};

    var getServerUrl = function() {
        return apiConstants.contextPath + "/api/apps/v1/elasticsearch";
        //return "http://localhost:8080"  + apiConstants.contextPath + apiConstants.api;
    }

    restApi.getAll = function () {
        return $http.get(getServerUrl() +'/files');
    }

    restApi.getFile = function (fileName) {
        return $http.get(getServerUrl() + '/file/' + fileName);
    }

    restApi.removeFile = function (fileName) {
        return $http({
            method: 'DELETE',
            url: getServerUrl() + "/file/" + fileName
        })
    }
    
    restApi.createFile = function (index, timestampFrom, timestampTo) {

        var postObject = {
            // 'index': index,
            // 'timestampFrom': timestampFrom,
            // 'timestampTo': timestampTo
        }
        postObject.index = index;
        postObject.timestampFrom = timestampFrom;
        postObject.timestampTo = timestampTo;


        return $http({
            method: 'POST',
            dataType: 'json',
            url: getServerUrl() + "/file",
            data: postObject,
            headers: {'Content-Type': 'application/json'}
        })
    }


    return restApi;
};