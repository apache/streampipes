/**
 * Created by Cuddl3s on 13.08.2015.
 */
var editorControllers = angular.module('editorControllers', ['ngMaterial','ngMdIcons']);


editorControllers
    .controller('EditorCtrl', ['$scope', '$http','restApi',
        function ($scope, $http, restApi) {
            $scope.standardUrl = "http://localhost:8080/semantic-epa-backend/api/";
            $scope.isStreamInAssembly = false;
            $scope.isSepaInAssembly = false;
            $scope.currentElements = {};

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
                return restApi.getOwnSepas().length > 0;
            };

            $scope.ownActionsAvailable = function(){
                return restApi.getOwnActions().length > 0;
            };

            $scope.loadCurrentElements = function(type){
                if (type == 'block'){

                }else if (type == 'source'){
                    $scope.loadSources();
                }else if (type == 'sepa'){
                    $scope.loadSepas();
                }else if (type == 'action'){
                    $scope.loadActions();
                }
            };

            $scope.loadSources = function(){
                restApi.getOwnSources()
                    .success(function(sources){
                        $.each(sources, function(i, source){
                           //getStreams for Source
                            restApi.getOwnStreams(source)
                                .success(function(streams){
                                    $.each(streams, function(i, stream){
                                        $scope.currentElements.push(stream);
                                    });
                                })
                                .error(function(msg){
                                    console.log(msg);
                                });
                           createStreams(data);
                        });

                        $scope.currentElements = sources;
                        console.log($scope.currentElements);
                    });
            };
            $scope.loadSepas = function(){
                restApi.getOwnSepas()
                    .success(function(sepas){
                        $scope.currentElements = sepas;
                    });
            };
            $scope.loadActions = function(){
                restApi.getOwnActions()
                    .success(function(actions){
                        $scope.currentElements = actions;
                    });
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
                    disabled: !($scope.isStreamInAssembly) || !($scope.ownSepasAvailable())
                },
                {
                    title : 'Actions',
                    type: 'action',
                    disabled: !($scope.isSepaInAssembly) || !($scope.ownActionsAvailable())
                }



            ];

            init("Proa");

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

                initAssembly();

                $("#assembly")
                    .selectable({
                        selected: function (event, ui) {
                        },
                        filter: ".connectable.stream,.connectable.sepa:not('.disabled')",
                        delay: 150

                    });
                jsPlumb.Defaults.Container = "assembly";
                //Inititate accordion functionality-----------------
                //$('#accordion').on('show.bs.collapse', function () {
                //    $('#accordion').find('.in').collapse('hide');
                //});
                $('#collapseOne,#collapseTwo,#collapseThree').collapse({toggle: false});

                $(document).click(function () {
                    $('#assemblyContextMenu').hide();
                    $('#staticContextMenu').hide();
                    $('.circleMenu-open').circleMenu('close');
                });
                $('#sources').on('change', function () {
                    $(this)
                        .css("background-color", "#044")
                        .animate("200")
                        .css("background-color", "")
                        .animate("200");
                });

                //Bind click handler--------------------------------
                $("#pipelineTableBody").on("click", "tr", function () {
                    if (!$(this).data("active") || $(this).data("active") == undefined) {
                        $(this).data("active", true);
                        $(this).addClass("info");
                        $("#pipelineTableBody").children().not(this).removeClass("info");
                        $("#pipelineTableBody").children().not(this).data("active", false);
                        clearPipelineDisplay();
                        displayPipeline($(this).data("JSON"));
                    } else {

                    }
                });

                $('a[data-toggle="tab"]')
                    .on('hide.bs.tab', function (e) {
                        clearTab(e);
                    })
                    .on('show.bs.tab', function (e) {
                        toTab(e);
                    });

                $('#assembly').on('click',".recommended-item", function (e) {
                    console.log(e);
                    e.stopPropagation();
                    createAndConnect(this);
                })

            });


        }
    ])
    ;