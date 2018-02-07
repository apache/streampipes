export class DeploymentController {

    constructor(deploymentService) {
        this.deploymentService = deploymentService;
        this.deployment = {};
        this.deployment.elementType = this.deploymentSettings.elementType;

        this.resultReturned = false;
        this.loading = false;
        this.jsonld = "";
        this.zipFile = "";
    }


    generateImplementation() {
        this.resultReturned = false;
        this.loading = true;
        this.deploymentService.generateImplementation(this.deployment, this.element)
            .success((data, status, headers, config) => {
                //$scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
                this.resultReturned = true;
                this.loading = false;
                this.zipFile = data;
            }).error((data, status, headers, config) => {
            console.log(data);
            this.loading = false;
        });
    };

    generateDescription() {
        this.loading = true;
        this.deploymentService.generateDescriptionJava(this.deployment, this.element)
            .success((data, status, headers, config) => {
                // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
                this.loading = false;
                this.resultReturned = true;
                this.java = data;
            }).error((data, status, headers, config) => {
            this.loading = false;
        });
        this.deploymentService.generateDescriptionJsonld(this.deployment, this.element)
            .success((data, status, headers, config) => {
                // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
                this.loading = false;
                this.resultReturned = true;
                this.jsonld = JSON.stringify(data, null, 2);
                console.log(this.jsonld);
            }).error((data, status, headers, config) => {
            this.loading = false;
        });
    }


    openSaveAsDialog(filename, content, mediaType) {
        var blob = new Blob([content], {type: mediaType});
        this.saveAs(blob, filename);
    }
}