/**
 * Created by Cuddl3s on 13.08.2015.
 */
angular.module('streamPipesApp')
    .controller('EditorCtrl', ['$scope', '$rootScope', '$timeout', '$http','restApi',
        function ($scope, $rootScope,$timeout, $http, restApi) {
            $scope.standardUrl = "http://localhost:8080/semantic-epa-backend/api/";
            $scope.isStreamInAssembly = false;
            $scope.isSepaInAssembly = false;
            $scope.isActionInAssembly = false;
            $scope.currentElements = [];


            
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
                $('#editor-icon-stand').children().remove();        //DOM ACCESS
                if (type == 'block'){

                }else if (type == 'source'){
                    $scope.loadSources();
                }else if (type == 'sepa'){
                    $scope.loadSepas();
                }else if (type == 'action'){
                    $scope.loadActions();
                }
            };

            $scope.tabs = [

                {
                    title : 'Blocks',
                    type: 'block',
                    disabled: !($scope.ownBlocksAvailable())
                },
                {
                    title : 'Sources',
                    type: 'source',
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
                return restApi.getOwnSources()
                    .success(function(sources){

                        $.each(sources, function(i, source){
                            restApi.getOwnStreams(source)
                                .success(function(streams){
                                    //console.log(streams);
                                    angular.forEach(streams, function(stream, i){
                                        for (var j = 0; j < $scope.currentElements.length; j++){
                                            if (stream.elementId == $scope.currentElements[j].elementId){
                                                return;
                                            }
                                        }
                                        stream.type = 'stream';
                                        $scope.currentElements.push(stream);

                                    });
                                    //$scope.createElements(streams, "stream", "#editor-icon-stand");
                                    console.log($scope.currentElements);
                                    $timeout(function(){
                                        makeDraggable();
                                    })
                                })
                                .error(function(msg){
                                    console.log(msg);
                                })
                        });
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
                            makeDraggable();
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
                            makeDraggable();
                        })

                    });
            };

            var makeDraggable = function(){
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
                    jsPlumb.addEndpoint($newElement, streamEndpointOptions);
                }
            };
            $scope.sepaDropped = function($newElement, endpoints){
                $scope.isSepaInAssembly = true;
                $newElement.addClass("connectable sepa");

                if ($newElement.data("JSON").staticProperties != null && !state.adjustingPipelineState && !$newElement.data("options")) {
                    $newElement
                        .addClass('disabled');
                }


                if (endpoints) {
                    if ($newElement.data("JSON").inputNodes < 2) { //1 InputNode

                        jsPlumb.addEndpoint($newElement, leftTargetPointOptions);
                    } else {
                        jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.25));

                        jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.75));
                    }
                    jsPlumb.addEndpoint($newElement, sepaEndpointOptions);
                }
            };
            $scope.actionDropped = function($newElement, endpoints){
                $scope.isActionInAssembly = true;

                $newElement
                    .addClass("connectable action");

                if ($newElement.data("JSON").staticProperties != null && !state.adjustingPipelineState) {
                    $newElement
                        .addClass('disabled');
                }
                if (endpoints) {
                    jsPlumb.addEndpoint($newElement, leftTargetPointOptions);
                }
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




            init("Proa");

            //TODO ANGULARIZE
            //Initiate assembly and jsPlumb functionality-------
            jsPlumb.ready(function (e) {
                console.log("READY");
                state.plumbReady = true;
                jsPlumb.bind("connection", function (info, originalEvent) {
                    var $target = $(info.target);
                    if (!$target.hasClass('a')){ //class 'a' = do not show customize modal //TODO class a zuweisen
                        createPartialPipeline(info);
                        $.when(
                            state.currentPipeline.update(info)
                        ).then(function(data){
                                if (data.success) {
                                    if ($target.hasClass('sepa')) {
                                        initRecs(state.currentPipeline, $target);
                                    }
                                }
                            });

                    }
                });

                window.onresize = function (event) {
                    jsPlumb.repaintEverything(true);
                };

                (function initAssembly() {

                    $('#clear').click(clearAssembly);

                    $('#assembly').droppable({
                        tolerance: "fit",
                        drop: function (element, ui) {

                            if (ui.draggable.hasClass('draggable-icon')) {
                                //TODO get data
                                console.log(ui);



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

                                    var tempPipeline = new Pipeline();
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
                })();

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

                jsPlumb.Defaults.Container = "assembly";

                $(document).click(function () {
                    $('#assemblyContextMenu').hide();
                    $('#staticContextMenu').hide();
                    $('.circleMenu-open').circleMenu('close');
                });

                //Bind click handler--------------------------------
                //$("#pipelineTableBody").on("click", "tr", function () {
                //    if (!$(this).data("active") || $(this).data("active") == undefined) {
                //        $(this).data("active", true);
                //        $(this).addClass("info");
                //        $("#pipelineTableBody").children().not(this).removeClass("info");
                //        $("#pipelineTableBody").children().not(this).data("active", false);
                //        clearPipelineDisplay();
                //        displayPipeline($(this).data("JSON"));
                //    } else {
                //
                //    }
                //});
                //
                //$('a[data-toggle="tab"]')
                //    .on('hide.bs.tab', function (e) {
                //        clearTab(e);
                //    })
                //    .on('show.bs.tab', function (e) {
                //        toTab(e);
                //    });



            });


        }
    ])
    .directive('myDataBind', function(){
        return {
            restrict: 'A',
            link: function(scope, elem, attrs){
                elem.data("JSON", scope.element)
            }
        }
    })

    ;