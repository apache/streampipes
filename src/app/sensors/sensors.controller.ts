import * as angular from 'angular';

declare const require: any;

export class SensorsCtrl {

    RestApi: any;
    $filter: any;
    editingDisabled: any;
    categoryOpt: any;
    sepas: any;
    sources: any;
    actions: any;
    selectedSepa: any;
    selectedSource: any;
    selectedAction: any;
    selectedStream: any;
    sepaSelected: any;
    sourceSelected: any;
    actionSelected: any;
    streamSelected: any;
    availableEpaCategories: any;
    availableEcCategories: any;
    selectedCategories: any;
    selectedTab: any;
    activeProducerTab: any;
    activeStreamTab: any;
    activeEpaTab: any;
    activeConsumerTab: any;
    showHints: any;
    deploymentSettings: any

    constructor(RestApi, $filter, $templateCache) {
        $templateCache.put('templates/grounding.html', require('./templates/grounding.html'));
        $templateCache.put('templates/sec.html', require('./templates/sec.html'));
        $templateCache.put('templates/sec-menu-bar.html', require('./templates/sec-menu-bar.html'));
        $templateCache.put('templates/sep.html', require('./templates/sep.html'));
        $templateCache.put('templates/sep-menu-bar.html', require('./templates/sep-menu-bar.html'));
        $templateCache.put('templates/sepa.html', require('./templates/sepa.html'));
        $templateCache.put('templates/sepa-menu-bar.html', require('./templates/sepa-menu-bar.html'));
        $templateCache.put('templates/stream.html', require('./templates/stream.html'));

        this.RestApi = RestApi;
        this.$filter = $filter;

        this.editingDisabled = true;

        this.categoryOpt = {displayProp: 'type', idProp: 'type', externalIdProp: 'type'};

        this.sepas = [];
        this.sources = [];
        this.actions = [];

        this.selectedSepa;
        this.selectedSource;
        this.selectedAction;
        this.selectedStream;

        this.sepaSelected = false;
        this.sourceSelected = false;
        this.actionSelected = false;
        this.streamSelected = false;

        this.availableEpaCategories = [];
        this.availableEcCategories = [];

        this.selectedCategories = [];

        this.selectedTab = "SOURCES";

        this.activeProducerTab = "basics";
        this.activeStreamTab = "basics";
        this.activeEpaTab = "basics";
        this.activeConsumerTab = "basics";

        this.showHints = false;

        this.deploymentSettings = [{
            "elementType": "SEPA",
            "outputTypes": [{"type": "IMPLEMENTATION", "description": "I'd like to generate a runtime implementation."},
                {"type": "DESCRIPTION", "description": "I'd like to generate the description only."}],
            "runtimeType": {
                "title": "Runtime implementation",
                "runtimeTypes": [{"type": "ALGORITHM", "description": "Custom implementation"},
                    {"type": "ESPER", "description": "Esper"},
                    {"type": "FLINK", "description": "Apache Flink"},
                    {"type": "STORM", "description": "Apache Storm"}]
            }
        },
            {
                "elementType": "SEP",
                "outputTypes": [{
                    "type": "IMPLEMENTATION",
                    "description": "I'd like to implement an adapter based on this description."
                },
                    {"type": "DESCRIPTION", "description": "I'd like to generate the description only."},
                    {
                        "type": "DIRECT_IMPORT",
                        "description": "Specified streams are already available on the message broker (no adapter implementation needed)."
                    }],
                "runtimeType": {
                    "title": "Adapter type (beta)",
                    "runtimeTypes": [{"type": "CUSTOM", "description": "Custom adapter"},
                        {"type": "OPC", "description": "OPC adapter"},
                        {"type": "FILE", "description": "File adapter"},
                        {"type": "MYSQL", "description": "MySQL adapter"}]
                }
            },
            {
                "elementType": "SEC",
                "outputTypes": [{
                    "type": "IMPLEMENTATION",
                    "description": "I'd like to generate a runtime implementation."
                },
                    {"type": "DESCRIPTION", "description": "I'd like to generate the description only."},
                ],
                "runtimeTypes": {
                    "title": "Runtime implementation",
                    "runtimeTypes": [{"type": "ACTION", "description": "Custom implementation"},
                        {"type": "ACTION_FLINK", "description": "Apache Flink"},
                    ]
                }
            }];
    }

    $onInit() {
        this.loadSepas();
        this.loadActions();
        this.loadSources();
        this.loadEcCategories();
        this.loadEpaCategories();
    }


    setSelectedTab(type) {
        this.selectedTab = type;
    }

    toggleEditMode() {
        this.editingDisabled = !this.editingDisabled;

    }

