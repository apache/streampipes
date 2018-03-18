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

    constructor($rootScope,
                RestApi,
                $stateParams,
                $window,
                JsplumbBridge,
                EditorDialogManager,
                AuthStatusService) {

        this.$rootScope = $rootScope;
        this.RestApi = RestApi;
        this.$stateParams = $stateParams;
        this.$window = $window;
        this.JsplumbBridge = JsplumbBridge;
        this.EditorDialogManager = EditorDialogManager;
        this.AuthStatusService = AuthStatusService;

        this.currentElements = [];
        this.allElements = {};

        if ($stateParams.pipeline) {
            this.currentModifiedPipelineId = $stateParams.pipeline;
        }

        this.selectedTab = 0;

        this.minimizedEditorStand = false;

        this.rawPipelineModel = [];
        this.activeType = "stream";

        if (this.AuthStatusService.email != undefined) {
            this.RestApi
                .getUserDetails()
                .success(user => {
                    if (!user.hideTutorial || user.hideTutorial == undefined) {
                        this.EditorDialogManager.showTutorialDialog().then(() => {
                            user.hideTutorial = true;
                            this.RestApi.updateUserDetails(user).success(data => {
                                this.$window.open('https://docs.streampipes.org', '_blank');
                            });
                        }, function () {
                        });
                    }
                })
        }

        angular.element($window).on('scroll', () => {
            JsplumbBridge.repaintEverything();
        });


        $rootScope.$on("elements.loaded", () => {
            this.makeDraggable();
        });

        this.tabs = [
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
    }

    loadSources() {
        var tempStreams = [];
        this.RestApi.getOwnSources()
            .then((sources) => {
                sources.data.forEach((source, i, sources) => {
                    source.spDataStreams.forEach(stream => {
                        stream.type = 'stream';
                        tempStreams = tempStreams.concat(stream);
                    });
                    this.allElements["stream"] = tempStreams;
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
                $('#outerAssemblyArea').css('border', '3px dashed rgb(255,64,129)');
            },
            stop: function (el, ui) {
                $('#outerAssemblyArea').css('border', '1px solid rgb(63,81,181)');
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
    'AuthStatusService'];
