export class PipelineElementOptionsController {

    constructor($rootScope, ObjectProvider, PipelineElementRecommendationService, InitTooltips, JsplumbBridge, EditorDialogManager, JsplumbService) {
        this.ObjectProvider = ObjectProvider;
        this.PipelineElementRecommendationService = PipelineElementRecommendationService;
        this.InitTooltips = InitTooltips;
        this.JsplumbBridge = JsplumbBridge;
        this.EditorDialogManager = EditorDialogManager;
        this.JsplumbService = JsplumbService;

        this.recommendationsAvailable = false;
        this.possibleElements = [];
        this.recommendedElements = [];
        this.recommendationsShown = false;

        $rootScope.$on("SepaElementConfigured", (event, item) => {
            if (item === this.pipelineElement.payload.DOM) {
                this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
            }
        });

        // $(document).click(() => {
        //     console.log("shown out");
        //     this.recommendationsShown = false;
        // });

        if (this.pipelineElement.type === 'stream') {
            this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
        }
    }

    removeElement() {
        this.deleteFunction(this.pipelineElement.payload.DOM);
    }

    openCustomizeDialog() {
        this.EditorDialogManager.showCustomizeDialog($("#" + this.pipelineElement.payload.DOM), "", this.pipelineElement.payload);
    }

    openCustomizeStreamDialog() {
        this.EditorDialogManager.showCustomizeStreamDialog(this.pipelineElement.payload);
    }

    initRecs(elementId, currentPipelineElements) {
        var currentPipeline = this.ObjectProvider.makePipeline(currentPipelineElements, elementId);
        this.PipelineElementRecommendationService.getRecommendations(this.allElements, currentPipeline).then((result) => {
            this.possibleElements = result.possibleElements;
            this.recommendedElements = result.recommendations;
            this.recommendationsAvailable = true;
            this.InitTooltips.initTooltips();
        });
    }

    showRecommendations(e) {
        this.recommendationsShown = !this.recommendationsShown;
        e.stopPropagation();
    }

    isRootElement() {
        return this.JsplumbBridge.getConnections({source: this.pipelineElement.payload.DOM}).length === 0;
    }

    isConfigured() {
        if (this.pipelineElement.type == 'stream') return true;
        else {
            return this.pipelineElement.payload.configured;
        }
    }

    isWildcardTopic() {
        return this.pipelineElement
            .payload
            .eventGrounding
            .transportProtocols[0]
            .properties
            .topicDefinition
            .type === "org.streampipes.model.grounding.WildcardTopicDefinition";

    }
}

PipelineElementOptionsController.$inject = ['$rootScope', 'ObjectProvider', 'PipelineElementRecommendationService', 'InitTooltips', 'JsplumbBridge', 'EditorDialogManager', 'JsplumbService'];