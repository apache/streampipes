export class PipelinePreviewController {

    constructor($scope, pipelinePositioningService, jsplumbService, $timeout) {
        this.$scope = $scope;

        $timeout(() => {
            var js2 = jsPlumb.getInstance({});
            js2.setContainer(this.jspcanvas);
            jsplumbService.prepareJsplumb(js2);
            var elid = "#" + this.jspcanvas;
            pipelinePositioningService.displayPipeline($scope, js2, this.pipeline, elid, true);
            var existingEndpointIds = [];
            js2.selectEndpoints().each(endpoint => {
                if (existingEndpointIds.indexOf(endpoint.element.id) == -1) {
                    $(endpoint.element).click(ev => {
                        this.updateSelected({selected: $(endpoint.element).data("JSON")});
                    });
                    existingEndpointIds.push(endpoint.element.id);
                }
            });

        });
    }
}

PipelinePreviewController.$inject = ['$scope', 'pipelinePositioningService', 'jsplumbService', '$timeout'];