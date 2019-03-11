

export class PipelineElementDocumentationController {

    RestApi: any;
    appId: any;
    documentationMarkdown: any;
    error: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
    }

    $onInit() {
        this.RestApi.getDocumentation(this.appId).then(msg => {
            this.error = false;
            this.documentationMarkdown = msg.data;
        }, error => {
            this.error = true;
        });
    }

}

PipelineElementDocumentationController.$inject = ['RestApi'];