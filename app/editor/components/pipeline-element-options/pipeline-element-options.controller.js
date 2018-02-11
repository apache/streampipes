import {HelpDialogController} from '../../dialog/help/help-dialog.controller';
import {PossibleElementsController} from '../../dialog/possible-elements/possible-elements-dialog.controller';

export class PipelineElementOptionsController {

    constructor($scope, $rootScope, $mdDialog, RestApi, ObjectProvider, InitTooltips, JsplumbBridge) {
        this.$rootScope = $rootScope;
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.objectProvider = ObjectProvider;
        this.InitTooltips = InitTooltips;
        this.JsplumbBridge = JsplumbBridge;

        this.recommendationsAvailable = false;
        this.possibleElements = [];

        $scope.$on("SepaElementConfigured", function (event, item) {
            this.initRecs($rootScope.state.currentPipeline, item);
        });


        this.initRecs($rootScope.state.currentPipeline, this.getDomElement(this.internalId));
        this.pipelineElement = this.getPipelineElementContents(this.pipelineElementId);
        console.log(this.pipelineElement);
    }


    openHelpDialog() {
        this.$mdDialog.show({
            controller: HelpDialogController,
            controllerAs: 'ctrl',
            templateUrl: 'app/editor/dialog/help/help-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                pipelineElement: this.getPipelineElementContents(this.pipelineElementId),
            },
            bindToController: true
        })
    };

    openPossibleElementsDialog() {
        this.$mdDialog.show({
            controller: PossibleElementsController,
            controllerAs: 'ctrl',
            templateUrl: 'app/editor/dialog/possible-elements/possible-elements-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            bindToController: true
        })
    };

    removeElement() {
        this.deleteFunction(this.getDomElement(this.internalId));
    }

    openCustomizeDialog() {
        this.showCustomizeDialogFunction(this.getDomElement(this.internalId));
    }

    openCustomizeStreamDialog() {
        this.showCustomizeStreamDialogFunction(this.getDomElement(this.internalId));
    }

    initRecs(pipeline, $element) {
        this.RestApi.recommendPipelineElement(pipeline)
            .success(data => {
                if (data.success) {
                    $(".recommended-list", $element).remove();
                    $element.append($("<span><ul>").addClass("recommended-list"));
                    $("ul", $element)
                        .circleMenu({
                            direction: "right-half",
                            item_diameter: 50,
                            circle_radius: 150,
                            trigger: 'none'
                        });
                    $element.hover(this.showRecButton, this.hideRecButton); //TODO alle Buttons anzeigen/verstecken
                    this.populateRecommendedList($element, data.recommendedElements);
                    if (data.recommendedElements.length > 0) {
                        this.recommendationsAvailable = true;
                    }
                }
                //$scope.domElement.data("possibleElements", data.possibleElements);
                this.possibleElements = data.possibleElements;
            })
            .error(data => {
                console.log(data);
            });
    }

    populateRecommendedList($element, recs) {

        recs.sort(function (a, b) {
            return (a.count > b.count) ? -1 : ((b.count > a.count) ? 1 : 0);
        });
        var maxRecs = recs.length > 7 ? 7 : recs.length;
        var el;
        for (var i = 0; i < maxRecs; i++) {

            el = recs[i];
            var element = this.getPipelineElementContents(el.elementId);
            if (typeof element != "undefined") {
                var recEl = new this.objectProvider.recElement(element);
                $("<li>").addClass("recommended-item tt").append(recEl.getjQueryElement()).attr({
                    "data-toggle": "tooltip",
                    "data-placement": "top",
                    "data-delay": '{"show": 100, "hide": 100}',
                    "weight": el.weight,
                    "type": element.type,
                    title: recEl.name
                }).appendTo($('ul', $element));
            } else {
                console.log(i);
            }
        }
        $('ul', $element).circleMenu('init');
        this.InitTooltips.initTooltips();
    }

    showRecommendations(e) {
        var $recList = $("ul", this.getDomElement(this.internalId));
        e.stopPropagation();
        $recList.circleMenu('open');
    }

    showRecButton(e) {
        $("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).show();
    }

    hideRecButton(e) {
        $("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).hide();
    }

    getPipelineElementContents(belongsTo) {
        console.log("content");

        var pipelineElement = undefined;
        angular.forEach(this.allElements, category => {
            angular.forEach(category, sepa => {
                if (sepa.type != 'stream') {
                    if (sepa.belongsTo == belongsTo) {
                        this.pipelineElement = sepa;
                    }
                } else {
                    if (sepa.elementId == belongsTo) {
                        this.pipelineElement = sepa;
                    }
                }
            });
        });
        console.log(this.allElements);
        return pipelineElement;
    }

    getDomElement(internalId) {
        console.log(internalId);
        return $("span[id=" + internalId + "]");
    }

    isRootElement() {
        return this.JsplumbBridge.getConnections({source: this.getDomElement(this.internalId)}).length == 0;
    }

    isConfigured() {
        if (this.pipelineElement.type == 'stream') return true;
        else {
            return $(this.getDomElement(this.internalId)).data("JSON").configured;
        }
    }

    isWildcardTopic(pipelineElement) {
        return pipelineElement
                .eventGrounding
                .transportProtocols[0]
                .properties
                .topicDefinition
                .type == "org.streampipes.model.grounding.WildcardTopicDefinition";

    }
}

PipelineElementOptionsController.$inject = ['$scope', '$rootScope', '$mdDialog', 'RestApi', 'ObjectProvider', 'InitTooltips', 'JsplumbBridge'];