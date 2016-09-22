objectProvider.$inject = ['$http', 'restApi', 'imageChecker'];

export default function objectProvider($http, restApi, imageChecker) {
        var oP = this;

        this.Stream = function (element) {

            var json = $(element).data("JSON");
            angular.copy(json, this);
            this.DOM = element.id;

        };
        this.Sepa = function (element) {

            var json = $(element).data("JSON");
            angular.copy(json, this);
            this.DOM = element.id;
            this.connectedTo = [];

        };
        this.Action = function (element) {
            var json = $(element).data("JSON");
            angular.copy(json, this);
            this.DOM = element.id;
            this.connectedTo = [];

        };

        this.Pipeline = function () {
            this.name = "";
            this.description = "";
            this.streams = [];
            this.sepas = [];
            this.actions = [];

            this.hasElement = function(domId) {
                var exists = false;
                var allElements = this.streams.concat(this.sepas).concat(this.actions);
                angular.forEach(allElements, function(el) {
                    if (el.DOM == domId) exists = true;
                });
                return exists;
            };

            this.addElement = function (element) {

                var $element = $(element);
                var connections = jsPlumb.getConnections({
                    target: element
                });
                if ($element.hasClass('connectable-block')) {
                    var block = $(element).data("block");
                    this.addBlock(block);
                }

                if ($element.hasClass('action')) {

                    var action = new oP.Action(element);
                    action.staticProperties = $element.data("JSON").staticProperties;
                    action.connectedTo = [];

                    for (var i = 0; i < connections.length; i++) {
                        var conObjId = "#" + connections[i].sourceId;
                        var $conObj = $(conObjId);

                        if ($conObj.hasClass('connectable-block')) {
                            var block = $conObj.data("block");
                            action.connectedTo.push(block.sepas[block.outputIndex].DOM)
                            //this.addBlock($conObj.data("block"), this.action);
                        } else {
                            action.connectedTo.push(connections[i].sourceId);
                        }
                    }
                    this.actions.push(action);
                } else if ($element.hasClass('sepa')) {
                    var el = new oP.Sepa(element);

                    el.staticProperties = $element.data("JSON").staticProperties;
                    el.connectedTo = [];
                    for (var i = 0; i < connections.length; i++) {
                        var conObjId = "#" + connections[i].sourceId;
                        var $conObj = $(conObjId);

                        if ($conObj.hasClass('connectable-block')) {
                            var block = $conObj.data("block");
                            el.connectedTo.push(block.sepas[block.outputIndex].DOM)
                            //this.addBlock($conObj.data("block"));
                        } else {
                            el.connectedTo.push(connections[i].sourceId);
                        }
                    }
                    this.sepas.push(el);

                } else if ($element.hasClass('stream')) {
                    var el = new oP.Stream(element);
                    this.streams.push(el);
                }

            };
            this.addBlock = function (block) {
                //connectedElement.connectedTo.push(block.pipeline.sepas[block.outputIndex].DOM)
                for (var i in block.sepas) {
                    this.sepas.push(block.sepas[i]); //TODO evtl keine Referenz?
                }
                for (var i in block.streams) {
                    this.streams.push(block.streams[i]);
                }
            };

            this.update = function (info, success) {
                var pipeline = this;
                return restApi.updatePartialPipeline(pipeline);

            };

            this.send = function () {
                var pipeline = this;
                return restApi.storePipeline(pipeline);

            }

        };

        this.State = function () {
            this.adjustingPipelineState = false;
            this.plumbReady = false;
            this.sources = {};
            this.sepas = {};
            this.actions = {};
            this.currentElement = {};
            this.currentPipeline = new oP.Pipeline();
            this.adjustingPipeline = {};
        };

        this.recElement = function (json) {
            this.json = json;
            this.name = json.name;
            this.getjQueryElement = function () {
                var element = this;
                var $el = $('<a style="text-decoration: none">')
                    .data("recObject", element);
                addImageOrTextIcon($el, element.json, true, 'recommended');

                return $el;
            };
        };

        this.Block = function (name, description, pipeline) {

            var oi;
            for (var i in pipeline.sepas) {
                if (jsPlumb.getConnections({source: pipeline.sepas[i].DOM}).length == 0) {
                    oi = i;
                    break;
                }
            }
            this.name = name;
            this.description = description;
            this.outputIndex = oi;
            this.sepas = $.extend([], pipeline.sepas);
            this.streams = $.extend([], pipeline.streams);

            this.getjQueryElement = function () {
                var block = this;
                var $inner = $('<div>')
                    .addClass("block-img-container");
                addImageOrTextIcon($inner, block, false, 'block');
                return $('<div>')
                    .data("block", $.extend({}, this))
                    .addClass("connectable-block")
                    .append($('<div>').addClass("block-name tt").text(block.name)
                        .attr({
                            "data-toggle": "tooltip",
                            "data-placement": "top",
                            "data-delay": '{"show": 100, "hide": 100}',
                            title: block.description
                        })
                    )
                    .append($inner);
            }
        };

        function addImageOrTextIcon($element, json, small, type) {
            var iconUrl = "";
            if (type == 'block') {
                iconUrl = json.streams[0].iconUrl;
            } else {
                iconUrl = json.iconUrl;
            }
            imageChecker.imageExists(iconUrl, function (exists) {
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
                        .text(getElementIconText(name) || "N/A")
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

        var getElementIconText = function (string) {
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

        //return oP;
    };
