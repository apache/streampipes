import * as angular from 'angular';
import {JsplumbBridge} from "../../../services/jsplumb-bridge.service";
import {JsplumbService} from "../../../services/jsplumb.service";

export class PipelineElementOptionsController {

    ObjectProvider: any;
    PipelineElementRecommendationService: any;
    InitTooltips: any;
    JsplumbBridge: JsplumbBridge;
    EditorDialogManager: any;
    JsplumbService: JsplumbService;
    recommendationsAvailable: any;
    possibleElements: any;
    recommendedElements: any;
    recommendationsShown: any;
    pipelineElement: any;
    rawPipelineModel: any;
    allElements: any;
    deleteFunction: any;
    TransitionService: any;
    $rootScope: any;

    constructor($rootScope, ObjectProvider, PipelineElementRecommendationService, InitTooltips, JsplumbBridge, EditorDialogManager, JsplumbService, TransitionService) {
        this.$rootScope = $rootScope;
        this.ObjectProvider = ObjectProvider;
        this.PipelineElementRecommendationService = PipelineElementRecommendationService;
        this.InitTooltips = InitTooltips;
        this.JsplumbBridge = JsplumbBridge;
        this.EditorDialogManager = EditorDialogManager;
        this.JsplumbService = JsplumbService;
        this.TransitionService = TransitionService;

        this.recommendationsAvailable = false;
        this.possibleElements = [];
        this.recommendedElements = [];
        this.recommendationsShown = false;
    }

    $onInit() {
        this.$rootScope.$on("SepaElementConfigured", (event, item) => {
            if (item === this.pipelineElement.payload.DOM) {
                this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
            }
        });

        if (this.pipelineElement.type === 'stream') {
            this.initRecs(this.pipelineElement.payload.DOM, this.rawPipelineModel);
        }
    }

    removeElement(pipelineElement) {
        this.deleteFunction(pipelineElement);
    }

    openCustomizeDialog() {
        this.EditorDialogManager.showCustomizeDialog($("#" + this.pipelineElement.payload.DOM), "", this.pipelineElement.payload)
            .then(() => {
                this.JsplumbService.activateEndpoint(this.pipelineElement.payload.DOM, !this.pipelineElement.payload.uncompleted);
            }, () => {
                this.JsplumbService.activateEndpoint(this.pipelineElement.payload.DOM, !this.pipelineElement.payload.uncompleted);
            });
    }

    openHelpDialog() {
        this.EditorDialogManager.openHelpDialog(this.pipelineElement.payload);
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

PipelineElementOptionsController.$inject = ['$rootScope', 'ObjectProvider', 'PipelineElementRecommendationService', 'InitTooltips', 'JsplumbBridge', 'EditorDialogManager', 'JsplumbService', 'TransitionService'];