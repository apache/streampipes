export class PipelineAssemblyController {

    constructor(JsplumbBridge, PipelinePositioningService, EditorDialogManager, PipelineValidationService, ObjectProvider, RestApi, JsplumbService, $timeout) {
        this.JsplumbBridge = JsplumbBridge;
        this.PipelinePositioningService = PipelinePositioningService;
        this.EditorDialogManager = EditorDialogManager;
        this.PipelineValidationService = PipelineValidationService;
        this.ObjectProvider = ObjectProvider;
        this.RestApi = RestApi;
        this.JsplumbService = JsplumbService;
        this.$timeout = $timeout;

        this.selectMode = true;
        this.currentZoomLevel = 1;

        $("#assembly").panzoom({
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });

        $("#assembly").on('panzoomzoom', (e, panzoom, scale) => {
            this.currentZoomLevel = scale;
            JsplumbBridge.setZoom(scale);
            JsplumbBridge.repaintEverything();
        });

        if (this.currentModifiedPipelineId) {
            this.displayPipelineById();
        }

    }

    autoLayout() {
        this.PipelinePositioningService.layoutGraph("#assembly", "span[id^='jsplumb']", 110, false);
        this.JsplumbBridge.repaintEverything();
    }

    toggleSelectMode() {
        if (this.selectMode) {
            $("#assembly").panzoom("option", "disablePan", false);
            $("#assembly").selectable("disable");
            this.selectMode = false;
        }
        else {
            $("#assembly").panzoom("option", "disablePan", true);
            $("#assembly").selectable("enable");
            this.selectMode = true;
        }
    }

    zoomOut() {
        this.doZoom(true);
    }

    zoomIn() {
        this.doZoom(false);
    }

    doZoom(zoomOut) {
        $("#assembly").panzoom("zoom", zoomOut);
    }

    showClearAssemblyConfirmDialog(ev) {
        this.EditorDialogManager.showClearAssemblyDialog(ev).then(() => {
            this.clearAssembly();
        }, function () {
        });
    };

    /**
     * clears the Assembly of all elements
     */
    clearAssembly() {
        //$('#assembly').children().not('#clear, #submit').remove();
        this.JsplumbBridge.deleteEveryEndpoint();
        this.rawPipelineModel = [];
        $("#assembly").panzoom("reset", {
            disablePan: true,
            increment: 0.25,
            minScale: 0.5,
            maxScale: 1.5,
            contain: 'invert'
        });
        this.currentZoomLevel = 1;
        this.JsplumbBridge.setZoom(this.currentZoomLevel);
        this.JsplumbBridge.repaintEverything();
    };

    /**
     * Sends the pipeline to the server
     */
    submit() {
        var pipeline = this.ObjectProvider.makeFinalPipeline(this.rawPipelineModel);

        pipeline.name = this.currentPipelineName;
        pipeline.description = this.currentPipelineDescription;

        this.openPipelineNameModal(pipeline);
    }


    openPipelineNameModal(pipeline) {
        this.EditorDialogManager.showSavePipelineDialog(pipeline);
    }

    displayPipelineById() {
        this.RestApi.getPipelineById(this.currentModifiedPipelineId)
            .success((pipeline) => {
                this.currentPipelineName = pipeline.name;
                this.currentPipelineDescription = pipeline.description;
                this.rawPipelineModel = this.JsplumbService.makeRawPipeline(pipeline, false);
                this.$timeout(() => {
                    this.PipelinePositioningService.displayPipeline(this.rawPipelineModel, "#assembly", false);
                });
            })
    };

}

PipelineAssemblyController.$inject = ['JsplumbBridge', 'PipelinePositioningService', 'EditorDialogManager', 'PipelineValidationService', 'ObjectProvider', 'RestApi', 'JsplumbService', '$timeout'];