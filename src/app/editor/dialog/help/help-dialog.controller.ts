export class HelpDialogController {

    $mdDialog: any;
    pipelineElement: any;
    selectedTab = 0;
    RestApi: any;
    latestMeasurements: any;
    $timeout: any;
    pollingActive;
    error: any;

    dtOptions = {paging: false, searching: false};
    nsPrefix = "http://www.w3.org/2001/XMLSchema#";
    tabs = [
        {
            title: 'Fields',
            type: 'fields',
        },
        {
            title: 'Values',
            type: 'values',
        },
        {
            title: 'Raw',
            type: 'raw',
        }
    ];

    constructor($mdDialog, pipelineElement, RestApi, $timeout) {
        this.$mdDialog = $mdDialog;
        this.pipelineElement = pipelineElement;
        this.RestApi = RestApi;
        this.$timeout = $timeout;
        this.pollingActive = true;
        this.error = false;
        if (this.pipelineElement.type == 'stream') {
            this.loadCurrentData();
        }
    }

    setSelectedTab(type) {
        //this.selectedTab = type;
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
        this.RestApi.getRuntimeInfo(this.pipelineElement).success(data => {
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


}

HelpDialogController.$inject = ['$mdDialog', 'pipelineElement', 'RestApi', '$timeout'];