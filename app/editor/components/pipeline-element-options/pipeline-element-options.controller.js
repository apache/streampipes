export class PipelineElementOptionsController {

    constructor($rootScope, ObjectProvider, RestApi, InitTooltips, JsplumbBridge, EditorDialogManager, $timeout, JsplumbService) {
        this.ObjectProvider = ObjectProvider;
        this.RestApi = RestApi;
        this.InitTooltips = InitTooltips;
        this.JsplumbBridge = JsplumbBridge;
        this.EditorDialogManager = EditorDialogManager;
        this.$timeout = $timeout;
        this.JsplumbService = JsplumbService;

        this.recommendationsAvailable = false;
        this.possibleElements = [];
        this.recommendedElements = [];
        this.recommendationsShown = true;

        $rootScope.$on("SepaElementConfigured", (event, item) => {
            if (item === this.pipelineElement.payload.DOM) {
                this.initRecs(this.pipelineElement.payload.DOM, this.pipelineModel);
            }
        });

        // $(document).click(() => {
        //     console.log("shown out");
        //     this.recommendationsShown = false;
        // });

        this.initRecs(this.pipelineElement.payload.DOM, this.pipelineModel);
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
        var currentPipeline = this.ObjectProvider.makePipeline($("#" + elementId), currentPipelineElements, elementId);
        var $element = $("#" + elementId);
        this.RestApi.recommendPipelineElement(currentPipeline)
            .success(data => {
                if (data.success) {
                    this.populateRecommendedList($element, data.recommendedElements);
                }
                this.collectPossibleElements(data.possibleElements);
            });
    }

    collectPossibleElements(possibleElements) {
        angular.forEach(possibleElements, pe => {
            this.possibleElements.push(this.getPipelineElementContents(pe.elementId));
        })
    }

    populateRecommendedList($element, recs) {
        var elementRecommendations = [];
        recs.sort(function (a, b) {
            return (a.count > b.count) ? -1 : ((b.count > a.count) ? 1 : 0);
        });
        var maxRecs = recs.length > 7 ? 7 : recs.length;
        var el;
        for (var i = 0; i < maxRecs; i++) {
            el = recs[i];
            var element = this.getPipelineElementContents(el.elementId);
            element.weight = el.weight;
            elementRecommendations.push(element);
        }
        this.recommendationsAvailable = true;
        this.recommendedElements = elementRecommendations;

        this.InitTooltips.initTooltips();
    }


    showRecommendations(e) {
        this.recommendationsShown = !this.recommendationsShown;
        //var $recList = $("ul", $("#" + this.pipelineElement.payload.DOM));
        e.stopPropagation();
        //$recList.circleMenu('open');
    }

    setStyle() {
        this.style = "background:green;";
    }

    getPipelineElementContents(belongsTo) {
        var pipelineElement = undefined;
        angular.forEach(this.allElements, category => {
            angular.forEach(category, sepa => {
                if (sepa.type != 'stream') {
                    if (sepa.belongsTo == belongsTo) {
                        pipelineElement = sepa;
                    }
                } else {
                    if (sepa.elementId == belongsTo) {
                        pipelineElement = sepa;
                    }
                }
            });
        });
        return pipelineElement;
    }

    isRootElement() {
        return this.JsplumbBridge.getConnections({source: this.pipelineElement.payload.DOM}).length == 0;
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

PipelineElementOptionsController.$inject = ['$rootScope', 'ObjectProvider', 'RestApi', 'InitTooltips', 'JsplumbBridge', 'EditorDialogManager', '$timeout', 'JsplumbService'];