/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import * as angular from 'angular';
import {AuthStatusService} from "../services/auth-status.service";

export class EditorCtrl {

    $rootScope: any;
    RestApi: any;
    $stateParams: any;
    $window: any;
    JsplumbBridge: any;
    EditorDialogManager: any;
    AuthStatusService: AuthStatusService;
    currentElements: any;
    allElements: any;
    currentModifiedPipelineId: any;
    selectedTab: any;
    minimizedEditorStand: any;
    rawPipelineModel: any;
    activeType: any;
    tabs: any;
    currentlyFocusedElement: any;
    ShepherdService: any;
    isTutorialOpen: boolean = false;

    requiredStreamForTutorialAppId: any = "org.apache.streampipes.sources.simulator.flowrate1";
    requiredProcessorForTutorialAppId: any = "org.apache.streampipes.processors.filters.jvm.numericalfilter";
    requiredSinkForTutorialAppId: any = "org.apache.streampipes.sinks.internal.jvm.dashboard";
    missingElementsForTutorial: any = [];

    constructor($rootScope,
                RestApi,
                $stateParams,
                $window,
                JsplumbBridge,
                EditorDialogManager,
                AuthStatusService,
                ShepherdService) {

        this.$rootScope = $rootScope;
        this.RestApi = RestApi;
        this.$stateParams = $stateParams;
        this.$window = $window;
        this.JsplumbBridge = JsplumbBridge;
        this.EditorDialogManager = EditorDialogManager;
        this.AuthStatusService = AuthStatusService;
        this.ShepherdService = ShepherdService;

        this.currentElements = [];
        this.allElements = {};

        if ($stateParams.pipeline) {
            this.currentModifiedPipelineId = $stateParams.pipeline;
        }

        this.tabs = [
            {
                title: 'Data Sets',
                type: 'set',
            },
            {
                title: 'Data Streams',
                type: 'stream',
            },
            {
                title: 'Data Processors',
                type: 'sepa',
            },
            {
                title: 'Data Sinks',
                type: 'action',
            }
        ];


    }

    $onInit() {

        this.selectedTab = 1;

        this.minimizedEditorStand = false;

        this.rawPipelineModel = [];
        this.activeType = "stream";

        angular.element(this.$window).on('scroll', () => {
            this.JsplumbBridge.repaintEverything();
        });

        this.$rootScope.$on("elements.loaded", () => {
            this.makeDraggable();
        });

        this.loadSources();
        this.loadSepas();
        this.loadActions();
    }

    checkForTutorial() {
        if (this.AuthStatusService.email != undefined) {
            this.RestApi
                .getUserDetails()
                .then(msg => {
                    let user = msg.data;
                    if ((!user.hideTutorial || user.hideTutorial == undefined) && !this.isTutorialOpen) {
                        if (this.requiredPipelineElementsForTourPresent()) {
                            this.isTutorialOpen = true;
                            this.EditorDialogManager.showWelcomeDialog(user);
                        }
                    }
                });
        }
    }

    startCreatePipelineTour() {
        if (this.requiredPipelineElementsForTourPresent()) {
            this.ShepherdService.startCreatePipelineTour();
        } else {
            this.missingElementsForTutorial = [];
            if (!this.requiredStreamForTourPresent()) {
                this.missingElementsForTutorial.push({"name" : "Flow Rate 1", "appId" : this.requiredStreamForTutorialAppId });
            }
            if (!this.requiredProcessorForTourPresent()) {
                this.missingElementsForTutorial.push({"name" : "Field Hasher", "appId" : this.requiredProcessorForTutorialAppId});
            }
            if (!this.requiredSinkForTourPresent()) {
                this.missingElementsForTutorial.push({"name" : "Dashboard Sink", "appId" : this.requiredSinkForTutorialAppId});
            }

            this.EditorDialogManager.showMissingElementsForTutorialDialog(this.missingElementsForTutorial);
        }
    }

    requiredPipelineElementsForTourPresent() {
        return this.requiredStreamForTourPresent() &&
            this.requiredProcessorForTourPresent() &&
            this.requiredSinkForTourPresent();
    }

    requiredStreamForTourPresent() {
        return this.requiredPeForTourPresent(this.allElements["stream"],
            this.requiredStreamForTutorialAppId);
    }

    requiredProcessorForTourPresent() {
        return this.requiredPeForTourPresent(this.allElements["sepa"],
            this.requiredProcessorForTutorialAppId);
    }

    requiredSinkForTourPresent() {
        return this.requiredPeForTourPresent(this.allElements["action"],
            this.requiredSinkForTutorialAppId);
    }

    requiredPeForTourPresent(list, appId) {
        return list && list.some(el => {
            return el.appId === appId;
        });
    }

    isActiveTab(elementType) {
        return elementType === this.activeType;
    }

    toggleEditorStand() {
        this.minimizedEditorStand = !this.minimizedEditorStand;
    }

    currentFocus(element, active) {
        if (active) this.currentlyFocusedElement = element;
        else this.currentlyFocusedElement = undefined;
    }

    currentFocusActive(element) {
        return this.currentlyFocusedElement == element;
    }

    loadCurrentElements(type) {
        this.currentElements = this.allElements[type];
        this.activeType = type;
        this.ShepherdService.trigger("select-" +type);
    }

    loadSources() {
        var tempStreams = [];
        var tempSets = [];
        this.RestApi.getOwnSources()
            .then((msg) => {
                let sources = msg.data;
                sources.forEach((source, i, sources) => {
                    source.spDataStreams.forEach(stream => {
                        if (stream.sourceType == 'org.apache.streampipes.model.SpDataSet') {
                            stream.type = "set";
                            tempSets = tempSets.concat(stream);
                        } else {
                            stream.type = "stream";
                            tempStreams = tempStreams.concat(stream);
                        }
                    });
                    this.allElements["stream"] = tempStreams;
                    this.allElements["set"] = tempSets;
                    this.currentElements = this.allElements["stream"];
                    this.checkForTutorial();
                });
            });
    };

    loadSepas() {
        this.RestApi.getOwnSepas()
            .then(msg => {
                let sepas = msg.data;
                angular.forEach(sepas, sepa => {
                    sepa.type = 'sepa';
                    sepa.correspondingUser = this.AuthStatusService.email;
                });
                this.allElements["sepa"] = sepas;
                this.checkForTutorial();
            });
    };

    loadActions() {
        this.RestApi.getOwnActions()
            .then((msg) => {
                let actions = msg.data;
                angular.forEach(actions, action => {
                    action.type = 'action';
                    action.correspondingUser = this.AuthStatusService.email;
                });
                this.allElements["action"] = actions;
                this.checkForTutorial();
            });
    };

    makeDraggable() {
        (<any>$('.draggable-icon')).draggable({
            revert: 'invalid',
            helper: 'clone',
            stack: '.draggable-icon',
            start: function (el, ui) {
                ui.helper.appendTo('#content');
                $('#outerAssemblyArea').css('border', '3px dashed #39b54a');
            },
            stop: function (el, ui) {
                $('#outerAssemblyArea').css('border', '3px solid rgb(156, 156, 156)');
            }
        });
    };

}

EditorCtrl.$inject = ['$rootScope',
    'RestApi',
    '$stateParams',
    '$window',
    'JsplumbBridge',
    'EditorDialogManager',
    'AuthStatusService',
    'ShepherdService'];
