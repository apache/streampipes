export class ObjectProvider {

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
                pipeline.streams.push(pe.payload);
            } else if (pe.type === 'sepa') {
                pipeline.sepas.push(pe.payload);
            } else if (pe.type === 'action') {
                pipeline.actions.push(pe.payload);
            }
        })

        return pipeline;
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

    makePipeline(element, currentPipelineElements, rootElementId) {
        var pipeline = this.preparePipeline();
        var rootElement = this.findElement(rootElementId, currentPipelineElements);
        this.addElement(element, rootElement, currentPipelineElements, pipeline);
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
    }
    ;

    recElement(json) {
        this.json = json;
        this.name = json.name;
        this.getjQueryElement = function () {
            var element = this;
            var $el = $('<a style="text-decoration: none">')
                .data("recObject", element);
            this.addImageOrTextIcon($el, element.json, true, 'recommended');

            return $el;
        };
    }

    addImageOrTextIcon($element, json, small, type) {
        var iconUrl = "";
        if (type == 'block') {
            iconUrl = json.streams[0].iconUrl;
        } else {
            iconUrl = json.iconUrl;
        }
        ImageChecker.imageExists(iconUrl, function (exists) {
            if (exists) {
                var $img = $('<img>')
                    .attr("src", iconUrl)
                    .data("JSON", $.extend(true, {}, json));
                if (type == 'draggable') {
                    $img.addClass("draggable-img tt");
                } else if (type == 'connectable') {
                    $img.addClass('connectable-img tt');
                } else if (type == 'block') {
                    $img.addClass('block-img tt');
                } else if (type == 'recommended') {
                    $img.addClass('recommended-item-img tt');
                }
                $element.append($img);
            } else {
                var name = "";
                if (type == 'block') {
                    name = json.streams[0].name;
                } else {
                    name = json.name;
                }
                var $span = $("<span>")
                    .text(this.getElementIconText(name) || "N/A")
                    .attr(
                        {
                            "data-toggle": "tooltip",
                            "data-placement": "top",
                            "data-delay": '{"show": 1000, "hide": 100}',
                            title: name
                        })
                    .data("JSON", $.extend(true, {}, json));
                if (small) {
                    $span.addClass("element-text-icon-small")
                } else {
                    $span.addClass("element-text-icon")
                }
                $element.append($span);
            }
        });
    }

    getElementIconText(string) {
        var result = "";
        if (string.length <= 4) {
            result = string;
        } else {
            var words = string.split(" ");
            words.forEach(function (word, i) {
                if (i < 4) {
                    result += word.charAt(0);
                }
            });
        }
        return result.toUpperCase();
    }
}

ObjectProvider.$inject = ['$http', 'RestApi', 'ImageChecker'];