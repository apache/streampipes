export class PipelineAssemblyController {

    constructor($rootScope, JsplumbBridge, PipelinePositioningService, EditorDialogManager) {
        this.$rootScope = $rootScope;
        this.JsplumbBridge = JsplumbBridge;
        this.pipelinePositioningService = PipelinePositioningService;
        this.EditorDialogManager = EditorDialogManager;

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
        this.pipelinePositioningService.layoutGraph("#assembly", "span.connectable-editor", 110, false);
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
        $('#assembly').children().not('#clear, #submit').remove();
        this.JsplumbBridge.deleteEveryEndpoint();
        this.$rootScope.state.adjustingPipelineState = false;
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
        var error = false;
        var pipelineNew = this.objectProvider.makePipeline(this.pipelineModel);
        var streamPresent = false;
        var sepaPresent = false;
        var actionPresent = false;


        // $('#assembly').find('.connectable, .connectable-block').each((i, element) => {
        //     var $element = $(element);
        //
        //     if (!this.pipelineEditorService.isConnected(element)) {
        //         error = true;
        //         this.showToast("error", "All elements must be connected", "Submit Error");
        //     }
        //
        //     if ($element.hasClass('sepa')) {
        //         sepaPresent = true;
        //         if ($element.data("options")) {
        //             pipelineNew.addElement(element);
        //
        //         } else if ($element.data("JSON").staticProperties != null) {
        //             this.showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
        //             error = true;
        //         }
        //     } else if ($element.hasClass('stream')) {
        //         streamPresent = true;
        //         pipelineNew.addElement(element);
        //
        //     } else if ($element.hasClass('action')) {
        //         actionPresent = true;
        //         if ($element.data("JSON").staticProperties == null || $element.data("options")) {
        //             pipelineNew.addElement(element);
        //         } else {
        //             this.showToast("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
        //             ;
        //             error = true;
        //         }
        //     }
        // });
        // if (!streamPresent) {
        //     this.showToast("error", "No stream element present in pipeline", "Submit Error");
        //     error = true;
        // }
        //
        // if (!actionPresent) {
        //     this.showToast("error", "No action element present in pipeline", "Submit Error");
        //     error = true;
        //}

        var error = false;
        if (!error) {
            this.$rootScope.state.currentPipeline = pipelineNew;
            if (this.$rootScope.state.adjustingPipelineState) {
                this.$rootScope.state.currentPipeline.name = this.currentPipelineName;
                this.$rootScope.state.currentPipeline.description = this.currentPipelineDescription;
            }

            this.openPipelineNameModal(pipelineNew);
        }
    }

    openPipelineNameModal(pipelineNew) {
        if (this.$rootScope.state.adjustingPipelineState) {
            this.modifyPipelineMode = true;
        }
        this.EditorDialogManager.showSavePipelineDialog(pipelineNew);
    }
}

PipelineAssemblyController.$inject = ['$rootScope', 'JsplumbBridge', 'PipelinePositioningService', 'EditorDialogManager'];