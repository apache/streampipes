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

        this.selectedTab = 1;

        this.minimizedEditorStand = false;

        this.rawPipelineModel = [];
        this.activeType = "stream";

        angular.element($window).on('scroll', () => {
            JsplumbBridge.repaintEverything();
        });

        $rootScope.$on("elements.loaded", () => {
            this.makeDraggable();
        });

        if (this.AuthStatusService.email != undefined) {
            this.RestApi
                .getUserDetails()
                .success(user => {

                    if (!user.hideTutorial || user.hideTutorial == undefined) {
                        this.EditorDialogManager.showWelcomeDialog(user);
                    }
                })
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

        this.loadSources();
        this.loadSepas();
        this.loadActions();
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
            .then((sources) => {
                sources.data.forEach((source, i, sources) => {
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
            .success(sepas => {
                angular.forEach(sepas, sepa => {
                    sepa.type = 'sepa';
                });
                this.allElements["sepa"] = sepas;
            })
    };

    loadActions() {
        this.RestApi.getOwnActions()
            .success((actions) => {
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
                $('#outerAssemblyArea').css('border', '1px solid #878787');
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
