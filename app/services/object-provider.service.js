export class ObjectProvider {

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
        pipeline.name = "";
        pipeline.description = "";
        pipeline.streams = [];
        pipeline.sepas = [];
        pipeline.actions = [];

        return pipeline;
    }

    makeFinalPipeline(currentPipelineElements) {
        return this.makePipeline(currentPipelineElements, currentPipelineElements[currentPipelineElements.length - 1].payload.DOM);
    }

    makePipeline(currentPipelineElements, rootElementId) {
        var domElement = $("#" + rootElementId);
        var pipeline = this.preparePipeline();
        var rootPipelineElement = this.findElement(rootElementId, currentPipelineElements);
        this.addElement(domElement, rootPipelineElement, currentPipelineElements, pipeline);
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
        var connections = this.JsplumbBridge.getConnections({
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

ObjectProvider.$inject = ['RestApi', 'JsplumbBridge'];