export class PipelineAssemblyController {

    $timeout: any;
    $state: any;
    JsplumbService: any;
    PipelineEditorService: any;
    JsplumbBridge: any;
    ObjectProvider: any;
    DialogBuilder: any;
    EditorDialogManager: any;
    currentMouseOverElement: any;
    currentZoomLevel: any;
    preview: any;
    rawPipelineModel: any;
    PipelinePositioningService: any;
    PipelineValidationService: any;
    RestApi: any;
    selectMode: any;
    currentPipelineName: any;
    currentPipelineDescription: any;
    currentModifiedPipelineId: any;
    TransitionService: any;
    ShepherdService: any;

    constructor(JsplumbBridge,
                PipelinePositioningService,
                EditorDialogManager,
                PipelineValidationService,
                ObjectProvider,
                RestApi,
                JsplumbService,
                $timeout,
                $state,
                TransitionService,
                ShepherdService) {
        this.JsplumbBridge = JsplumbBridge;
        this.PipelinePositioningService = PipelinePositioningService;
        this.EditorDialogManager = EditorDialogManager;
        this.PipelineValidationService = PipelineValidationService;
        this.ObjectProvider = ObjectProvider;
        this.RestApi = RestApi;
        this.JsplumbService = JsplumbService;
        this.$timeout = $timeout;
        this.$state = $state;
        this.TransitionService = TransitionService;
        this.ShepherdService = ShepherdService;

        this.selectMode = true;
        this.currentZoomLevel = 1;

        ($("#assembly") as any).panzoom({
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

    $onInit() {
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
            ($("#assembly") as any).panzoom("option", "disablePan", false);
            ($("#assembly") as any).selectable("disable");
            this.selectMode = false;
        }
        else {
            ($("#assembly") as any).panzoom("option", "disablePan", true);
            ($("#assembly") as any).selectable("enable");
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
        ($("#assembly") as any).panzoom("zoom", zoomOut);
    }

    showClearAssemblyConfirmDialog(ev) {
        this.EditorDialogManager.showClearAssemblyDialog(ev).then(() => {
            if (this.currentModifiedPipelineId) {
                this.currentModifiedPipelineId = undefined;
            }
            this.clearAssembly();
            this.TransitionService.makePipelineAssemblyEmpty(true);

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
        ($("#assembly") as any).panzoom("reset", {
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
        if (this.currentModifiedPipelineId) {
            pipeline._id = this.currentModifiedPipelineId;
        }

        this.openPipelineNameModal(pipeline, (!!this.currentModifiedPipelineId));
        if (this.ShepherdService.isTourActive()) {
            this.ShepherdService.trigger("save-pipeline-dialog");
        }
    }


    openPipelineNameModal(pipeline, modificationMode) {
        this.EditorDialogManager.showSavePipelineDialog(pipeline, modificationMode);
    }

    displayPipelineById() {
        this.RestApi.getPipelineById(this.currentModifiedPipelineId)
            .then((msg) => {
                let pipeline = msg.data;
                this.currentPipelineName = pipeline.name;
                this.currentPipelineDescription = pipeline.description;
                this.rawPipelineModel = this.JsplumbService.makeRawPipeline(pipeline, false);
                this.$timeout(() => {
                    this.PipelinePositioningService.displayPipeline(this.rawPipelineModel, "#assembly", false);
                    this.TransitionService.makePipelineAssemblyEmpty(false);
                });
            });
    };

}

PipelineAssemblyController.$inject = ['JsplumbBridge',
    'PipelinePositioningService',
    'EditorDialogManager',
    'PipelineValidationService',
    'ObjectProvider',
    'RestApi',
    'JsplumbService',
    '$timeout',
    '$state',
    'TransitionService',
    'ShepherdService'];