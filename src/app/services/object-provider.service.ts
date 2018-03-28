import * as angular from 'angular';

declare const jsPlumb: any;

export class ObjectProvider {

    RestApi: any;
    JsplumbBridge: any;
    ImageChecker: any;
    adjustingPipelineState: any;
    plumbReady: any;
    sources: any;
    sepas: any;
    actions: any;
    currentElement: any;
    //this.currentPipeline = new this.Pipeline();
    adjustingPipeline: any;

    constructor(RestApi, JsplumbBridge) {
        this.RestApi = RestApi;
        this.JsplumbBridge = JsplumbBridge;
    }

    prepareElement(json) {
        json.connectedTo = [];
        return json;
    }

    preparePipeline() {
        var pipeline = {};
        pipeline['name'] = "";
        pipeline['description'] = "";
        pipeline['streams'] = [];
        pipeline['sepas'] = [];
        pipeline['actions'] = [];

        return pipeline;
    }

    makeFinalPipeline(currentPipelineElements) {
        return this.makePipeline(currentPipelineElements);
    }

    makePipeline(currentPipelineElements) {
        var pipeline = this.preparePipeline();
        pipeline = this.addElementNew(pipeline, currentPipelineElements);
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

    addElementNew(pipeline, currentPipelineElements) {
        currentPipelineElements.forEach(pe => {
            if (pe.type === 'sepa' || pe.type === 'action') {
                var payload = pe.payload;
                payload = this.prepareElement(payload);
                var connections = this.JsplumbBridge.getConnections({
                    target: $("#" +payload.DOM)
                });
                for (var i = 0; i < connections.length; i++) {
                    payload.connectedTo.push(connections[i].sourceId);
                }
                pe.type === 'action' ? pipeline.actions.push(payload) : pipeline.sepas.push(payload);
            }
            else {
                pipeline.streams.push(pe.payload);
            }
        });
        return pipeline;
    }

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

//ObjectProvider.$inject = ['RestApi', 'JsplumbBridge'];
