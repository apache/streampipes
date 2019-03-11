export class HelpDialogController {

    $mdDialog: any;
    pipelineElement: any;
    selectedTab = 0;
    RestApi: any;
    latestMeasurements: any;
    $timeout: any;
    pollingActive;
    error: any;
    ctrl: any;

    dtOptions = {paging: false, searching: false};
    nsPrefix = "http://www.w3.org/2001/XMLSchema#";
    tabs = [
        {
            title: 'Fields',
            type: 'fields',
            targets: ['set', 'stream']
        },
        {
            title: 'Values',
            type: 'values',
            targets: ['set', 'stream']
        },
        {
            title: 'Raw',
            type: 'raw',
            targets: ['set', 'stream']
        },
        {
            title: 'Documentation',
            type: 'documentation',
            targets: ['set', 'stream', 'sepa', 'action']
        }
    ];

    constructor($mdDialog, pipelineElement, RestApi, $timeout) {
        this.$mdDialog = $mdDialog;
        this.pipelineElement = pipelineElement;
        this.RestApi = RestApi;
        this.$timeout = $timeout;
        this.pollingActive = true;
        this.error = false;
    }

    $onInit() {
        if (this.pipelineElement.type == 'stream') {
            this.loadCurrentData();
        }
    }

    setSelectedTab(type) {
        this.selectedTab = type;
    }

    getFriendlyRuntimeType(runtimeType) {
        if (this.isNumber(runtimeType)) {
            return "Number";
        } else if (this.isBoolean(runtimeType)) {
            return "Boolean";
        } else {
            return "Text";
        }
    }

    isNumber(runtimeType) {
        return (runtimeType == (this.nsPrefix + "float")) ||
            (runtimeType == (this.nsPrefix + "integer")) ||
            (runtimeType == (this.nsPrefix + "long")) ||
            (runtimeType == (this.nsPrefix + "double"));
    }

    isBoolean(runtimeType) {
        return runtimeType == this.nsPrefix + "boolean";
    }

    hide() {
        this.pollingActive = false;
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };

    loadCurrentData() {
        this.RestApi.getRuntimeInfo(this.pipelineElement).then(msg => {
            let data = msg.data;
            if (!data.notifications) {
                this.error = false;
                this.latestMeasurements = data;
            } else {
                this.error = true;
            }
            if (this.pollingActive) {
                this.$timeout(() => {
                    this.loadCurrentData();
                }, 1000)
            }
        });
    }

    filterTab(tab) {
        return tab.targets.indexOf(this.ctrl.pipelineElement.type) != -1;
    }


}

HelpDialogController.$inject = ['$mdDialog', 'pipelineElement', 'RestApi', '$timeout'];