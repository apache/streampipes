import * as angular from 'angular';
declare const jsPlumb: any;

export class ObjectProvider {

    $http: any;
    RestApi: any;
    ImageChecker: any;
    adjustingPipelineState: any;
    plumbReady: any;
    sources: any;
    sepas: any;
    actions: any;
    currentElement: any;
    //this.currentPipeline = new this.Pipeline();
    adjustingPipeline: any;

    constructor($http, RestApi, ImageChecker) {
        this.$http = $http;
        this.RestApi = RestApi;
        this.ImageChecker = ImageChecker;
    }

    prepareElement(json) {
        json.connectedTo = [];
        return json;
    }

    makePipeline(pipelineModel) {
        var pipeline = this.preparePipeline();

        angular.forEach(pipelineModel, pe => {
            if (pe.type === 'stream') {
                pipeline['streams'].push(pe.payload);
            } else if (pe.type === 'sepa') {
                pipeline['sepas'].push(pe.payload);
            } else if (pe.type === 'action') {
                pipeline['actions'].push(pe.payload);
            }
        })

        return pipeline;
    }

    // TODO: Function overloading?
/*
    makePipeline(element, currentPipelineElements, rootElementId) {
        var pipeline = this.preparePipeline();
        var rootElement = this.findElement(rootElementId, currentPipelineElements);
        this.addElement(element, rootElement, currentPipelineElements, pipeline);
        return pipeline;
    }
*/
    preparePipeline() {
        var pipeline = {};
        pipeline['name'] = "";
        pipeline['description'] = "";
        pipeline['streams'] = [];
        pipeline['sepas'] = [];
        pipeline['actions'] = [];

        return pipeline;
    }

    findElement(elementId, currentPipeline) {
        var result = {};
        angular.forEach(currentPipeline, pe => {
            if (pe.payload.DOM === elementId) {
                result = pe;
            }
        });
        return result;
    }

    addElement(element, rootElement, currentPipelineElements, pipeline) {
        var connections = jsPlumb.getConnections({
            target: element
        });
        if (rootElement.type === 'action' || rootElement.type === 'sepa') {
            var el = this.prepareElement(rootElement.payload);

            for (var i = 0; i < connections.length; i++) {
                el.connectedTo.push(connections[i].sourceId);
            }
            rootElement.type === 'action' ? pipeline.actions.push(el) : pipeline.sepas.push(el);
            for (var i = 0; i < el.connectedTo.length; i++) {
                var $conObj = $("#" + el.connectedTo[i]);
                this.addElement($conObj, this.findElement(el.connectedTo[i], currentPipelineElements), currentPipelineElements, pipeline);
            }
        } else if (rootElement.type === 'stream') {
            var el = rootElement.payload;
            pipeline.streams.push(el);
        }
    };

    updatePipeline(pipeline) {
        return this.RestApi.updatePartialPipeline(pipeline);
    };

    storePipeline(pipeline) {
        return this.RestApi.storePipeline(pipeline);

    }

    State() {
        this.adjustingPipelineState = false;
        this.plumbReady = false;
        this.sources = {};
        this.sepas = {};
        this.actions = {};
        this.currentElement = {};
        //this.currentPipeline = new this.Pipeline();
        this.adjustingPipeline = {};
    };
}

//ObjectProvider.$inject = ['$http', 'RestApi', 'ImageChecker'];