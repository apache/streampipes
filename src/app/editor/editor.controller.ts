import * as angular from 'angular';

export class EditorCtrl {

    $rootScope: any;
    RestApi: any;
    $stateParams: any;
    $window: any;
    JsplumbBridge: any;
    EditorDialogManager: any;
    AuthStatusService: any;
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

    requiredStreamForTutorialAppId: any = "org.streampipes.pe.random.number.json";
    requiredProcessorForTutorialAppId: any = "org.streampipes.processors.transformation.flink.fieldhasher";
    requiredSinkForTutorialAppId: any = "org.streampipes.sinks.internal.jvm.dashboard";
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

        if (this.AuthStatusService.email != undefined) {
            this.RestApi
                .getUserDetails()
                .then(msg => {
                    let user = msg.data;
                    if (!user.hideTutorial || user.hideTutorial == undefined) {
                        if (this.requiredPipelineElementsForTourPresent()) {
                            this.EditorDialogManager.showWelcomeDialog(user);
                        }
                    }
                });
        }

        this.loadSources();
        this.loadSepas();
        this.loadActions();
    }

    startCreatePipelineTour() {
        if (this.requiredPipelineElementsForTourPresent()) {
            this.ShepherdService.startCreatePipelineTour();
        } else {
            this.missingElementsForTutorial = [];
            if (!this.requiredStreamForTourPresent()) {
                this.missingElementsForTutorial.push({"name" : "Random Number Stream", "appId" : this.requiredStreamForTutorialAppId });
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
                        if (stream.sourceType == 'org.streampipes.model.SpDataSet') {
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
                });
            });
    };

    loadSepas() {
        this.RestApi.getOwnSepas()
            .then(msg => {
                let sepas = msg.data;
                angular.forEach(sepas, sepa => {
                    sepa.type = 'sepa';
                });
                this.allElements["sepa"] = sepas;
            });
    };

    loadActions() {
        this.RestApi.getOwnActions()
            .then((msg) => {
                let actions = msg.data;
                angular.forEach(actions, action => {
                    action.type = 'action';
                });
                this.allElements["action"] = actions;
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
