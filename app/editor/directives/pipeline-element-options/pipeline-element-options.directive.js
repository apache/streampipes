import HelpDialogController from './help-dialog.controller';
import PossibleElementsController from './possible-elements-dialog.controller';
import angular from 'npm/angular';

pipelineElementOptions.$inject = ['$rootScope', '$mdDialog', 'restApi', 'objectProvider', 'initTooltips'];
'use strict';

export default function pipelineElementOptions($rootScope, $mdDialog, restApi, objectProvider, initTooltips) {

    return {
        restrict: 'E',
        templateUrl: 'app/editor/directives/pipeline-element-options/pipeline-element-options.tmpl.html',
        scope: {
            pipelineElementId: "@",
            internalId: "@",
            allElements: "=",
            createFunction: "=",
            createPartialPipelineFunction: "=",
            showCustomizeDialogFunction: "=",
            deleteFunction: "="
        },
        controller: function ($scope) {
            $scope.recommendationsAvailable = false;
            $scope.possibleElements = [];
            
            $scope.openHelpDialog = function () {
                $mdDialog.show({
                    controller: HelpDialogController,
                    templateUrl: 'app/editor/directives/pipeline-element-options/help-dialog.tmpl.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: true,
                    scope: $scope,
                    preserveScope: true,
                    locals: {
                        pipelineElement: $scope.getPipelineElementContents($scope.pipelineElementId),
                    }
                })
            };

            $scope.openPossibleElementsDialog = function () {
                $mdDialog.show({
                    controller: PossibleElementsController,
                    templateUrl: 'app/editor/directives/pipeline-element-options/possible-elements-dialog.tmpl.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: true,
                    scope: $scope,
                    preserveScope: true
                })
            };

            $scope.removeElement = function() {
                $scope.deleteFunction($scope.getDomElement($scope.internalId));
            }

            $scope.openCustomizeDialog = function() {
                $scope.showCustomizeDialogFunction($scope.getDomElement($scope.internalId));
            }

            $scope.$on("SepaElementConfigured", function (event, item) {
                initRecs($rootScope.state.currentPipeline, item);
            });

            var initRecs = function (pipeline, $element) {
                restApi.recommendPipelineElement(pipeline)
                    .success(function (data) {
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
                            $element.hover(showRecButton, hideRecButton); //TODO alle Buttons anzeigen/verstecken
                            populateRecommendedList($element, data.recommendedElements);
                            if (data.recommendedElements.length > 0) {
                                $scope.recommendationsAvailable = true;
                            }
                        }
                        //$scope.domElement.data("possibleElements", data.possibleElements);
                        $scope.possibleElements = data.possibleElements;
                    })
                    .error(function (data) {
                        console.log(data);
                    });
            }

            var populateRecommendedList = function ($element, recs) {

                recs.sort(function (a, b) {
                    return (a.count > b.count) ? -1 : ((b.count > a.count) ? 1 : 0);
                });
                var maxRecs = recs.length > 7 ? 7 : recs.length;
                var el;
                for (var i = 0; i < maxRecs; i++) {

                    el = recs[i];
                    var element = $scope.getPipelineElementContents(el.elementId);
                    if (typeof element != "undefined") {
                        var recEl = new objectProvider.recElement(element);
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
                initTooltips();
            }

            $scope.showRecommendations = function (e) {
                var $recList = $("ul", $scope.getDomElement($scope.internalId));
                e.stopPropagation();
                $recList.circleMenu('open');
            }

            function showRecButton(e) {
                $("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).show();
            }

            function hideRecButton(e) {
                $("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).hide();
            }

            $scope.getPipelineElementContents = function (belongsTo) {
                var pipelineElement = undefined;
                angular.forEach($scope.allElements, function (category) {
                    angular.forEach(category, function (sepa) {
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

            $scope.getDomElement = function(internalId) {
                return $("span[id=" +internalId +"]");
            }

            initRecs($rootScope.state.currentPipeline, $scope.getDomElement($scope.internalId));
            $scope.pipelineElement = $scope.getPipelineElementContents($scope.pipelineElementId);
            console.log($scope.pipelineElement);

            $scope.isRootElement = function() {
                return jsPlumb.getConnections({source: $scope.getDomElement($scope.internalId)}).length == 0;
            }

            $scope.isConfigured = function() {
                if ($scope.pipelineElement.type == 'stream') return true;
                else return $($scope.getDomElement($scope.internalId)).data("JSON").configured;
            }

        },
        link: function postLink(scope, element) {

        }
    }
};