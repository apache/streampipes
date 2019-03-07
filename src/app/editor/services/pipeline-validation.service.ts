import * as angular from 'angular';
import * as dagre from 'dagre';

export class PipelineValidationService {

    ObjectProvider: any;
    errorMessages: any = [];
    JsplumbBridge: any;

    availableErrorMessages: any = [
        {title: "Did you add a data stream?", content: "Any pipeline needs at least one data stream."},
        {title: "Did you add a data sink?", content: "Any pipeline needs at least one data sink."},
        {title: "Did you connect all elements?", content: "No orphaned elements are allowed within a pipeline, make sure to connect all elements."},
        {title: "Separate pipelines", content: "It seems you've created more than one pipeline at once. Create only one pipeline at a time!"}
    ];

    constructor(ObjectProvider, JsplumbBridge) {
        this.ObjectProvider = ObjectProvider;
        this.JsplumbBridge = JsplumbBridge;
    }

    isValidPipeline(rawPipelineModel) {
        let streamInAssembly = this.isStreamInAssembly(rawPipelineModel);
        let sepaInAssembly = this.isSepaInAssembly(rawPipelineModel);
        let actionInAssembly = this.isActionInAssembly(rawPipelineModel);
        let allElementsConnected = true;
        let onlyOnePipelineCreated = true;

        if (streamInAssembly && (sepaInAssembly || actionInAssembly)) {
            allElementsConnected = this.allElementsConnected();
        }

        if (streamInAssembly && actionInAssembly && allElementsConnected) {
            onlyOnePipelineCreated = this.onlyOnePipelineCreated();
        }

        if (!this.isEmptyPipeline(rawPipelineModel)) {
            this.buildErrorMessages(streamInAssembly, actionInAssembly, allElementsConnected, onlyOnePipelineCreated);
        }

        return streamInAssembly && actionInAssembly && allElementsConnected && onlyOnePipelineCreated;
    }

    isEmptyPipeline(rawPipelineModel) {
        return !this.isActionInAssembly(rawPipelineModel) && !this.isStreamInAssembly(rawPipelineModel) && !this.isInAssembly(rawPipelineModel, 'sepa');
    }

    buildErrorMessages(streamInAssembly, actionInAssembly, allElementsConnected, onlyOnePipelineCreated) {
        this.errorMessages = [];
        if (!streamInAssembly) {
            this.errorMessages.push(this.availableErrorMessages[0]);
        }
        if (!actionInAssembly) {
            this.errorMessages.push(this.availableErrorMessages[1]);
        }
        if (!allElementsConnected) {
            this.errorMessages.push(this.availableErrorMessages[2]);
        }
        if (!onlyOnePipelineCreated) {
            this.errorMessages.push(this.availableErrorMessages[3]);
        }
    }

    allElementsConnected() {
        let g = this.makeGraph();
        return g.nodes().every(node => g.outEdges(node).length > 0);
    }

    isStreamInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "stream") || this.isInAssembly(rawPipelineModel, "set");
    }

    isActionInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "action");
    }

    isSepaInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "sepa");
    }

    onlyOnePipelineCreated() {
        let g = this.makeGraph();
        let tarjan = dagre.graphlib.alg.tarjan(g);

        return tarjan.length == 1;
    }

    isInAssembly(rawPipelineModel, type) {
        var isElementInAssembly = false;
        angular.forEach(rawPipelineModel, pe => {
            if (pe.type === type && !pe.settings.disabled) {
                isElementInAssembly = true;
            }
        });
        return isElementInAssembly;
    }

    makeGraph() {
        var g = new dagre.graphlib.Graph();
        g.setGraph({rankdir: "LR"});
        g.setDefaultEdgeLabel(function () {
            return {};
        });
        var nodes = $("#assembly").find("span[id^='jsplumb']").get();

        for (var i = 0; i < nodes.length; i++) {
            var n = nodes[i];
            g.setNode(n.id, {label: n.id});
        }
        var edges = this.JsplumbBridge.getAllConnections();
        for (var i = 0; i < edges.length; i++) {
            var c = edges[i];
            g.setEdge(c.source.id, c.target.id);
            g.setEdge(c.target.id, c.source.id);
        }
        return g;
    }
}

PipelineValidationService.$inject=['ObjectProvider', 'JsplumbBridge'];