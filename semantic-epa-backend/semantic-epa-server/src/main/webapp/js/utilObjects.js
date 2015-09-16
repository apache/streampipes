//
//
//function Stream(element){
//
//    var json = $(element).data("JSON");
//
//    this.DOM = element.id;
//    this.name = json.name;
//    this.elementId = json.elementId;
//    this.description = json.description;
//    this.iconUrl = json.iconUrl;
//    this.domains = json.domains;
//
//}
//function Sepa(element){
//
//    var json = $(element).data("JSON");
//
//    this.DOM = element.id;
//    this.name = json.name;
//    this.elementId = json.elementId;
//    this.description = json.description;
//    this.iconUrl = json.iconUrl;
//    this.connectedTo = [];
//    this.domains = json.domains;
//    this.staticProperties = json.staticProperties;
//
//}
//function Action(element){
//    var json = $(element).data("JSON");
//
//    this.DOM = element.id;
//    this.name = json.name;
//    this.elementId = json.elementId;
//    this.description = json.description;
//    this.iconUrl = json.iconUrl;
//    this.connectedTo = [];
//    this.staticProperties = json.staticProperties;
//
//}
//
//function Pipeline(){
//    this.name = "";
//    this.description = "";
//    this.streams = [];
//    this.sepas = [];
//    this.action = {};
//    this.addElement = function(element){
//        var $element = $(element);
//        var connections = jsPlumb.getConnections({
//            target : element
//        });
//
//        if ($element.hasClass('block')){
//            var block = $(element).data("block");
//            this.addBlock(block);
//        }
//
//        if ($element.hasClass('action')){
//
//            this.action = new Action(element);
//
//            for (var i = 0; i < connections.length; i++) {
//                var conObjId = "#" + connections[i].sourceId;
//                var $conObj = $(conObjId);
//
//                if ($conObj.hasClass('block')){
//                    var block = $conObj.data("block");
//                    this.action.connectedTo.push(block.pipeline.sepas[block.outputIndex].DOM)
//                    //this.addBlock($conObj.data("block"), this.action);
//                }else {
//                    this.action.connectedTo.push(connections[i].sourceId);
//                }
//            }
//        }else if ($element.hasClass('sepa')){
//            var el = new Sepa(element);
//
//            el.staticProperties = $element.data("JSON").staticProperties;
//            el.connectedTo = [];
//            for (var i = 0; i < connections.length; i++) {
//                var conObjId = "#" + connections[i].sourceId;
//                var $conObj = $(conObjId);
//
//                if ($conObj.hasClass('block')){
//                    var block = $conObj.data("block");
//                    el.connectedTo.push(block.pipeline.sepas[block.outputIndex].DOM)
//                    //this.addBlock($conObj.data("block"));
//                }else {
//                    el.connectedTo.push(connections[i].sourceId);
//                }
//            }
//            this.sepas.push(el);
//
//        } else if ($element.hasClass('stream')){
//            var el = new Stream(element);
//            this.streams.push(el);
//        }
//
//    }
//    this.addBlock = function(block){
//        //connectedElement.connectedTo.push(block.pipeline.sepas[block.outputIndex].DOM)
//        for (var i in block.pipeline.sepas){
//            this.sepas.push(block.pipeline.sepas[i]); //TODO evtl keine Referenz?
//        }
//        for (var i in block.pipeline.streams){
//            this.streams.push(block.pipeline.streams[i]);
//        }
//    }
//
//    this.update = function(info){
//        var pipeline = this;
//        return $.ajax({
//            url: standardUrl +"pipelines/update",
//            data: JSON.stringify(pipeline),
//            processData: false,
//            type: 'POST',
//            success: function (data) {
//                if (data.success) {			//TODO Objekt im Backend �ndern
//                    modifyPipeline(data.pipelineModifications);
//                    for (var i = 0, sepa; sepa = pipeline.sepas[i]; i++) {
//                        var id = "#" + sepa.DOM;
//                        if ($(id).length > 0) {
//                            if ($(id).data("options") != true) {
//                                if (!isFullyConnected(id)) {
//                                    return;
//                                }
//                                $('#customize-content').html(prepareCustomizeModal($(id)));
//                                $(textInputFields).each(function (index, value) {
//                                    addAutoComplete(value.fieldName, value.propertyName);
//                                });
//                                var iwbUri = "https://localhost:8443/resource/?uri=" +sepa.elementId;
//                                var string = "Customize " + sepa.name +"  <a target='_blank' href='" +iwbUri +"'<span class='glyphicon glyphicon-question-sign' aria-hidden='true'></span></a>";
//                                $('#customizeTitle').html(string);
//                                $('#customizeModal').modal('show');
//
//                            }
//                        }
//
//                    }
//                    if (!$.isEmptyObject(pipeline.action)) {
//                        var id = "#" + pipeline.action.DOM;
//                        if (!isFullyConnected(id)) {
//                            return;
//                        }
//                        $('#customize-content').html(prepareCustomizeModal($(id)));
//                        var iwbUri = "https://localhost:8443/resource/?uri=" +pipeline.action.elementId;
//                        var string = "Customize " + pipeline.action.name +"  <a target='_blank' href='" +iwbUri +"'<span class='glyphicon glyphicon-question-sign' aria-hidden='true'></span></a>";
//                               ;
//                        $('#customizeTitle').html(string);
//                        $('#customizeModal').modal('show');
//                    }
//
//                } else {
//                    jsPlumb.detach(info.connection);
//                    displayErrors(data);
//                }
//
//            },
//            error: function (data) {
//                toastRightTop("error", "Could not fulfill request", "Connection Error");
//            }
//        });
//    }
//
//    this.send = function(overWrite){
//        var pipeline = this;
//        return $.ajax({
//            url: standardUrl +"v2/users/riemer@fzi.de/pipelines",
//            data: JSON.stringify(pipeline),
//            processData: false,
//            type: 'POST',
//            success: function (data) {
//                if (data.success) {
//                    displaySuccess(data);
//                    if (state.adjustingPipelineState && overWrite) {
//                        var pipelineId = state.adjustingPipeline._id;
//                        var url = standardUrl + "pipelines/" + pipelineId;
//                        $.ajax({
//                            url: url,
//                            success: function (data) {
//                                if (data.success) {
//                                    state.adjustingPipelineState = false;
//                                    $("#overwriteCheckbox").css("display", "none");
//                                    refresh("Proa");
//                                } else {
//                                    displayErrors(data);
//                                }
//                            },
//                            error: function (data) {
//                                console.log(data);
//                            },
//                            type: 'DELETE',
//                            processData: false
//                        });
//                    }
//                    refresh("Proa");
//
//
//                } else {
//                    displayErrors(data);
//                }
//            },
//            error: function (data) {
//                toastRightTop("error", "Could not fulfill request", "Connection Error");
//            }
//        });
//    }
//
//}
//
//function State(){
//    this.adjustingPipelineState = false;
//    this.plumbReady = false;
//    this.sources = {};
//    this.sepas = {};
//    this.actions = {};
//    this.currentElement = {};
//    this.currentPipeline = new Pipeline();
//    this.adjustingPipeline = {};
//}
//
//function recElement(json){
//    console.log(json);
//    this.json = json;
//    this.name = json.name;
//    this.getjQueryElement = function(){
//        var element = this;
//        return $('<a>')
//            .data("recObject", element)
//            .append($('<img>').attr("src", element.json.iconUrl).error(function(){
//                addTextIconToElement($(this).parent(), element.name, true);
//                $(this).remove();
//
//            }).addClass("recommended-item-img"));
//
//    };
//}
