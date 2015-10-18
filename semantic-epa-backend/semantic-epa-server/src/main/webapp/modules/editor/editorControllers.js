/**
 * Created by Cuddl3s on 13.08.2015.
 */
angular.module('streamPipesApp')
    .controller('EditorCtrl', ['$scope', '$rootScope', '$timeout', '$http','restApi','$stateParams','objectProvider','apiConstants','$q',
        function ($scope, $rootScope,$timeout, $http, restApi, $stateParams, objectProvider, apiConstants, $q) {

            $scope.standardUrl = "http://localhost:8080/semantic-epa-backend/api/";
            $scope.isStreamInAssembly = false;
            $scope.isSepaInAssembly = false;
            $scope.isActionInAssembly = false;
            $scope.currentElements = [];
            $scope.currentModifiedPipeline = $stateParams.pipeline;
            //var editorPlumb;
            var textInputFields = [];
            var connCount = 1;



            $scope.$on('$destroy', function () {
                jsPlumb.deleteEveryEndpoint();
            });

            $scope.$on('$viewContentLoaded', function (event) {
                jsPlumb.setContainer("assembly");

                initAssembly();
                initPlumb();
                console.log($scope.currentModifiedPipeline);
            });
            $rootScope.$on("elements.loaded", function(){
                makeDraggable();
                bindContextMenu();
                //initTooltips();
            });
            $scope.openContextMenu = function($mdOpenMenu, event){
                $mdOpenMenu(event.$event);
                alert("open context menu");
            };
            
            $scope.getOwnBlocks = function(){
                return [];           //TODO anpassen
            };

            $scope.ownBlocksAvailable = function(){
                console.log($scope.getOwnBlocks());
                return false;
            };

            $scope.ownSourcesAvailable = function(){
                return restApi.getOwnSources();
            };

            $scope.ownSepasAvailable = function(){
                return restApi.getOwnSepas();
            };

            $scope.ownActionsAvailable = function(){
                return restApi.getOwnActions();
            };

            $scope.loadCurrentElements = function(type){

                $scope.currentElements = [];
                //$('#editor-icon-stand').children().remove();        //DOM ACCESS
                if (type == 'block'){
                    //TODO BLOCKS
                }else if (type == 'stream'){
                    $scope.loadSources();
                }else if (type == 'sepa'){
                    $scope.loadSepas();
                }else if (type == 'action'){
                    $scope.loadActions();
                }
            };
            
            $scope.displayPipelineById = function(){
                restApi.getPipelineById($scope.currentModifiedPipeline)
                    .success(function(pipeline){
                        console.log("Succes, pipeline retrieved by ID");
                        $scope.displayPipeline(pipeline);
                    })
                    .error(function(msg){
                        console.log(msg);
                    });
                
            };

            $scope.displayPipeline = function(pipeline){
                var currentx = 50;
                var currenty = 50;
                for (var i = 0, stream; stream = pipeline.streams[i]; i++){
                    $scope.streamDropped(createNewAssemblyElement(stream, {'x':currentx, 'y':currenty}));
                    currenty += 200;
                }
                currenty = 50;
                for (var i = 0, sepa; sepa = pipeline.sepas[i]; i++){
                    currentx += 200;
                    var $sepa = $scope.sepaDropped(createNewAssemblyElement(sepa, {'x':currentx, 'y':currenty})
                        .data("options", true));
                    if (jsPlumb.getConnections({source :sepa.DOM}).length == 0){ //Output Element
                        jsPlumb.addEndpoint($sepa, apiConstants.sepaEndpointOptions);
                    }
                }
                currentx += 200;
                if (!$.isEmptyObject(pipeline.action)) {
                    $scope.actionDropped(createNewAssemblyElement(pipeline.action, {'x': currentx, 'y': currenty})
                        .data("options", true));

                }


                connectPipelineElements(pipeline, true);
                //console.log(json);
                jsPlumb.repaintEverything();
            };

            function bindContextMenu(){
                $(".draggable-icon").off("contextmenu").on("contextmenu", staticContextMenu);
            }
            
            function connectPipelineElements(json, detachable){
                console.log("connectPipelineElements()");
                var source, target;
                var sourceEndpoint;
                var targetEndpoint

                jsPlumb.setSuspendDrawing(true);
                if (!$.isEmptyObject(json.action)) {
                    //Action --> Sepas----------------------//
                    target = json.action.DOM;

                    for (var i = 0, connection; connection = json.action.connectedTo[i]; i++) {
                        source = connection;

                        sourceEndpoint = jsPlumb.addEndpoint(source, apiConstants.sepaEndpointOptions);
                        targetEndpoint = jsPlumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                        jsPlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
                    }
                }
                //Sepas --> Streams / Sepas --> Sepas---------------------//
                for (var i = 0, sepa; sepa = json.sepas[i]; i++){
                    for (var j = 0, connection; connection = sepa.connectedTo[j]; j++){

                        source = connection;
                        target = sepa.DOM;


                        var options;
                        var id = "#" + source;
                        console.log($(id));
                        if ($(id).hasClass("sepa")){
                            options = apiConstants.sepaEndpointOptions;
                        }else{
                            options = apiConstants.streamEndpointOptions;
                        }

                        sourceEndpoint = jsPlumb.addEndpoint(source, options);
                        targetEndpoint = jsPlumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
                        jsPlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable:detachable});
                    }
                }
                jsPlumb.setSuspendDrawing(false ,true);
            }

            $scope.tabs = [

                {
                    title : 'Blocks',
                    type: 'block',
                    disabled: !($scope.ownBlocksAvailable())
                },
                {
                    title : 'Streams',
                    type: 'stream',
                    disabled: !($scope.ownSourcesAvailable())
                },
                {
                    title : 'Sepas',
                    type: 'sepa',
                    disabled: !($scope.ownSepasAvailable())
                },
                {
                    title : 'Actions',
                    type: 'action',
                    disabled: !($scope.ownActionsAvailable())
                }
            ];


            $scope.loadSources = function(){
                var tempStreams = [];
                var promises = [];
                restApi.getOwnSources()
                    .then(function(sources){
                        //console.log(sources);
                        sources.data.forEach(function(source, i, sources){
                            promises.push(restApi.getOwnStreams(source));
                                //.then(function(streams){
                                //    //console.log(streams);
                                //
                                //},function(msg){
                                //    console.log(msg);
                                //}));
                            //console.log(promises);
                        });

                        $q.all(promises).then(function(data){
                            console.log(data);
                            data.forEach(function(streams){
                                streams.data.forEach(function(stream){
                                    stream.type = 'stream';
                                });
                                tempStreams = tempStreams.concat(streams.data);
                            });
                            $scope.currentElements = tempStreams;
                            console.log(tempStreams);
                        });

                    }, function(msg){
                        console.log(msg);
                    });
            };


            $scope.loadSepas = function(){
                restApi.getOwnSepas()
                    .success(function(sepas){
                        console.log(sepas);
                        $.each(sepas, function(i, sepa){
                           sepa.type = 'sepa';
                        });
                        $scope.currentElements = sepas;
                        $timeout(function(){
                            //makeDraggable();
                            $rootScope.state.sepas = $.extend(true, [], $scope.currentElements);
                        })

                    })
                    .error(function(msg) {
                        console.log(msg);
                    });
            };
            $scope.loadActions = function(){
                restApi.getOwnActions()
                    .success(function(actions){
                        $.each(actions, function(i, action){
                            action.type = 'action';
                        });
                        $scope.currentElements = actions;
                        $timeout(function(){
                            //makeDraggable();
                            $rootScope.state.actions = $.extend(true, [], $scope.currentElements);
                        })

                    });
            };

            var makeDraggable = function(){
                console.log("MAKING DRAGGABLE");
                $('.draggable-icon').draggable({
                    revert: 'invalid',
                    helper: 'clone',
                    stack: '.draggable-icon',
                    start: function (stream, ui) {
                        ui.helper.appendTo('#content');
                        $('#assembly').css('border-color', 'red');
                    },
                    stop: function (stream, ui) {
                        $('#assembly').css('border-color', '#666666');
                    }
                });
            };

            $scope.streamDropped = function($newElement, endpoints){
                $scope.isStreamInAssembly = true;
                $newElement.addClass("connectable stream");

                if (endpoints) {
                    jsPlumb.addEndpoint($newElement, apiConstants.streamEndpointOptions);
                }
                return $newElement;
            };

            $scope.sepaDropped = function($newElement, endpoints){
                $scope.isSepaInAssembly = true;
                $newElement.addClass("connectable sepa");

                if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState && !$newElement.data("options")) {
                    $newElement
                        .addClass('disabled');
                }


                if (endpoints) {
                    if ($newElement.data("JSON").inputNodes < 2) { //1 InputNode

                        jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
                    } else {
                        jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.25));

                        jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.75));
                    }
                    jsPlumb.addEndpoint($newElement, apiConstants.sepaEndpointOptions);
                }
                return $newElement;
            };
            $scope.actionDropped = function($newElement, endpoints){
                $scope.isActionInAssembly = true;

                $newElement
                    .addClass("connectable action");

                if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState) {
                    $newElement
                        .addClass('disabled');
                }
                if (endpoints) {
                    jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
                }
                return $newElement;
            };

            $scope.elementTextIcon = function (string){
                var result ="";
                if (string.length <= 4){
                    result = string;
                }else {
                    var words = string.split(" ");
                    words.forEach(function(word, i){
                        result += word.charAt(0);
                    });
                }
                return result.toUpperCase();
            }

            //TODO ANGULARIZE
            //Initiate assembly and jsPlumb functionality-------
            function initPlumb(){
                console.log("JSPLUMB EDITOR READY");
                $rootScope.state.plumbReady = true;
                jsPlumb.bind("connection", function (info, originalEvent) {
                    console.log("connection" + connCount++);
                    var $target = $(info.target);
                    if (!$target.hasClass('a')){ //class 'a' = do not show customize modal //TODO class a zuweisen
                        createPartialPipeline(info);
                        $rootScope.state.currentPipeline.update()
                            .success(function(data){
                                if (data.success) {
                                    //TODO Objekt im Backend �ndern
                                    modifyPipeline(data.pipelineModifications);
                                    for (var i = 0, sepa; sepa = $rootScope.state.currentPipeline.sepas[i]; i++) {
                                        var id = "#" + sepa.DOM;
                                        if ($(id).length > 0) {
                                            if ($(id).data("options") != true) {
                                                if (!isFullyConnected(id)) {
                                                    return;
                                                }
                                                $('#customize-content').html($scope.prepareCustomizeModal($(id)));
                                                $(textInputFields).each(function (index, value) {
                                                    addAutoComplete(value.fieldName, value.propertyName);
                                                });
                                                var iwbUri = "https://localhost:8443/resource/?uri=" +sepa.elementId;
                                                var string = "Customize " + sepa.name +"  <a target='_blank' href='" +iwbUri +"'<span class='glyphicon glyphicon-question-sign' aria-hidden='true'></span></a>";
                                                $('#customizeTitle').html(string);
                                                $('#customizeModal').modal('show');
                                            }
                                        }
                                    }
                                    if (!$.isEmptyObject($rootScope.state.currentPipeline.action)) {
                                        var id = "#" + $rootScope.state.currentPipeline.action.DOM;
                                        if (!isFullyConnected(id)) {
                                            return;
                                        }
                                        $('#customize-content').html($scope.prepareCustomizeModal($(id)));
                                        var iwbUri = "https://localhost:8443/resource/?uri=" +$rootScope.state.currentPipeline.action.elementId;
                                        var string = "Customize " + $rootScope.state.currentPipeline.action.name +"  <a target='_blank' href='" +iwbUri +"'<span class='glyphicon glyphicon-question-sign' aria-hidden='true'></span></a>";
                                        ;
                                        $('#customizeTitle').html(string);
                                        $('#customizeModal').modal('show');
                                    }
                                    if ($target.hasClass('sepa')) {
                                        initRecs($rootScope.state.currentPipeline, $target);
                                    }
                                }else{
                                    jsPlumb.detach(info.connection);
                                    displayErrors(data);
                                }
                            })
                            .error(function(data){
                                console.log(data);
                            });
                    }
                });


                window.onresize = function (event) {
                    jsPlumb.repaintEverything(true);
                };
            }

            function initAssembly() {
                console.log("INIT ASSEMBLY")

                $('#assembly').droppable({
                    tolerance: "fit",
                    drop: function (element, ui) {

                        if (ui.draggable.hasClass('draggable-icon')) {
                            //TODO get data
                            //console.log(ui);

                            if (ui.draggable.data("JSON") == null) {
                                alert("No JSON - Data for Dropped element");
                                return false;
                            }
                            //Neues Container Element f�r Icon / identicon erstellen
                            //TODO MOVE TO CONTROLLER
                            var $newState = createNewAssemblyElement(ui.draggable.data("JSON"), getCoordinates(ui), false);

                            //Droppable Streams
                            if (ui.draggable.hasClass('stream')) {
                                $scope.streamDropped($newState, true);

                                var tempPipeline = new objectProvider.Pipeline();
                                tempPipeline.addElement($newState[0]);
                                initRecs(tempPipeline, $newState);

                                //$newState.hover(showRecButton, hideRecButton);

                                //Droppable Sepas
                            } else if (ui.draggable.hasClass('sepa')) {
                                $scope.sepaDropped($newState, true);

                                //Droppable Actions
                            } else if (ui.draggable.hasClass('action')) {
                                $scope.actionDropped($newState, true);
                            }
                            initTooltips();
                        }
                        jsPlumb.repaintEverything(true);
                    }

                }); //End #assembly.droppable()
                $("#assembly")
                    .selectable({
                        selected: function (event, ui) {
                        },
                        filter: ".connectable.stream,.connectable.sepa:not('.disabled')",
                        delay: 150

                    })
                    .on('click',".recommended-item", function (e) {
                        console.log(e);
                        e.stopPropagation();
                        createAndConnect(this);
                    });



                $(document).click(function () {
                    $('#assemblyContextMenu').hide();
                    $('#staticContextMenu').hide();
                    $('.circleMenu-open').circleMenu('close');
                });

                if (typeof $scope.currentModifiedPipeline != 'undefined'){
                    $rootScope.state.adjustingPipelineState = true;
                    $scope.displayPipelineById();
                }

            };


            /**
             * clears the Assembly of all elements
             */
            $scope.clearAssembly = function() {
                $('#assembly').children().not('#clear, #submit').remove();
                jsPlumb.deleteEveryEndpoint();
                $rootScope.state.adjustingPipelineState = false;
            };

            function createPartialPipeline(info) {
                var pipelinePart = new objectProvider.Pipeline();
                var element = info.target;

                addElementToPartialPipeline(element, pipelinePart);
                $rootScope.state.currentPipeline = pipelinePart;

            }

            function addElementToPartialPipeline(element, pipelinePart) {

                pipelinePart.addElement(element);
                var connections = jsPlumb.getConnections({target: element});
                if (connections.length > 0) {
                    for (var i = 0, con; con = connections[i]; i++) {
                        addElementToPartialPipeline(con.source, pipelinePart);
                    }
                }
            }

            /**
             * Sends the pipeline to the server
             */
            $scope.submit = function() {
                var error = false;
                var pipelineNew = new objectProvider.Pipeline();
                var streamPresent = false;
                var sepaPresent = false;
                var actionPresent = false;



                $('#assembly').find('.connectable, .block').each(function (i, element) {
                    var $element = $(element);

                    if (!isConnected(element)) {
                        error = true;

                        toastRightTop("error", "All elements must be connected", "Submit Error");
                    }

                    if ($element.hasClass('sepa')) {
                        sepaPresent = true;
                        if ($element.data("options")) {
                            pipelineNew.addElement(element);

                        } else if ($element.data("JSON").staticProperties != null) {
                            toastRightTop("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
                            ;
                            error = true;

                        }
                    } else if ($element.hasClass('stream')) {
                        streamPresent = true;
                        pipelineNew.addElement(element);


                    } else if ($element.hasClass('action')) {
                        if (actionPresent) {
                            error = true;
                            toastRightTop("error", "More than one action element present in pipeline", "Submit Error");
                        } else {
                            actionPresent = true;
                            if ($element.data("JSON").staticProperties == null || $element.data("options")) {
                                pipelineNew.addElement(element);
                            } else {
                                toastRightTop("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");
                                ;
                                error = true;

                            }
                        }
                    } else if ($element.hasClass('block')) {
                        streamPresent = true;
                        sepaPresent = true;
                        pipelineNew.addElement(element);
                    }
                });
                if (!streamPresent) {
                    toastRightTop("error", "No stream element present in pipeline", "Submit Error");
                    error = true;
                }
                if (!sepaPresent) {
                    toastRightTop("error", "No sepa element present in pipeline", "Submit Error");
                    error = true;
                }
                if (!actionPresent) {
                    toastRightTop("error", "No action element present in pipeline", "Submit Error");
                    error = true;
                }
                if (!error) {

                    $rootScope.state.currentPipeline = pipelineNew;
                    openPipelineNameModal();


                }
            }

            function openPipelineNameModal() {
                if ($rootScope.state.adjustingPipelineState) {
                    var name = $rootScope.state.adjustingPipeline.name;
                    var descr = $rootScope.state.adjustingPipeline.description;

                    $("#nameInput").attr("value", name);
                    $("#descriptionInput").attr("value", descr);
                    $("#overwriteCheckbox").show();
                }
                $('#pipelineNameModal').modal('show');
            }

             $scope.savePipelineName = function() {

                var pipelineName = $('#pipelineNameForm').serializeArray();
                if (pipelineName.length < 2) {
                    toastRightTop("error", "Please enter all parameters");
                    return false;
                }

                $rootScope.state.currentPipeline.name = pipelineName[0].value;
                $rootScope.state.currentPipeline.description = pipelineName[1].value;

                var overWrite;

                if (!($("#overwriteCheckbox").css('display') == 'none')) {
                    overWrite = $("#overwriteCheckbox").prop("checked");
                } else {
                    overWrite = false;
                }
                $rootScope.state.currentPipeline.send()
                    .success(function(data){
                        if (data.success) {
                            displaySuccess(data);
                            if ($rootScope.state.adjustingPipelineState && overWrite) {
                                var pipelineId = $rootScope.state.adjustingPipeline._id;

                                restApi.deleteOwnPipeline(pipelineId)
                                    .success(function(data){
                                        if (data.success) {
                                            $rootScope.state.adjustingPipelineState = false;
                                            $("#overwriteCheckbox").css("display", "none");
                                            refresh("Proa");
                                        } else {
                                            displayErrors(data);
                                        }
                                    })
                                    .error(function(data){
                                        toastRightTop("error", "Could not delete Pipeline")
                                        console.log(data);
                                    })

                            }
                            $scope.clearAssembly();

                        } else {
                            displayErrors(data);
                        }
                    })
                    .error(function(data){
                        toastRightTop("error", "Could not fulfill request", "Connection Error");
                        console.log(data);
                    });

            };



            function initRecs(pipeline, $element) {
                console.log("Recommending");
                restApi.recommendPipelineElement(pipeline)
                    .success(function (data) {
                        console.log(data);
                        if (data.success) {

                            $element.append($("<span><ul>").addClass("recommended-list"));
                            $("ul", $element)
                                .circleMenu({
                                    direction: "right-half",
                                    item_diameter: 50,
                                    circle_radius: 150,
                                    trigger: 'none'
                                });
                            $element.hover(showRecButton, hideRecButton);
                            populateRecommendedList($element, data.recommendedElements);
                            addRecommendedButton($element);

                        }else{
                            console.log(data);

                        }
                    })
                    .error(function(data){
                        console.log(data);
                    });
            }

            function getRecommendations(partialPipeline) {
                console.log("RECOMMENDING");

                return ;
            }


            function populateRecommendedList($element, recs) {

                var el;
                for (var i = 0; i < recs.length; i++) {

                    el = recs[i];
                    getElementByElementId(el.elementId)
                        .success(function(element){
                            console.log(element);
                            if (typeof element != "undefined") {

                                var recEl = new objectProvider.recElement(element);
                                $("<li>").addClass("recommended-item tt").append(recEl.getjQueryElement()).attr({
                                    "data-toggle": "tooltip",
                                    "data-placement": "top",
                                    "data-delay": '{"show": 100, "hide": 100}',
                                    title: recEl.name
                                }).appendTo($('ul', $element));
                                $('ul', $element).circleMenu('init');
                            } else {
                                console.log(i);
                            }
                        });


                }

                initTooltips();
            }

            function getElementByElementId(elId) {


                if (elId.indexOf("sepa") >= 0) { //Sepa
                    return restApi.getSepaById(elId)

                    //for (var i = 0, element; element = $rootScope.state.sepas[i]; i++) {
                    //    if (element.elementId === elId) {
                    //        return element;
                    //    }
                    //}
                } else {		//Action

                    return restApi.getActionById(elId);

                    //for (var i = 0, element; element = $rootScope.state.actions[i]; i++) {
                    //    if (element.elementId === elId) {
                    //        return element;
                    //    }
                    //}
                }
            }

            function createAndConnect(target) {
                console.log(target);
                var json = $("a", $(target)).data("recObject").json;
                var $parentElement = $(target).parents(".connectable");
                var x = $parentElement.position().left;
                var y = $parentElement.position().top;
                var coord = {'x': x + 200, 'y': y};
                var $target;
                if (json.elementId.indexOf("sepa") > 0) { //Sepa Element
                    $target = $scope.sepaDropped(createNewAssemblyElement(json, coord), true);
                }else{
                    $target = $scope.actionDropped(createNewAssemblyElement(json, coord),true);
                }

                var options;
                if ($parentElement.hasClass("stream")) {
                    options = apiConstants.streamEndpointOptions;
                } else {
                    options = apiConstants.sepaEndpointOptions;
                }
                var sourceEndPoint;
                if(jsPlumb.selectEndpoints({source : $parentElement}).length > 0){

                    if(!(jsPlumb.selectEndpoints({source : $parentElement}).get(0).isFull())){
                        sourceEndPoint = jsPlumb.selectEndpoints({source : $parentElement}).get(0)
                    }else{
                        sourceEndPoint = jsPlumb.addEndpoint($parentElement, options);
                    }
                }else{
                    sourceEndPoint = jsPlumb.addEndpoint($parentElement, options);
                }

                var targetEndPoint = jsPlumb.selectEndpoints({target: $target}).get(0);
                //console.log(targetEndPoint);

                jsPlumb.connect({source: sourceEndPoint, target: targetEndPoint, detachable: true});
                jsPlumb.repaintEverything();
            }

            $scope.prepareCustomizeModal = function(element) {
                $rootScope.state.currentElement = element;
                var string = "";
                // $('#savedOptions').children().not('strong').remove();
                // if (element.data("modal") == null) {
                textInputFields.length = 0;
                if (element.data("JSON").staticProperties != null && element.data("JSON").staticProperties != []) {
                    var staticPropertiesArray = element.data("JSON").staticProperties;

                    var textInputCount = 0;
                    var radioInputCount = 0;
                    var selectInputCount = 0;
                    var checkboxInputCount = 0;

                    for (var i = 0; i < staticPropertiesArray.length; i++) {
                        switch (staticPropertiesArray[i].input.properties.elementType) {
                            case "TEXT_INPUT":
                                var textInput = {};
                                if (staticPropertiesArray[i].input.properties.datatype != undefined)
                                {
                                    textInput.fieldName = "textinput" +i;
                                    textInput.propertyName = staticPropertiesArray[i].input.properties.datatype;
                                    textInputFields.push(textInput);
                                }
                                string += getTextInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].name, textInputCount, staticPropertiesArray[i].input.properties.value);
                                textInputCount++;
                                continue;
                            case "RADIO_INPUT":
                                string += getRadioInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].input.properties.options, radioInputCount);
                                radioInputCount++;
                                continue;
                            case "CHECKBOX":
                                string += getCheckboxInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].input.properties.options, i);
                                checkboxInputCount++;
                                continue;
                            case "SELECT_INPUT":
                                string += getSelectInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].input.properties.options, selectInputCount);
                                selectInputCount++;
                                continue;
                        }
                    }
                }

                return string;
            }

            /**
             * saves the parameters in the current element's data with key "options"
             */
            $scope.save = function() {

                var options = $('#modalForm').serializeArray();
                if (options.length < $rootScope.state.currentElement.data("JSON").staticProperties.length) {
                    toastRightTop("error", "Please enter all parameters");
                    return false;
                }
                for (var i = 0; i < options.length; i++) {
                    if (options[i].value == "") {
                        toastRightTop("error", "Please enter all parameters");
                        return false;
                    }
                }

                $rootScope.state.currentElement.data("options", true);
                saveInStaticProperties(options);
                $rootScope.state.currentElement.removeClass("disabled");
                // state.currentElement.css("opacity", 1);
            }


            function saveInStaticProperties(options) {
                for (var i = 0; i < options.length; i++) {
                    switch ($rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.elementType) {

                        case "RADIO_INPUT" :
                        case "SELECT_INPUT" :
                            for (var j = 0; j < $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options.length; j++) {
                                if ($rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].humanDescription == options[i].value) {
                                    $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = true;
                                } else {
                                    $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = false;
                                }
                            }
                            continue;
                        case "CHECKBOX" :
                            for (var j = 0; j < $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options.length; j++) {
                                if ($("#" + options[i].value + " #checkboxes-" + i + "-" + j).is(':checked')) {
                                    $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = true;
                                } else {
                                    $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = false;
                                }
                            }
                            continue;
                        case "TEXT_INPUT":
                            $rootScope.state.currentElement.data("JSON").staticProperties[i].input.properties.value = options[i].value;
                            continue;

                    }
                }
                toastRightTop("success", "Parameters saved");
            }


             $scope.clearCurrentElement = function() {
                $rootScope.state.currentElement = null;
            };

            function ContextMenuClickHandler(type) {

                if (type === "assembly") {
                    $('#assemblyContextMenu').off('click').on('click', function (e) {
                        $(this).hide();

                        var $invokedOn = $(this).data("invokedOn");
                        var $selected = $(e.target);
                        while ($invokedOn.parent().get(0) != $('#assembly').get(0)) {
                            $invokedOn = $invokedOn.parent();

                        }
                        if ($selected.get(0) === $('#blockButton').get(0)){
                            if ($invokedOn.hasClass("block")){

                                $scope.displayPipeline($.extend({},$invokedOn.data("block").pipeline));
                                handleDeleteOption($invokedOn);
                                //$invokedOn.remove();
                            }else{
                                $('#blockNameModal').modal('show');
                            }
                        }
                        else if ($selected.get(0) === $('#delete').get(0)) {

                            handleDeleteOption($invokedOn);

                        } else if ($selected.get(0) === $('#customize').get(0)) {//Customize clicked

                            $('#customize-content').html($scope.prepareCustomizeModal($invokedOn));
                            $('#customizeModal').modal('show');

                        } else {
                            handleJsonLDOption($invokedOn)
                        }
                    });
                } else if (type === "static") {
                    $('#staticContextMenu').off('click').on('click', function (e) {
                        $(this).hide();
                        var $invokedOn = $(this).data("invokedOn");
                        while ($invokedOn.parent().get(0) != $("#editor-icon-stand").get(0)) {
                                $invokedOn = $invokedOn.parent();
                        }
                        var json = $invokedOn.data("JSON");
                        $('#description-title').text(json.name);
                        $('#modal-description').text(json.description);
                        $('#descrModal').modal('show');
                    });
                }
            }

            function handleDeleteOption($element){
                jsPlumb.removeAllEndpoints($element);

                $element.remove();
            }

            function handleJsonLDOption($element) {
                var json = $element.data("JSON");
                $('#description-title').text(json.name);
                if (json.description) {$('#modal-description').text(json.description);}
                else {$('#modal-description').text("No description available");}
                $('#descrModal').modal('show');
                $rootScope.state.currentElement = $element;
                prepareJsonLDModal(json);
            }

            function addRecommendedButton($element) {
                $("<span>")
                    .addClass("recommended-button")
                    .click(function (e) {
                        e.stopPropagation();
                        var $recList = $("ul", $element);
                        $recList.circleMenu('open');
                    })
                    .appendTo($element);
            }

            function showRecButton(e) {
                $("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).show();
            }
            function hideRecButton(e) {
                $("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).hide();
            }


            function getCoordinates(ui) {

                var newPos = ui.helper.position();
                var newTop = getDropPosition(ui.helper);

                return {
                    'x': newPos.left,
                    'y': newTop
                };
            }

            function createNewAssemblyElement(json, coordinates) {


                var $newState = $('<span>')
                    .data("JSON", $.extend(true, {}, json))
                    .appendTo('#assembly');
                if (typeof json.DOM != "undefined") { //TODO TESTTEST
                    $newState.attr("id", json.DOM);
                    $newState.addClass('a'); //Flag so customize modal won't get triggered
                }

                jsPlumb.draggable($newState, {containment: 'parent'});

                $newState
                    .css({'position': 'absolute', 'top': coordinates.y, 'left': coordinates.x})
                    .on("contextmenu", function (e) {
                        if ($(this).hasClass('stream')) {
                            $('#customize, #division ').hide();

                        } else {
                            $('#customize, #division ').show();
                        }
                        if ($(this).hasClass('ui-selected') && isConnected(this)){
                            $('#blockButton').text("Create Block from Selected");
                            $('#blockButton, #division1 ').show();
                        } else {
                            $('#blockButton, #division1 ').hide();
                        }
                        $('#assemblyContextMenu')
                            .data("invokedOn", $(e.target))
                            .show()
                            .css({
                                position: "fixed",
                                left: getLeftLocation(e, "assembly"),
                                top: getTopLocation(e, "assembly")
                            });
                        ContextMenuClickHandler("assembly");
                        return false;
                    });


                if ($newState.data('JSON').iconUrl == null) { //Kein icon in JSON angegeben

                    addTextIconToElement($newState, $newState.data('JSON').name);

                } else {
                    $('<img>')
                        .attr({
                            src: json.iconUrl,
                            "data-toggle": "tooltip",
                            "data-placement": "top",
                            "data-delay": '{"show": 1000, "hide": 100}',
                            title: json.name
                        })
                        .error(function(){
                            $(".connectable-img", $newState).remove();
                            addTextIconToElement($newState, $newState.data("JSON").name);
                        })
                        .addClass('connectable-img tt')
                        .appendTo($newState)
                        .data("JSON", $.extend(true, {}, json));
                }

                return $newState;
            }

            function getNewTargetPoint(x, y) {
                return {
                    endpoint: "Rectangle",
                    paintStyle: {fillStyle: "grey"},
                    anchor: [x, y, -1, 0],
                    isTarget: true
                };
            }

            function modifyPipeline(pipelineModifications) {
                var id;

                for (var i = 0, modification; modification = pipelineModifications[i]; i++) {
                    id = "#" + modification.domId;
                    if ($(id) !== "undefined") {
                        $(id).data("JSON").staticProperties = modification.staticProperties;
                    }
                    //clearCurrentElement();
                }
            }
            function isConnected(element) {

                if (jsPlumb.getConnections({source: element}).length < 1 && jsPlumb.getConnections({target: element}).length < 1) {
                    return false;
                }
                return true;
            }

            function isFullyConnected(element) {
                return $(element).data("JSON").inputNodes == null || jsPlumb.getConnections({target: $(element)}).length == $(element).data("JSON").inputNodes;
            }

            function addAutoComplete(input, datatype) {
                console.log(input);
                $("#" +input).autocomplete({
                    source: function(request, response) {
                        $.ajax({
                            url: standardUrl +'autocomplete?propertyName=' +encodeURIComponent(datatype),
                            dataType: "json",
                            data: "term=" + request.term,
                            success: function (data) {
                                var suggestion = new Array();
                                $(data.result).each(function(index, value) {
                                    var item = {};
                                    item.label = value.label;
                                    item.value = value.value;
                                    suggestion.push(item);
                                });
                                response(suggestion);
                            }});
                    }
                });
            }

            //----------------------------------------------------
            // Block Methods
            //----------------------------------------------------

            $scope.blockElements = function(){
                var block = createBlock();
                if (block == false){
                    toastRightTop("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Block Creation Error");
                    return;
                }
                //sendToServer();
                var $selectedElements = $('.ui-selected');
                var blockCoords = getMidpointOfAssemblyElements($selectedElements);
                var $block = block.getjQueryElement()
                    .appendTo("#assembly")
                    .css({"top" : blockCoords.y, "left" : blockCoords.x, "position" : "absolute"});
                initTooltips();
                $('.block-name').flowtype({
                    minFont: 12,
                    maxFont: 25,
                    fontRatio: 10
                });
                jsPlumb.draggable($block, {containment: 'parent'});

                $block.on("contextmenu", function(e){
                    $('#customize, #division ').hide();
                    $('#blockButton, #division1 ').show();
                    $('#blockButton').text("Revert to Pipeline");
                    $('#assemblyContextMenu')
                        .data("invokedOn", $(e.target))
                        .show()
                        .css({
                            position: "absolute",
                            left: getLeftLocation(e, "assembly"),
                            top: getTopLocation(e, "assembly")
                        });
                    ContextMenuClickHandler("assembly");
                    return false;
                });

                //CLEANUP
                $selectedElements.each(function(i, element){
                    jsPlumb.remove(element);
                });
                //jsPlumb.remove($selectedElements);
                //$selectedElements.remove();
                //jsPlumb.deleteEveryEndpoint();
                jsPlumb.addEndpoint($block, apiConstants.sepaEndpointOptions);


            }

            function createBlock(){
                var blockData = $('#blockNameForm').serializeArray(); //TODO SAVE
                //console.log(blockData);

                var blockPipeline = new objectProvider.Pipeline();
                $('.ui-selected').each(function(){
                    var $el = $(this)
                    if ($el.hasClass("sepa") && $el.data("JSON").staticProperties != null && $el.data("options")) {
                        blockPipeline.addElement(this);
                    }else if ($el.hasClass("stream")){
                        blockPipeline.addElement(this);
                    }else{
                        return false;
                    }
                });
                //console.log(blockPipeline);
                var block = new objectProvider.Block(blockData[0].value, blockData[1].value, blockPipeline);


                return block;

            }

            function getMidpointOfAssemblyElements($elements){
                var maxLeft, minLeft, maxTop, minTop;

                $elements.each(function(i, element){
                    var offsetObject = $(element).position();
                    if (i == 0){
                        maxLeft = offsetObject.left;
                        minLeft = offsetObject.left;
                        maxTop = offsetObject.top;
                        minTop = offsetObject.top;
                    }
                    else {
                        minLeft = Math.min(minLeft, offsetObject.left);
                        maxLeft = Math.max(maxLeft, offsetObject.left);
                        minTop = Math.min(minTop, offsetObject.top);
                        maxTop = Math.max(maxTop, offsetObject.top);
                    }
                });
                var midLeft = (minLeft + maxLeft) / 2;
                var midTop = (minTop + maxTop) / 2;
                return {x: midLeft, y: midTop};
            }

            /**
             * Shows the contextmenu for given element
             * @param {Object} e
             */
            function staticContextMenu(e) {
                $('#staticContextMenu').data("invokedOn", $(e.target)).show().css({
                    position: "fixed",
                    left: getLeftLocation(e, "static"),
                    top: getTopLocation(e, "static")
                });
                ContextMenuClickHandler("static");
                return false;

            }


            /**
             * Gets the position of the dropped element insidy the assembly
             * @param {Object} helper
             */
            function getDropPosition(helper) {
                var helperPos = helper.position();
                var divPos = $('#assembly').position();
                var newTop = helperPos.top - divPos.top;
                return newTop;
            }

            /**
             *
             * @param {Object} e
             * @param {Object} type
             */
            function getLeftLocation(e, type) {
                if (type === "static") {
                    var menuWidth = $('#staticContextMenu').width();
                } else {
                    var menuWidth = $('#assemblyContextMenu').width();
                }
                var mainCoords = $('#main').position();
                console.log(mainCoords);
                var mouseWidth = e.pageX;
                var pageWidth = $(window).width();

                // opening menu would pass the side of the page
                if (mouseWidth + menuWidth > pageWidth && menuWidth < mouseWidth) {
                    return mouseWidth - menuWidth;
                }
                return mouseWidth;
            }

            function getTopLocation(e, type) {

                //var elCoordsOffset = $(e.currentTarget).position();
                //var testtop = elCoordsOffset.top + e.offsetY + $('#assembly').position().top;
                //return testtop;
                if (type === "static") {
                    var menuHeight = $('#staticContextMenu').height();
                } else {
                    var menuHeight = $('#assemblyContextMenu').height();
                }

                var mouseHeight = e.pageY;
                var pageHeight = $(window).height();

                if (mouseHeight + menuHeight > pageHeight && menuHeight < mouseHeight) {
                    return mouseHeight - menuHeight ;
                }
                return mouseHeight ;

            }

            $scope.openDescriptionModal = function(element){

                $('#description-title').text(element.name);
                $('#modal-description').text(element.description);
                $('#descrModal').modal('show');
            }


        }
    ])
    .directive('myDataBind', function($rootScope){
        return {
            restrict: 'A',
            link: function(scope, elem, attrs){
                elem.data("JSON", scope.element);
                elem.attr({'data-toggle' : "tooltip", 'data-placement': "top", 'title' : scope.element.name});
                elem.tooltip();
                if (scope.$last){
                    console.log("BROADCASTING");
                    $rootScope.$broadcast("elements.loaded");
                }
            }
        }
    })
    .service('objectProvider', function($http, restApi){
        var oP = this;


        this.Stream = function(element){

            var json = $(element).data("JSON");

            this.DOM = element.id;
            this.name = json.name;
            this.elementId = json.elementId;
            this.description = json.description;
            this.iconUrl = json.iconUrl;
            this.domains = json.domains;

        }
        this.Sepa = function(element){

            var json = $(element).data("JSON");

            this.DOM = element.id;
            this.name = json.name;
            this.elementId = json.elementId;
            this.description = json.description;
            this.iconUrl = json.iconUrl;
            this.connectedTo = [];
            this.domains = json.domains;
            this.staticProperties = json.staticProperties;

        }
        this.Action = function(element){
            var json = $(element).data("JSON");

            this.DOM = element.id;
            this.name = json.name;
            this.elementId = json.elementId;
            this.description = json.description;
            this.iconUrl = json.iconUrl;
            this.connectedTo = [];
            this.staticProperties = json.staticProperties;

        }

        this.Pipeline = function(){
            this.name = "";
            this.description = "";
            this.streams = [];
            this.sepas = [];
            this.action = {};
            this.addElement = function(element){

                var $element = $(element);
                var connections = jsPlumb.getConnections({
                    target : element
                });

                if ($element.hasClass('block')){
                    var block = $(element).data("block");
                    this.addBlock(block);
                }

                if ($element.hasClass('action')){

                    this.action = new oP.Action(element);

                    for (var i = 0; i < connections.length; i++) {
                        var conObjId = "#" + connections[i].sourceId;
                        var $conObj = $(conObjId);

                        if ($conObj.hasClass('block')){
                            var block = $conObj.data("block");
                            this.action.connectedTo.push(block.pipeline.sepas[block.outputIndex].DOM)
                            //this.addBlock($conObj.data("block"), this.action);
                        }else {
                            this.action.connectedTo.push(connections[i].sourceId);
                        }
                    }
                }else if ($element.hasClass('sepa')){
                    var el = new oP.Sepa(element);

                    el.staticProperties = $element.data("JSON").staticProperties;
                    el.connectedTo = [];
                    for (var i = 0; i < connections.length; i++) {
                        var conObjId = "#" + connections[i].sourceId;
                        var $conObj = $(conObjId);

                        if ($conObj.hasClass('block')){
                            var block = $conObj.data("block");
                            el.connectedTo.push(block.pipeline.sepas[block.outputIndex].DOM)
                            //this.addBlock($conObj.data("block"));
                        }else {
                            el.connectedTo.push(connections[i].sourceId);
                        }
                    }
                    this.sepas.push(el);

                } else if ($element.hasClass('stream')){
                    var el = new oP.Stream(element);
                    this.streams.push(el);
                }

            }
            this.addBlock = function(block){
                //connectedElement.connectedTo.push(block.pipeline.sepas[block.outputIndex].DOM)
                for (var i in block.pipeline.sepas){
                    this.sepas.push(block.pipeline.sepas[i]); //TODO evtl keine Referenz?
                }
                for (var i in block.pipeline.streams){
                    this.streams.push(block.pipeline.streams[i]);
                }
            }

            this.update = function(info, success){
                var pipeline = this;
                return restApi.updatePartialPipeline(pipeline);

            };

            this.send = function(){
                var pipeline = this;
                return restApi.storePipeline(pipeline);

            }

        };

        this.State = function(){
            this.adjustingPipelineState = false;
            this.plumbReady = false;
            this.sources = {};
            this.sepas = {};
            this.actions = {};
            this.currentElement = {};
            this.currentPipeline = new oP.Pipeline();
            this.adjustingPipeline = {};
        };

        this.recElement = function(json){
            console.log(json);
            this.json = json;
            this.name = json.name;
            this.getjQueryElement = function(){
                var element = this;
                return $('<a>')
                    .data("recObject", element)
                    .append($('<img>').attr("src", element.json.iconUrl).error(function(){
                        addTextIconToElement($(this).parent(), element.name, true);
                        $(this).remove();

                    }).addClass("recommended-item-img"));

            };
        }
        this.Block = function(name, description, pipeline){

            var oi;
            for (var i in pipeline.sepas){
                if (jsPlumb.getConnections({source :pipeline.sepas[i].DOM}).length == 0){
                    oi = i;
                    break;
                }
            }
            this.name = name;
            this.description = description;
            this.outputIndex = oi;
            this.pipeline = pipeline;
            this.getjQueryElement = function(){
                return $('<div>')
                    .data("block", $.extend({},this))
                    .addClass("block")
                    .append($('<div>').addClass("block-name tt").text(this.name)
                        .attr({
                            "data-toggle": "tooltip",
                            "data-placement": "top",
                            "data-delay": '{"show": 100, "hide": 100}',
                            title: this.description
                        })
                )
                    .append($('<div>').addClass("block-img-container")
                        .append($('<img>').addClass('block-img').attr("src", this.pipeline.streams[0].iconUrl)));
            }
        }
    })
    //.service('plumbService', function(){
    //
    //        this.editorPlumb = jsPlumb.getInstance({Container: "assembly"});
    //        this.pipelinePlumb = jsPlumb.getInstance({Container : "pipelineDisplay"});
    //
    //        this.refresh = function(){
    //            this.editorPlumb = jsPlumb.getInstance({Container: "assembly"});
    //            this.pipelinePlumb =  jsPlumb.getInstance({Container : "pipelineDisplay"});
    //        };
    //
    //})

    ;