    selectProducerTab(name) {
        this.activeProducerTab = name;
    }

    isProducerTabSelected(name) {
        return this.activeProducerTab == name;
    }

    getProducerActiveTabCss(name) {
        if (name == this.activeProducerTab) {
            return "md-fab md-accent";
        }
        else {
            return "md-fab md-accent wizard-inactive";
        }
    }

    selectEpaTab(name) {
        this.activeEpaTab = name;
    }

    isEpaTabSelected(name) {
        return this.activeEpaTab == name;
    }

    getEpaActiveTabCss(name) {
        if (name == this.activeEpaTab) {
            return "md-fab md-accent";
        }
        else {
            return "md-fab md-accent wizard-inactive";
        }
    }

    selectConsumerTab(name) {
        this.activeConsumerTab = name;
    }

    isConsumerTabSelected(name) {
        return this.activeConsumerTab == name;
    }

    getConsumerActiveTabCss(name) {
        if (name == this.activeConsumerTab) return "md-fab md-accent";
        else return "md-fab md-accent wizard-inactive";
    }


    removeStream(eventStreams, stream) {
        eventStreams.splice(stream, 1);
    }

    loadStreamDetails(stream, editingDisabled?) {
        this.editingDisabled = editingDisabled;
        this.streamSelected = true;
        this.selectedStream = stream;
    }

    addNewSepa() {
        this.selectedSepa = {"spDataStreams": [], "name": "", "staticProperties": []};
        this.sepaSelected = true;
        this.editingDisabled = false;
    }

    addNewAction() {
        this.selectedAction = {"spDataStreams": [], "name": "", "staticProperties": []};
        this.actionSelected = true;
        this.editingDisabled = false;
    }

    addNewSource() {
        this.selectedSource = undefined;
        this.selectedSource = {"spDataStreams": [], "name": ""};
        this.sourceSelected = true;
        this.streamSelected = false;
        this.selectedStream = "";
        this.editingDisabled = false;
        this.activeProducerTab = "basics";
        this.activeStreamTab = "basics";
    }

    addStream(element) {
        element.push({
            "name": "",
            "eventSchema": {"eventProperties": []},
            "eventGrounding": {"transportFormats": [], "transportProtocols": []}
        });
        this.loadStreamDetails(element[element.length - 1]);
    }

    cloneStream(eventStreams, stream) {
        var clonedStream = angular.copy(stream);
        clonedStream.uri = "";
        eventStreams.push(clonedStream);
    }

    loadSepaDetails(uri, keepIds, editingDisabled) {
        this.RestApi.getSepaDetailsFromOntology(uri, keepIds)
            .then(sepaData => {
                this.selectedSepa = sepaData.data;
                this.sepaSelected = true;
                this.editingDisabled = editingDisabled;
            });
    }

    loadActionDetails(uri, keepIds, editingDisabled) {
        this.RestApi.getActionDetailsFromOntology(uri, keepIds)
            .then(actionData => {
                this.selectedAction = actionData.data;
                this.actionSelected = true;
                this.editingDisabled = editingDisabled;
            });
    }

    loadSourceDetails(index) {
        this.editingDisabled = true;
        this.sourceSelected = true;
        this.selectedSource = this.sources[index];
    }

    loadSepas() {
        this.RestApi.getSepasFromOntology()
            .then(sepaData => {
                this.sepas = this.$filter('orderBy')(sepaData.data, "name", false);
                ;
            });
    };

    getSourceDetailsFromOntology(sourceId) {
        this.RestApi.getSourceDetailsFromOntology(sourceId, false)
            .then(source => {
                this.editingDisabled = false;
                this.sourceSelected = true;
                this.selectedSource = source.data;
                this.selectedSource.uri = "";
                angular.forEach(this.selectedSource.spDataStreams, (stream, key) => {
                    stream.uri = "";
                });
            });
    }

    loadSources() {
        this.RestApi.getSourcesFromOntology()
            .then(sources => {
                this.sources = this.$filter('orderBy')(sources.data, "name", false);
            });
    };

    loadActions() {
        this.RestApi.getActionsFromOntology()
            .then(actions => {
                this.actions = this.$filter('orderBy')(actions.data, "name", false);
            });
    };

    loadEpaCategories() {
        this.RestApi.getEpaCategories()
            .then(epas => {
                this.availableEpaCategories = epas.data;
            });
    }

    loadEcCategories() {
        this.RestApi.getEcCategories()
            .then(ecs => {
                this.availableEcCategories = ecs.data;
            });
    }

}

SensorsCtrl.$inject = ['RestApi', '$filter', '$templateCache'];
