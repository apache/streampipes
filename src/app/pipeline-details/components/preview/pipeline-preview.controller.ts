export class PipelinePreviewController {

    PipelinePositioningService: any;
    JsplumbService: any;
    $timeout: any;
    ObjectProvider: any;
    JsplumbBridge: any;
    jspcanvas: any;
    rawPipelineModel: any;
    pipeline: any;
    selectedElement: any;

    constructor(PipelinePositioningService, JsplumbService, $timeout, ObjectProvider, JsplumbBridge) {
        this.PipelinePositioningService = PipelinePositioningService;
        this.JsplumbService = JsplumbService;
        this.$timeout = $timeout;
        this.ObjectProvider = ObjectProvider;
        this.JsplumbBridge = JsplumbBridge;
    }

    $onInit() {
        this.$timeout(() => {
            var elid = "#" + this.jspcanvas;
            this.rawPipelineModel = this.JsplumbService.makeRawPipeline(this.pipeline, true);
            this.$timeout(() => {
                this.PipelinePositioningService.displayPipeline(this.rawPipelineModel, elid, true);
                var existingEndpointIds = [];
                this.$timeout(() => {
                    this.JsplumbBridge.selectEndpoints().each(endpoint => {
                        if (existingEndpointIds.indexOf(endpoint.element.id) === -1) {
                            $(endpoint.element).click(() => {
                                var payload = this.ObjectProvider.findElement(endpoint.element.id, this.rawPipelineModel).payload;
                                this.selectedElement = payload;
                            });
                            existingEndpointIds.push(endpoint.element.id);
                        }
                    });
                });
            });
        });
    }
}

PipelinePreviewController.$inject = ['PipelinePositioningService', 'JsplumbService', '$timeout', 'ObjectProvider', 'JsplumbBridge'];