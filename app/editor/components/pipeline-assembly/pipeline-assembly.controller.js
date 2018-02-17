export class PipelineAssemblyController {

    constructor($rootScope, JsplumbBridge, PipelinePositioningService, EditorDialogManager, PipelineValidationService, ObjectProvider) {
        this.$rootScope = $rootScope;
        this.JsplumbBridge = JsplumbBridge;
        this.pipelinePositioningService = PipelinePositioningService;
        this.EditorDialogManager = EditorDialogManager;
        this.PipelineValidationService = PipelineValidationService;
        this.ObjectProvider = ObjectProvider;

        this.selectMode = true;
        this.currentZoomLevel = 1;

        // T1
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
    }

    autoLayout() {
        this.pipelinePositioningService.layoutGraph("#assembly", "span[id^='jsplumb']", 110, false);
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
        if (this.$rootScope.state.adjustingPipelineState) {
            this.modifyPipelineMode = true;
        }
        this.EditorDialogManager.showSavePipelineDialog(pipeline);
    }

}

PipelineAssemblyController.$inject = ['$rootScope', 'JsplumbBridge', 'PipelinePositioningService', 'EditorDialogManager', 'PipelineValidationService', 'ObjectProvider'];