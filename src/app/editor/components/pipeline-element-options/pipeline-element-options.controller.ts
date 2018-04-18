import * as angular from 'angular';

export class PipelineElementOptionsController {

    ObjectProvider: any;
    PipelineElementRecommendationService: any;
    InitTooltips: any;
    JsplumbBridge: any;
    EditorDialogManager: any;
    JsplumbService: any;
    recommendationsAvailable: any;
    possibleElements: any;
    recommendedElements: any;
    recommendationsShown: any;
    pipelineElement: any;
    rawPipelineModel: any;
    allElements: any;
    deleteFunction: any;

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

        if (this.pipelineElement.type === 'stream') {
            this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
        }
    }

    removeElement() {
        // TODO: deleteFunction not implemented
        this.deleteFunction(this.pipelineElement.payload.DOM);
    }

    openCustomizeDialog() {
        this.EditorDialogManager.showCustomizeDialog($("#" + this.pipelineElement.payload.DOM), "", this.pipelineElement.payload);
    }

    openCustomizeStreamDialog() {
        this.EditorDialogManager.showCustomizeStreamDialog(this.pipelineElement.payload);
    }

    initRecs(elementId, currentPipelineElements) {
        var currentPipeline = this.ObjectProvider.makePipeline(angular.copy(currentPipelineElements));
        this.PipelineElementRecommendationService.getRecommendations(this.allElements, currentPipeline).then((result) => {
            if (result.success) {
                this.possibleElements = result.possibleElements;
                this.recommendedElements = result.recommendations;
                this.recommendationsAvailable = true;
                this.InitTooltips.initTooltips();
            }
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