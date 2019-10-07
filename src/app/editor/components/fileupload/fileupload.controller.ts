import {HttpParams, HttpRequest} from "@angular/common/http";
import {RestApi} from "../../../services/rest-api.service";
import {OneOfRemoteController} from "../oneof-remote/oneof-remote.controller";

export class FileUploadController {

    file: any;
    restApi: RestApi;

    constructor(restApi: RestApi) {
        this.restApi = restApi;
    }

    $onInit() {

    }

    uploadFile() {
        let fileInput: any = document.getElementById('file');
        let file = fileInput.files[0];
        let filename = fileInput.files[0].name;

        const data: FormData = new FormData();
        data.append('file_upload', file, filename);

        this.restApi.uploadFile(data).then(result => {
            console.log(result);
        });
    }
}

FileUploadController.$inject = ['RestApi'];