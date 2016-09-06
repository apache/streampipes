EditorCtrl.$inject = ['$scope', '$rootScope', '$timeout', '$http', 'restApi', '$stateParams', 'objectProvider', 'apiConstants', '$q', '$mdDialog', '$document', '$compile', 'imageChecker'];

export default function EditorCtrl($scope, $rootScope, $timeout, $http, restApi, $stateParams, objectProvider, apiConstants, $q, $mdDialog, $window, $compile, imageChecker) {
	$scope.standardUrl = "http://localhost:8080/semantic-epa-backend/api/";
	$scope.isStreamInAssembly = false;
	$scope.isSepaInAssembly = false;
	$scope.isActionInAssembly = false;
	$scope.currentElements = [];
	$scope.currentModifiedPipeline = $stateParams.pipeline;
	$scope.possibleElements = [];
	$scope.activePossibleElementFilter = {};
	$scope.selectedTab = 1;
	$rootScope.title = "StreamPipes";
	$scope.options = [];
	$scope.selectedOptions = [];

	$scope.currentPipelineName = "";
	$scope.currentPipelineDescription = "";

	$scope.minimizedEditorStand = false;

	$scope.selectMode = true;

	//var editorPlumb;
	var textInputFields = [];
	var connCount = 1;

	$scope.currentZoomLevel = 1;

	$scope.toggleEditorStand = function () {
		$scope.minimizedEditorStand = !$scope.minimizedEditorStand;
	}

	$("#assembly").panzoom({
		disablePan: true,
		increment: 0.25,
		minScale: 0.5,
		maxScale: 1.5,
		contain: 'invert'
	});

	$("#assembly").on('panzoomzoom', function (e, panzoom, scale) {
		$scope.currentZoomLevel = scale;
		jsPlumb.setZoom(scale);
		jsPlumb.repaintEverything();
	});

	$scope.toggleSelectMode = function () {
		if ($scope.selectMode) {
			$("#assembly").panzoom("option", "disablePan", false);
			$("#assembly").selectable("disable");
			$scope.selectMode = false;
		}
		else {
			$("#assembly").panzoom("option", "disablePan", true);
			$("#assembly").selectable("enable");
			$scope.selectMode = true;
		}
	}

	$scope.zoomOut = function () {
		doZoom(true);
	}

	$scope.zoomIn = function () {
		doZoom(false);
	}

	var doZoom = function (zoomOut) {
		$("#assembly").panzoom("zoom", zoomOut);
	}

	$scope.possibleFilter = function (value, index, array) {
		if ($scope.possibleElements.length > 0) {
			for (var i = 0; i < $scope.possibleElements.length; i++) {
				if (value.belongsTo === $scope.possibleElements[i].elementId) {
					return true;
				}
			}
			return false;
		}
		return true;
	};

	$scope.selectFilter = function (value, index, array) {
		if ($scope.selectedOptions.length > 0) {
			var found = false;
			if (value.category.length == 0) value.category[0] = "UNCATEGORIZED";
			angular.forEach(value.category, function (c) {
				if ($scope.selectedOptions.indexOf(c) > -1) found = true;
			});
			return found;
		} else {
			return false;
		}
	};

	$scope.toggleFilter = function (option) {
		$scope.selectedOptions = [];
		$scope.selectedOptions.push(option.type);
	}

	$scope.optionSelected = function (option) {
		return $scope.selectedOptions.indexOf(option.type) > -1;
	}

	$scope.selectAllOptions = function () {
		$scope.selectedOptions = [];
		angular.forEach($scope.options, function (o) {
			$scope.selectedOptions.push(o.type);
		});
	}

	$scope.deselectAllOptions = function () {
		$scope.selectedOptions = [];
	}

	$scope.showImageIf = function (iconUrl) {
		return !!(iconUrl != null && iconUrl != 'http://localhost:8080/img' && iconUrl !== 'undefined');
	};

	$scope.showSavePipelineDialog = function (elementData, sepaName) {
		$rootScope.state.currentElement = elementData;
		$mdDialog.show({
			controller: SavePipelineController,
			templateUrl: 'modules/editor/templates/submitPipelineModal.tmpl.html',
			parent: angular.element(document.body),
			clickOutsideToClose: true,
			scope: $scope,
			rootScope: $rootScope,
			preserveScope: true,

		})
	}

	$scope.showMatchingErrorDialog = function (elementData) {
		$mdDialog.show({
			controller: MatchingErrorController,
			templateUrl: 'modules/editor/templates/matchingErrorDialog.tmpl.html',
			parent: angular.element(document.body),
			clickOutsideToClose: true,
			scope: $scope,
			rootScope: $rootScope,
			preserveScope: true,
			locals: {
				elementData: elementData,
			}
		})
	}

	$scope.showCustomizeDialog = function (elementData, sepaName, sourceEndpoint) {
		$rootScope.state.currentElement = elementData;
		$mdDialog.show({
			controller: CustomizeController,
			templateUrl: 'modules/editor/templates/customizeElementDialog.tmpl.html',
			parent: angular.element(document.body),
			clickOutsideToClose: true,
			scope: $scope,
			rootScope: $rootScope,
			preserveScope: true,
			locals: {
				elementData: elementData,
				sepaName: sepaName,
				sourceEndpoint: sourceEndpoint
			}
		})
	};

	$scope.showClearAssemblyConfirmDialog = function (ev) {
		var confirm = $mdDialog.confirm()
			.title('Clear assembly area?')
			.textContent('All pipeline elements in the assembly area will be removed.')
			.targetEvent(ev)
			.ok('Clear assembly')
			.cancel('Cancel');
		$mdDialog.show(confirm).then(function () {
			$scope.clearAssembly();
		}, function () {

		});
	};

	angular.element($window).on('scroll', function () {
		jsPlumb.repaintEverything(true);
	});

	$scope.$on("SepaElementConfigured", function (event, item) {
		initRecs($rootScope.state.currentPipeline, item);
	});


	$scope.$on('$destroy', function () {
		jsPlumb.deleteEveryEndpoint();
	});

	$scope.$on('$viewContentLoaded', function (event) {
		jsPlumb.setContainer("assembly");

		initAssembly();
		initPlumb();
	});
	$rootScope.$on("elements.loaded", function () {
		makeDraggable();
		bindContextMenu();
		//initTooltips();
	});
	$scope.openContextMenu = function ($mdOpenMenu, event) {
		$mdOpenMenu(event.$event);
		alert("open context menu");
	};

	$scope.getOwnBlocks = function () {
		return restApi.getBlocks();           //TODO anpassen
	};

	$scope.ownBlocksAvailable = function () {
		return true; //TODO
	};

	$scope.ownSourcesAvailable = function () {
		return true; //TODO
	};

	$scope.ownSepasAvailable = function () {
		return true; //TODO
	};

	$scope.ownActionsAvailable = function () {
		return true; //TODO
	};

	$scope.loadCurrentElements = function (type) {

		$scope.currentElements = [];
		//$('#editor-icon-stand').children().remove();        //DOM ACCESS
		if (type == 'block') {
			$scope.loadOptions("block");
			$scope.loadBlocks();
		} else if (type == 'stream') {
			$scope.loadOptions("stream");
			$scope.loadSources();
		} else if (type == 'sepa') {
			$scope.loadOptions("sepa");
			$scope.loadSepas();
		} else if (type == 'action') {
			$scope.loadOptions("action");
			$scope.loadActions();
		}
	};

	$scope.displayPipelineById = function () {
		restApi.getPipelineById($scope.currentModifiedPipeline)
			.success(function (pipeline) {
				$scope.displayPipeline(pipeline);

			})
			.error(function (msg) {
				console.log(msg);
			});

	};

	$scope.displayPipeline = function (pipeline) {
		var currentx = 50;
		var currenty = 50;
		for (var i = 0, stream; stream = pipeline.streams[i]; i++) {
			$scope.streamDropped(createNewAssemblyElement(stream, {'x': currentx, 'y': currenty}));
			currenty += 200;
		}
		currenty = 50;
		for (var i = 0, sepa; sepa = pipeline.sepas[i]; i++) {
			currentx += 200;
			var $sepa = $scope.sepaDropped(createNewAssemblyElement(sepa, {'x': currentx, 'y': currenty})
				.data("options", true));
			if (jsPlumb.getConnections({source: sepa.DOM}).length == 0) { //Output Element
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

		$scope.currentPipelineName = pipeline.name;
		$scope.currentPipelineDescription = pipeline.description;
	};

	function bindContextMenu() {
		$(".draggable-icon").off("contextmenu").on("contextmenu", staticContextMenu);
	}

	function connectPipelineElements(json, detachable) {
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
		for (var i = 0, sepa; sepa = json.sepas[i]; i++) {
			for (var j = 0, connection; connection = sepa.connectedTo[j]; j++) {

				source = connection;
				target = sepa.DOM;


				var options;
				var id = "#" + source;
				if ($(id).hasClass("sepa")) {
					options = apiConstants.sepaEndpointOptions;
				} else {
					options = apiConstants.streamEndpointOptions;
				}

				sourceEndpoint = jsPlumb.addEndpoint(source, options);
				targetEndpoint = jsPlumb.addEndpoint(target, apiConstants.leftTargetPointOptions);
				jsPlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable: detachable});
			}
		}
		jsPlumb.setSuspendDrawing(false, true);
	}

	$scope.tabs = [

		{
			title: 'Blocks',
			type: 'block',
			disabled: !($scope.ownBlocksAvailable())
		},
		{
			title: 'Data Streams',
			type: 'stream',
			disabled: !($scope.ownSourcesAvailable())
		},
		{
			title: 'Processing Elements',
			type: 'sepa',
			disabled: !($scope.ownSepasAvailable())
		},
		{
			title: 'Data Sinks',
			type: 'action',
			disabled: !($scope.ownActionsAvailable())
		}
	];

	$scope.loadOptions = function (type) {
		$scope.options = [];
		$scope.selectedOptions = [];

		if (type == 'stream') {
			restApi.getEpCategories()
				.then(function (result) {
					$scope.options = result.data;
					angular.forEach($scope.options, function (o) {
						$scope.selectedOptions.push(o.type);
					});
				}, function (error) {
					$scope.options = [];
					console.log(error);
				});
		} else if (type == 'sepa') {
			restApi.getEpaCategories()
				.then(function (result) {
					$scope.options = result.data;
					angular.forEach($scope.options, function (o) {
						$scope.selectedOptions.push(o.type);
					});
				}, function (error) {
					$scope.options = [];
					console.log(error);
				});
		} else if (type == 'action') {
			restApi.getEcCategories()
				.then(function (result) {
					$scope.options = result.data;
					angular.forEach($scope.options, function (o) {
						$scope.selectedOptions.push(o.type);
					});
				}, function (error) {
					$scope.options = [];
					console.log(error);
				});
		}

	};

	$scope.loadBlocks = function () {
		restApi.getBlocks().then(function (data) {
			data.data.forEach(function (block, i, blocks) {
				block.type = "block";
			});
			$scope.currentElements = data.data;
		});
	};

	$scope.loadSources = function () {
		var tempStreams = [];
		var promises = [];
		restApi.getOwnSources()
			.then(function (sources) {
				sources.data.forEach(function (source, i, sources) {
					//promises.push(restApi.getOwnStreams(source));
					source.eventStreams.forEach(function (stream) {
						stream.type = 'stream';
						tempStreams = tempStreams.concat(stream);
					});
					$scope.currentElements = tempStreams;
				});
			}, function (msg) {
				console.log(msg);
			});
	};


	$scope.loadSepas = function () {
		restApi.getOwnSepas()
			.success(function (sepas) {
				$.each(sepas, function (i, sepa) {
					sepa.type = 'sepa';
				});
				$scope.currentElements = sepas;
				$timeout(function () {
					//makeDraggable();
					$rootScope.state.sepas = $.extend(true, [], $scope.currentElements);
				})

			})
			.error(function (msg) {
				console.log(msg);
			});
	};
	$scope.loadActions = function () {
		restApi.getOwnActions()
			.success(function (actions) {
				$.each(actions, function (i, action) {
					action.type = 'action';
				});
				$scope.currentElements = actions;
				$timeout(function () {
					//makeDraggable();
					$rootScope.state.actions = $.extend(true, [], $scope.currentElements);
				})

			});
	};

	var makeDraggable = function () {
		$('.draggable-icon').draggable({
			revert: 'invalid',
			helper: 'clone',
			stack: '.draggable-icon',
			start: function (el, ui) {
				ui.helper.appendTo('#content');
				$('#outerAssemblyArea').css('border', '3px dashed rgb(255,64,129)');
			},
			stop: function (el, ui) {
				$('#outerAssemblyArea').css('border', '1px solid rgb(63,81,181)');
			}
		});
		$('.block').draggable({
			revert: 'invalid',
			helper: 'clone',
			stack: '.block',
			start: function (el, ui) {
				ui.helper.appendTo('#content');
				$('#assemblyArea').css('border-color', '3px dashed rgb(255,64,129)');
			},
			stop: function (el, ui) {
				$('#assemblyArea').css('border', '1px solid rgb(63,81,181)');
			}
		});
	};

	$scope.blockDropped = function ($newElement, endpoints) {
		$scope.isStreamInAssembly = true;
		$scope.isSepaInAssembly = true;
		var data = $.extend({}, $newElement.data("JSON"));
		$newElement
			.addClass("connectable-block")
			.data("block", data)
			.append($('<div>').addClass("block-name tt").text(data.name)
			.attr({
				"data-toggle": "tooltip",
				"data-placement": "top",
				"data-delay": '{"show": 100, "hide": 100}',
				title: data.description
			})
			)
			.append($('<div>').addClass("block-img-container")
			.append($('<img>').addClass('block-img').attr("src", data.streams[0].iconUrl)));

		if (endpoints) {
			jsPlumb.addEndpoint($newElement, apiConstants.sepaEndpointOptions);
		}


	};

	$scope.streamDropped = function ($newElement, endpoints) {

		$newElement.append($('<span>').addClass("help-button").append($compile("<md-icon md-svg-icon='action:ic_help_24px'>")($scope).addClass("green")).click(function (e) {

			//toggleStyle(e);
			//togglePossibleElements(e, $newElement);
		}));
		$newElement.append($('<span>').addClass("possible-button").append($compile("<md-icon md-svg-icon='action:ic_visibility_24px'>")($scope).addClass("green")).click(function (e) {

			//toggleStyle(e);
			togglePossibleElements(e, $newElement);
		}));
		$scope.isStreamInAssembly = true;
		$newElement.addClass("connectable stream");

		if (endpoints) {
			jsPlumb.addEndpoint($newElement, apiConstants.streamEndpointOptions);
		}
		return $newElement;
	};

	$scope.sepaDropped = function ($newElement, endpoints) {
		$newElement.append($('<span>').addClass("possible-button").append($compile("<md-icon md-svg-icon='action:ic_visibility_24px'>")($scope).addClass("green")).click(function (e) {
			//toggleStyle(e);
			togglePossibleElements(e, $newElement);
		}));
		$scope.isSepaInAssembly = true;
		$newElement.addClass("connectable sepa");

		if ($newElement.data("JSON").staticProperties != null && !$rootScope.state.adjustingPipelineState && !$newElement.data("options")) {
			$newElement
				.addClass('disabled');
		}


		if (endpoints) {
			if ($newElement.data("JSON").inputStreams.length < 2) { //1 InputNode
				//jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
				jsPlumb.addEndpoint($newElement, apiConstants.leftTargetPointOptions);
			} else {
				jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.25));

				jsPlumb.addEndpoint($newElement, getNewTargetPoint(0, 0.75));
			}
			jsPlumb.addEndpoint($newElement, apiConstants.sepaEndpointOptions);
		}
		return $newElement;
	};
	$scope.actionDropped = function ($newElement, endpoints) {
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

	$scope.elementTextIcon = function (string) {
		var result = "";
		if (string.length <= 4) {
			result = string;
		} else {
			var words = string.split(" ");
			words.forEach(function (word, i) {
				if (word.charAt(0) != '(' && word.charAt(0) != ')') {
					result += word.charAt(0);
				}
			});
		}
		return string;
		//return result.toUpperCase();
	}

	//TODO ANGULARIZE
	//Initiate assembly and jsPlumb functionality-------
	function initPlumb() {

		$rootScope.state.plumbReady = true;

		jsPlumb.registerEndpointTypes({
			"empty": {
				paintStyle: {
					fillStyle: "white",
					strokeStyle: "#9E9E9E",
					lineWidth: 2
				}
			},
			"token": {
				paintStyle: {
					fillStyle: "#BDBDBD",
					strokeStyle: "#9E9E9E",
					lineWidth: 2
				},
				hoverPaintStyle: {
					fillStyle: "#BDBDBD",
					strokeStyle: "#4CAF50",
					lineWidth: 4
				}
			},
			"highlight": {
				paintStyle: {
					fillStyle: "white",
					strokeStyle: "#4CAF50",
					lineWidth: 4
				}
			}
		});

		jsPlumb.unbind("connection");

		jsPlumb.bind("connectionDetached", function (info, originalEvent) {
			info.targetEndpoint.setType("empty");
		});

		jsPlumb.bind("connectionDrag", function (connection) {
			jsPlumb.selectEndpoints().each(function (endpoint) {
				if (endpoint.isTarget && endpoint.connections.length == 0) {
					endpoint.setType("highlight");
				}
			});

		});
		jsPlumb.bind("connectionAborted", function (connection) {
			jsPlumb.selectEndpoints().each(function (endpoint) {
				if (endpoint.isTarget && endpoint.connections.length == 0) {
					endpoint.setType("empty");
				}
			});
		})

		jsPlumb.bind("connection", function (info, originalEvent) {

			var $target = $(info.target);

			if (!$target.hasClass('a')) { //class 'a' = do not show customize modal //TODO class a zuweisen
				createPartialPipeline(info);
				$rootScope.state.currentPipeline.update()
					.success(function (data) {
						if (data.success) {
							info.targetEndpoint.setType("token");
							modifyPipeline(data.pipelineModifications);
							for (var i = 0, sepa; sepa = $rootScope.state.currentPipeline.sepas[i]; i++) {
								var id = "#" + sepa.DOM;
								if ($(id).length > 0) {
									if ($(id).data("options") != true) {
										if (!isFullyConnected(id)) {
											return;
										}
										var sourceEndpoint = jsPlumb.selectEndpoints({element: info.targetEndpoint.elementId});
										$scope.showCustomizeDialog($(id), sepa.name, sourceEndpoint);
									}
								}
							}
							if (!$.isEmptyObject($rootScope.state.currentPipeline.action)) {
								var id = "#" + $rootScope.state.currentPipeline.action.DOM;
								if (!isFullyConnected(id)) {
									return;
								}
								$scope.showCustomizeDialog($(id), $rootScope.state.currentPipeline.action.name);
							}
						} else {
							jsPlumb.detach(info.connection);
							$scope.showMatchingErrorDialog(data);
						}
					})
					.error(function (data) {
						console.log(data);
					});
			}
		});


		window.onresize = function (event) {
			jsPlumb.repaintEverything(true);
		};
	}

	function initAssembly() {
		$('#assembly').droppable({
			tolerance: "fit",
			drop: function (element, ui) {

				if (ui.draggable.hasClass('draggable-icon') || ui.draggable.hasClass('block')) {
					//TODO get data
					//console.log(ui);

					if (ui.draggable.data("JSON") == null) {
						alert("No JSON - Data for Dropped element");
						return false;
					}
					var $newState;
					//Neues Container Element f�r Icon / identicon erstellen
					if (ui.draggable.hasClass("block")) {
						$newState = createNewAssemblyElement(ui.draggable.data("JSON"), getCoordinates(ui), true);
					} else {
						$newState = createNewAssemblyElement(ui.draggable.data("JSON"), getCoordinates(ui), false);
					}

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
					} else if (ui.draggable.hasClass('block')) {
						$scope.blockDropped($newState, true)
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
			.on('click', ".recommended-item", function (e) {
				e.stopPropagation();
				createAndConnect(this);
			});


		$(document).click(function () {
			$('#assemblyContextMenu').hide();
			$('#staticContextMenu').hide();
			$('.circleMenu-open').circleMenu('close');
		});

		if (typeof $scope.currentModifiedPipeline != 'undefined') {
			$rootScope.state.adjustingPipelineState = true;
			$scope.displayPipelineById();
		}

	};


	/**
	 * clears the Assembly of all elements
	 */
	$scope.clearAssembly = function () {
		togglePossibleElements(null, null);
		$('#assembly').children().not('#clear, #submit').remove();
		jsPlumb.deleteEveryEndpoint();
		$rootScope.state.adjustingPipelineState = false;
		$("#assembly").panzoom("reset", {
			disablePan: true,
			increment: 0.25,
			minScale: 0.5,
			maxScale: 1.5,
			contain: 'invert'
		});
		$scope.currentZoomLevel = 1;
		jsPlumb.setZoom($scope.currentZoomLevel);
		jsPlumb.repaintEverything();
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
	$scope.submit = function () {
		var error = false;
		var pipelineNew = new objectProvider.Pipeline();
		var streamPresent = false;
		var sepaPresent = false;
		var actionPresent = false;


		$('#assembly').find('.connectable, .connectable-block').each(function (i, element) {
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
			} else if ($element.hasClass('connectable-block')) {
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
			if ($rootScope.state.adjustingPipelineState) {
				$rootScope.state.currentPipeline.name = $scope.currentPipelineName;
				$rootScope.state.currentPipeline.description = $scope.currentPipelineDescription;
			}

			openPipelineNameModal();


		}
	}

	function openPipelineNameModal() {
		if ($rootScope.state.adjustingPipelineState) {
			$scope.modifyPipelineMode = true;
		}
		$scope.showSavePipelineDialog();
	}

	function initRecs(pipeline, $element) {
		restApi.recommendPipelineElement(pipeline)
			.success(function (data) {
				if (data.success) {
					$(".recommended-list", $element).remove();
					$element.append($("<span><ul>").addClass("recommended-list"));
					$("ul", $element)
						.circleMenu({
							direction: "right-half",
							item_diameter: 50,
							circle_radius: 150,
							trigger: 'none'
						});
					$element.hover(showRecButton, hideRecButton); //TODO alle Buttons anzeigen/verstecken
					populateRecommendedList($element, data.recommendedElements);
					var hasElements = false;
					if (data.recommendedElements.length > 0) {
						hasElements = true;
					}
					addRecommendedButton($element, hasElements);

				} else {
					console.log(data);

				}
				$element.data("possibleElements", data.possibleElements);
			})
			.error(function (data) {
				console.log(data);
			});
	}

	function togglePossibleElements(event, el) {

		if (event != null && el != null) {
			if (!$.isEmptyObject($scope.activePossibleElementFilter)) { //Filter Aktiv
				if ($scope.activePossibleElementFilter == event.currentTarget) { //Auf aktiven Filter geklickt
					$scope.possibleElements = [];
					$scope.activePossibleElementFilter = {};
					$("md-icon", event.currentTarget).remove();
					$(event.currentTarget).append($compile("<md-icon md-svg-icon='action:ic_visibility_24px'>")($scope).addClass("green"));
					//$scope.$apply();
					//altes SVG adden

				} else { //Auf anderen Filter geklickt
					$("md-icon", event.currentTarget).remove();
					$(event.currentTarget).append($compile("<md-icon md-svg-icon='action:ic_visibility_off_24px'>")($scope));

					$("md-icon", $scope.activePossibleElementFilter).remove();
					$($scope.activePossibleElementFilter).append($compile("<md-icon md-svg-icon='action:ic_visibility_24px'>")($scope).addClass("green"));
					if (el.data("possibleElements") !== 'undefined') {
						$scope.possibleElements = el.data("possibleElements");
						$scope.activePossibleElementFilter = event.currentTarget;
						if (el.hasClass("stream")) {
							$scope.selectedTab = 2;
						} else if (el.hasClass("sepa")) {
							$scope.selectedTab = 2;
						}
						//$scope.$apply();
					}
				}
			} else { //KEIN FILTER AKTIV
				$scope.activePossibleElementFilter = event.currentTarget;
				if (el.data("possibleElements") !== 'undefined') {
					$("md-icon", event.currentTarget).remove();
					$(event.currentTarget).append($compile("<md-icon md-svg-icon='action:ic_visibility_off_24px'>")($scope));
					$scope.possibleElements = el.data("possibleElements");
					if (el.hasClass("stream")) {
						$scope.selectedTab = 2;
					} else if (el.hasClass("sepa")) {
						$scope.selectedTab = 2;
					}
					//$scope.$apply();
				}
			}
		} else {
			if (!$.isEmptyObject($scope.activePossibleElementFilter) && $scope.activePossibleElementFilter != null && $scope.activePossibleElementFilter !== 'undefined') {
				$("md-icon", $scope.activePossibleElementFilter).remove();
				$($scope.activePossibleElementFilter).append($compile("<md-icon md-svg-icon='action:ic_visibility_24px'>")($scope).addClass("green"));
			}
			$scope.possibleElements = [];
			$scope.activePossibleElementFilter = {};

		}

		$scope.$apply();
	}


	function populateRecommendedList($element, recs) {

		var el;
		for (var i = 0; i < recs.length; i++) {

			el = recs[i];
			getElementByElementId(el.elementId)
				.success(function (element) {
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

		} else {		//Action
			return restApi.getActionById(elId);
		}
	}

	function createAndConnect(target) {
		var json = $("a", $(target)).data("recObject").json;
		var $parentElement = $(target).parents(".connectable");
		var x = $parentElement.position().left;
		var y = $parentElement.position().top;
		var coord = {'x': x + 200, 'y': y};
		var $target;
		if (json.belongsTo.indexOf("sepa") > 0) { //Sepa Element
			$target = $scope.sepaDropped(createNewAssemblyElement(json, coord), true);
		} else {
			$target = $scope.actionDropped(createNewAssemblyElement(json, coord), true);
		}

		var options;
		if ($parentElement.hasClass("stream")) {
			options = apiConstants.streamEndpointOptions;
		} else {
			options = apiConstants.sepaEndpointOptions;
		}
		var sourceEndPoint;
		if (jsPlumb.selectEndpoints({source: $parentElement}).length > 0) {

			if (!(jsPlumb.selectEndpoints({source: $parentElement}).get(0).isFull())) {
				sourceEndPoint = jsPlumb.selectEndpoints({source: $parentElement}).get(0)
			} else {
				sourceEndPoint = jsPlumb.addEndpoint($parentElement, options);
			}
		} else {
			sourceEndPoint = jsPlumb.addEndpoint($parentElement, options);
		}

		var targetEndPoint = jsPlumb.selectEndpoints({target: $target}).get(0);
		//console.log(targetEndPoint);

		jsPlumb.connect({source: sourceEndPoint, target: targetEndPoint, detachable: true});
		jsPlumb.repaintEverything();
	}

	$scope.clearCurrentElement = function () {
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
				if ($selected.get(0) === $('#blockButton').get(0)) {
					if ($invokedOn.hasClass("connectable-block")) {

						$scope.displayPipeline($.extend({}, $invokedOn.data("block")));
						handleDeleteOption($invokedOn);
						//$invokedOn.remove();
					} else {
						$('#blockNameModal').modal('show');
					}
				}
				else if ($selected.get(0) === $('#delete').get(0)) {

					handleDeleteOption($invokedOn);

				} else if ($selected.get(0) === $('#customize').get(0)) {//Customize clicked
					$scope.showCustomizeDialog($invokedOn);

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

	function handleDeleteOption($element) {
		jsPlumb.removeAllEndpoints($element);

		$element.remove();
	}

	function handleJsonLDOption($element) {
		var json = $element.data("JSON");
		$('#description-title').text(json.name);
		if (json.description) {
			$('#modal-description').text(json.description);
		}
		else {
			$('#modal-description').text("No description available");
		}
		$('#descrModal').modal('show');
		$rootScope.state.currentElement = $element;
		prepareJsonLDModal(json);
	}

	function addRecommendedButton($element, hasElements) {
		var classString = "";
		if (hasElements) {
			classString = "green";
		} else {
			classString = "red";
		}
		$("<span>")
			.addClass("recommended-button")
			.click(function (e) {
				e.stopPropagation();
				var $recList = $("ul", $element);
				$recList.circleMenu('open');
			})
			.append($compile("<md-icon md-svg-icon='content:ic_add_circle_24px'>")($scope).addClass("hover-icon").addClass(classString)
			.attr("aria-label", "Recommended Elements"))
			.appendTo($element);
	}

	function showRecButton(e) {
		$("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).show();
	}

	function hideRecButton(e) {
		$("span:not(.recommended-list,.recommended-item,.element-text-icon,.element-text-icon-small)", this).hide();
	}


	function getCoordinates(ui) {

		var newLeft = getDropPositionX(ui.helper);
		var newTop = getDropPositionY(ui.helper);
		return {
			'x': newLeft,
			'y': newTop
		};
	}

	function createNewAssemblyElement(json, coordinates, block) {

		togglePossibleElements(null, null);

		var $newState = $('<span>')
			.data("JSON", $.extend(true, {}, json))
			.appendTo('#assembly');
		if (typeof json.DOM !== "undefined") { //TODO TESTTEST
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

				if ($(this).hasClass('ui-selected') && isConnected(this)) {
					$('#blockButton').text("Create Block from Selected");
					$('#blockButton, #division1 ').show();
				} else {
					$('#blockButton, #division1 ').hide();
				}
				if ($(this).hasClass("connectable-block")) {
					$('#customize, #division ').hide();
					$('#blockButton, #division1 ').show();
					$('#blockButton').text("Revert to Pipeline");
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

		if (!block) {
			$scope.addImageOrTextIcon($newState, json, false, 'connectable');
		}

		return $newState;
	}

	function getNewTargetPoint(x, y) {
		return {
			endpoint: ["Dot", {radius: 12}],
			type: "empty",
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

				$(id).data("JSON").outputStrategies = modification.outputStrategies;

			}
		}
	}

	function isConnected(element) {

		if (jsPlumb.getConnections({source: element}).length < 1 && jsPlumb.getConnections({target: element}).length < 1) {
			return false;
		}
		return true;
	}

	function isFullyConnected(element) {
		return $(element).data("JSON").inputStreams == null || jsPlumb.getConnections({target: $(element)}).length == $(element).data("JSON").inputStreams.length;
	}

	function addAutoComplete(input, datatype) {
		$("#" + input).autocomplete({
			source: function (request, response) {
				$.ajax({
					url: standardUrl + 'autocomplete?propertyName=' + encodeURIComponent(datatype),
					dataType: "json",
					data: "term=" + request.term,
					success: function (data) {
						var suggestion = new Array();
						$(data.result).each(function (index, value) {
							var item = {};
							item.label = value.label;
							item.value = value.value;
							suggestion.push(item);
						});
						response(suggestion);
					}
				});
			}
		});
	}

	//----------------------------------------------------
	// Block Methods
	//----------------------------------------------------

	$scope.blockElements = function () {
		var blockData = createBlock();
		var block = blockData[0];
		if (block == false) {
			toastRightTop("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Block Creation Error");
			return;
		}

		if (blockData.length == 2 && blockData[1] === "on") {
			restApi.saveBlock(block)
				.then(function (successData) {
					console.log(successData);
				}, function (errorData) {
					console.log(errorData);
				});
		}

		//sendToServer();
		var $selectedElements = $('.ui-selected');
		var blockCoords = getMidpointOfAssemblyElements($selectedElements);
		var $block = block.getjQueryElement()
			.appendTo("#assembly")
			.css({"top": blockCoords.y, "left": blockCoords.x, "position": "absolute"});
		initTooltips();
		$('.block-name').flowtype({
			minFont: 12,
			maxFont: 25,
			fontRatio: 10
		});
		jsPlumb.draggable($block, {containment: 'parent'});

		$block.on("contextmenu", function (e) {
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
		$selectedElements.each(function (i, element) {
			jsPlumb.remove(element);
		});
		//jsPlumb.remove($selectedElements);
		//$selectedElements.remove();
		//jsPlumb.deleteEveryEndpoint();
		jsPlumb.addEndpoint($block, apiConstants.sepaEndpointOptions);


	}

	function createBlock() {
		var blockData = $('#blockNameForm').serializeArray(); //TODO SAVE
		var blockPipeline = new objectProvider.Pipeline();
		$('.ui-selected').each(function () {
			var $el = $(this)
			if ($el.hasClass("sepa") && $el.data("JSON").staticProperties != null && $el.data("options")) {
				blockPipeline.addElement(this);
			} else if ($el.hasClass("stream")) {
				blockPipeline.addElement(this);
			} else {
				return false;
			}
		});
		//console.log(blockPipeline);
		var block = new objectProvider.Block(blockData[0].value, blockData[1].value, blockPipeline);
		var data;
		if (blockData.length = 3) {
			data = [block, blockData[2].value];
		} else {
			data = [block];
		}

		return data;

	}

	function getMidpointOfAssemblyElements($elements) {
		var maxLeft, minLeft, maxTop, minTop;

		$elements.each(function (i, element) {
			var offsetObject = $(element).position();
			if (i == 0) {
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
	function getDropPositionY(helper) {
		var newTop;
		var helperPos = helper.offset();
		var divPos = $('#assembly').offset();
		newTop = (helperPos.top - divPos.top) + (1 - $scope.currentZoomLevel) * ((helperPos.top - divPos.top) * 2);
		return newTop;
	}

	function getDropPositionX(helper) {
		var newLeft;
		var helperPos = helper.offset();
		var divPos = $('#assembly').offset();
		newLeft = (helperPos.left - divPos.left) + (1 - $scope.currentZoomLevel) * ((helperPos.left - divPos.left) * 2);
		return newLeft;
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
		var mouseWidth = e.pageX;
		var pageWidth = $(window).width();

		// opening menu would pass the side of the page
		if (mouseWidth + menuWidth > pageWidth && menuWidth < mouseWidth) {
			return mouseWidth - menuWidth;
		}
		return mouseWidth;
	}

	function getTopLocation(e, type) {

		if (type === "static") {
			var menuHeight = $('#staticContextMenu').height();
		} else {
			var menuHeight = $('#assemblyContextMenu').height();
		}

		var mouseHeight = e.pageY - $(window).scrollTop();
		var pageHeight = $(window).height();

		if (mouseHeight + menuHeight > pageHeight && menuHeight < mouseHeight) {
			return mouseHeight - menuHeight;
		}

		return mouseHeight;

	}

	$scope.openDescriptionModal = function (element) {

		$('#description-title').text(element.name);
		$('#modal-description').text(element.description);
		$('#descrModal').modal('show');
	};

	$scope.addImageOrTextIcon = function ($element, json, small, type) {
		var iconUrl = "";
		if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
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
				if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
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


				};
				function SavePipelineController($scope, $rootScope, $mdDialog, $state, restApi) {

					$scope.pipelineCategories = [];

					$scope.getPipelineCategories = function () {
						restApi.getPipelineCategories()
							.success(function (pipelineCategories) {
								$scope.pipelineCategories = pipelineCategories;
							})
							.error(function (msg) {
								console.log(msg);
							});

					};
					$scope.getPipelineCategories();

					$scope.savePipelineName = function (switchTab) {

						if ($rootScope.state.currentPipeline.name == "") {
							toastRightTop("error", "Please enter a name for your pipeline");
							return false;
						}

						var overWrite;

						if (!($("#overwriteCheckbox").css('display') == 'none')) {
							overWrite = $("#overwriteCheckbox").prop("checked");
						} else {
							overWrite = false;
						}
						$rootScope.state.currentPipeline.send()
							.success(function (data) {
								if (data.success) {
									displaySuccess(data);
									$scope.hide();
									if (switchTab) $state.go("streampipes.pipelines");
									if ($scope.startPipelineAfterStorage) $state.go("streampipes.pipelines", {pipeline: data.notifications[1].description});
									if ($rootScope.state.adjustingPipelineState && overWrite) {
										var pipelineId = $rootScope.state.adjustingPipeline._id;

										restApi.deleteOwnPipeline(pipelineId)
											.success(function (data) {
												if (data.success) {
													$rootScope.state.adjustingPipelineState = false;
													$("#overwriteCheckbox").css("display", "none");
													refresh("Proa");
												} else {
													displayErrors(data);
												}
											})
											.error(function (data) {
												toastRightTop("error", "Could not delete Pipeline");
												console.log(data);
											})

									}
									$scope.clearAssembly();

								} else {
									displayErrors(data);
								}
							})
							.error(function (data) {
								toastRightTop("error", "Could not fulfill request", "Connection Error");
								console.log(data);
							});

					};

					$scope.hide = function () {
						$mdDialog.hide();
					};
				}

				function MatchingErrorController($scope, $rootScope, $mdDialog, elementData) {
					$scope.msg = elementData;
					console.log(elementData);
					$scope.hide = function () {
						$mdDialog.hide();
					};

					$scope.cancel = function () {
						$mdDialog.cancel();
					};
				}

				function CustomizeController($scope, $rootScope, $mdDialog, elementData, sepaName, sourceEndpoint, restApi) {

					$scope.selectedElement = elementData.data("JSON");
					$scope.selection = [];
					$scope.matchingSelectionLeft = [];
					$scope.matchingSelectionRight = [];
					$scope.sepaName = sepaName;
					$scope.invalid = false;
					$scope.helpDialogVisible = false;
					$scope.currentStaticProperty;
					$scope.validationErrors = [];

					$scope.primitiveClasses = [{"id": "http://www.w3.org/2001/XMLSchema#string"},
						{"id": "http://www.w3.org/2001/XMLSchema#boolean"},
						{"id": "http://www.w3.org/2001/XMLSchema#integer"},
						{"id": "http://www.w3.org/2001/XMLSchema#long"},
						{"id": "http://www.w3.org/2001/XMLSchema#double"}];

				$scope.toggleHelpDialog = function () {
					$scope.helpDialogVisible = !$scope.helpDialogVisible;
				}

				$scope.setCurrentStaticProperty = function (staticProperty) {
					$scope.currentStaticProperty = staticProperty;
				}

				$scope.getStaticPropertyInfo = function () {
					var info = "";
					if (currentStaticProperty.type == 'MAPPING_PROPERTY')
						info += "This field is a mapping property. It requires you to select one or more specific data elements from a stream.<b>"
					info += "This field requires the following specifc input: <b>";
					return info;
				}

				$scope.hide = function () {
					$mdDialog.hide();
				};

				$scope.cancel = function () {
					$mdDialog.cancel();
				};

				$scope.setSelectValue = function (c, q) {
					console.log(q);
					angular.forEach(q, function (item) {
						item.selected = false;
					});

					c.selected = true;
				};

				/**
				 * saves the parameters in the current element's data with key "options"
				 */
				$scope.saveProperties = function () {

					angular.forEach($scope.selectedElement.staticProperties, function (item) {
						if (item.properties.staticPropertyType === 'OneOfStaticProperty') {
							console.log(item);
							angular.forEach(item.properties.options, function (option) {
								if (item.properties.currentSelection) {
									if (option.elementId == item.properties.currentSelection.elementId) {
										option.selected = true;
									}
								}
							}
							);
						}
					}
					)
					;

					if ($scope.validate()) {
						$rootScope.state.currentElement.data("options", true);
						$rootScope.state.currentElement.data("JSON").staticProperties = $scope.selectedElement.staticProperties;
						$rootScope.state.currentElement.removeClass("disabled");
						$rootScope.$broadcast("SepaElementConfigured", elementData);
						$scope.hide();
						if (sourceEndpoint) sourceEndpoint.setType("token");
					}
					else $scope.invalid = true;
				}

				$scope.validate = function () {
					$scope.validationErrors = [];
					var valid = true;

					angular.forEach($scope.selectedElement.staticProperties, function (staticProperty) {
						if (staticProperty.properties.staticPropertyType === 'OneOfStaticProperty' ||
							staticProperty.properties.staticPropertyType === 'AnyStaticProperty') {
							var anyOccurrence = false;
							angular.forEach(staticProperty.properties.options, function (option) {
								if (option.selected) anyOccurrence = true;
							});
							if (!anyOccurrence) valid = false;
						} else if (staticProperty.properties.staticPropertyType === 'FreeTextStaticProperty') {
							if (!staticProperty.properties.value) {
								valid = false;
							}
							if (staticProperty.properties.requiredDatatype) {
								if (!$scope.typeCheck(staticProperty.properties.value, staticProperty.properties.requiredDatatype)) {
									valid = false;
									$scope.validationErrors.push(staticProperty.properties.label + " must be of type " + staticProperty.properties.requiredDatatype);
								}
							}
						} else if (staticProperty.properties.staticPropertyType === 'MappingPropertyUnary') {
							if (!staticProperty.properties.mapsTo) {
								valid = false;
							}

						} else if (staticProperty.properties.staticPropertyType === 'MappingPropertyNary') {
							if (!staticProperty.properties.mapsTo ||
								!staticProperty.properties.mapsTo.length > 0) {
								valid = false;
							}
						}
					});

					angular.forEach($scope.selectedElement.outputStrategies, function (strategy) {
						if (strategy.type == 'de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy') {
							if (!strategy.properties.eventProperties && !strategy.properties.eventProperties.length > 0) {
								valid = false;
							}
						}
						// TODO add replace output strategy
						// TODO add support for replace output strategy
					});

					return valid;
				}

				$scope.typeCheck = function (property, datatype) {
					if (datatype == $scope.primitiveClasses[0].id) return true;
					if (datatype == $scope.primitiveClasses[1].id) return (property == 'true' || property == 'false');
					if (datatype == $scope.primitiveClasses[2].id) return (!isNaN(property) && parseInt(Number(property)) == property && !isNaN(parseInt(property, 10)));
					if (datatype == $scope.primitiveClasses[3].id) return (!isNaN(property) && parseInt(Number(property)) == property && !isNaN(parseInt(property, 10)));
					if (datatype == $scope.primitiveClasses[4].id) return !isNaN(property);
					return false;
				}

				}
