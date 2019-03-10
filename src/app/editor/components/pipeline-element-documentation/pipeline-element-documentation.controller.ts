

export class PipelineElementDocumentationController {

    RestApi: any;
    appId: any;
    documentationMarkdown: any;

    constructor(RestApi) {
        this.RestApi = RestApi;
    }

    $onInit() {
        this.RestApi.getDocumentation(this.appId).then(msg => {
            this.documentationMarkdown = msg.data;
        });
    }

}

PipelineElementDocumentationController.$inject = ['RestApi'];