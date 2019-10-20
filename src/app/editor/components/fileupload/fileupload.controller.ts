import {RestApi} from "../../../services/rest-api.service";

export class FileUploadController {

    file: any;
    restApi: RestApi;
    selectedElement: any;
    staticProperty: any;

    fileMetadataDescriptions: any = [];
    showFiles: boolean;

    $scope: any;
    $rootScope: any;

    constructor(restApi: RestApi, $scope, $rootScope) {
        this.restApi = restApi;
        this.$scope = $scope;
        this.$rootScope = $rootScope;
    }

    $onInit() {
        this.showFiles = false;
        this.getFileMetadata();


        this.$scope.$watch(() => this.staticProperty.properties.locationPath, (newValue, oldValue) => {
            if (this.staticProperty.properties.locationPath !== "" && this.staticProperty.properties.locationPath != undefined) {
                if (newValue !== oldValue) {
                    this.$rootScope.$emit(this.staticProperty.properties.internalName);
                }
            }
        });
    }

    uploadFile() {
        let fileInput: any = document.getElementById('file');
        let file = fileInput.files[0];
        let filename = fileInput.files[0].name;

        const data: FormData = new FormData();
        data.append('file_upload', file, filename);

        this.restApi.uploadFile(data).then(result => {
            this.getFileMetadata();
        });
    }

    getFileMetadata() {
        this.restApi.getFileMetadata().then(fm => {
            this.fileMetadataDescriptions = fm.data;
            this.showFiles = true;
        })
    }
}

FileUploadController.$inject = ['RestApi', '$scope', '$rootScope'];