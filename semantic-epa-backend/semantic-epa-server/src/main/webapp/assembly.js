

//  ============= 
//  = Variables = 
//  ============= 
var streamEndpointOptions = {
	endpoint: ["Dot", {radius: 5}],
	paintStyle: {fillStyle: "grey"},
	connectorStyle : {strokeStyle: "grey", lineWidth: 4},
	connector: "Straight",
	isSource: true,
	anchor: ["Perimeter", {shape: "Circle"}],
	connectorOverlays: [ 
	    [ "Arrow", { width:25, length:20, location:.5, id:"arrow" } ],   
  	]
};

var sepaEndpointOptions = {
	endpoint: ["Dot", {radius: 5}],
	paintStyle: {fillStyle: "grey"},
	connectorStyle : {strokeStyle: "grey", lineWidth: 4},
	connector: "Straight",
	isSource: true,
	anchor: "Right",
	connectorOverlays: [ 
	    [ "Arrow", { width:25, length:20, location:.5, id:"arrow" } ],   
  	]
};

var leftTargetPointOptions = {
	endpoint: "Rectangle",
	paintStyle: {fillStyle: "grey"},
	anchor: "Left",
	isTarget: true
};

var currentPipeline;
var mod;

/**
 * Handles everything that has to do with the assembly area, and elements in it
 */
function initAssembly(){
	
	$('#clear').click(clearAssembly); 
	
	

	$('#assembly').droppable({
		tolerance: "fit",
		drop: function(stream, ui) {
			if (ui.draggable.hasClass('draggable-icon')) {
				if(ui.draggable.data("JSON") == null){
					alert("No JSON - Data for Dropped element");
					return false;
				}
				//Neues Container Element für Icon / identicon erstellen
				var $newState = createNewAssemblyElement(ui);

				//Droppable Streams
				if(ui.draggable.hasClass('stream')){
					handleDroppedStream($newState);	
				//Droppable Sepas
				}else if(ui.draggable.hasClass('sepa')){
					handleDroppedSepa(ui, $newState);
				//Droppable Actions
				}else if(ui.draggable.hasClass('action')){
					handleDroppedAction($newState);
				}
				initTooltips();
			}
		}
		
	}); //End #assembly.droppable(
		
}

function createNewAssemblyElement(ui){
	console.log(ui.draggable);
	var $newState = $('<span>')									
		.data("JSON", $.extend(true, {},ui.draggable.data("JSON")))
		.appendTo('#assembly');
	jsPlumb.draggable($newState, {containment: 'parent'});
	var newPos = ui.helper.position();
	var newTop = getDropPosition(ui.helper);
	$newState
		.css({'position': 'absolute', 'top': newTop , 'left': newPos.left})
		.on("contextmenu", function (e) {
			if ($(this).hasClass('stream') || $(this).hasClass('action')){
				$('#customize, #division ').hide();
				
			}else{
				$('#customize, #division ').show();
			}
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
	if ($newState.data('JSON').iconUrl == null){ //Kein icon in JSON angegeben
		var md5 = ui.draggable.data("JSON").elementId.replace("-", "");
		var $ident = $('<p>')
			.text(md5)
			.appendTo($newState);
		$ident.identicon5({size:150});
		$ident.children().addClass("connectable-img tt")
		.attr(
			{"data-toggle": "tooltip",
			"data-placement": "top",
			"data-delay": '{"show": 1000, "hide": 100}',
			title: ui.draggable.data("JSON").name
			})
		.data("JSON", $.extend(true, {},ui.draggable.data("JSON")));
	}else{
		$('<img>')
			.addClass('connectable-img tt')
			.attr({src: ui.draggable.children().attr('src'),
				"data-toggle": "tooltip",
				"data-placement": "top",
				"data-delay": '{"show": 1000, "hide": 100}',
				title: ui.draggable.data("JSON").name})
			.appendTo($newState)
			
			.data("JSON", ui.draggable.data("JSON"));
	}
	
	return $newState;
}

function handleDroppedStream($newState){
	
	displaySepas();
	$('#sepas').children().show();
	$('#sepas').fadeTo(100,1);
	
	$newState
		.addClass('connectable stream');
		
	
	jsPlumb.addEndpoint($newState,streamEndpointOptions);
	
	$newState.dblclick(function(){
		jsPlumb.addEndpoint($newState,streamEndpointOptions);
	});
	
	
	if ($('#assembly').children().hasClass('sepa')){
		$('#actionCollapse').attr("data-toggle", "collapse");
		$('#actionCollapse').removeClass("disabled");
	}
	
}

function handleDroppedSepa(ui, $newState){
	
	$('#actions').children().remove();
	displayActions();
	$('#actions').children().show();
	$('#actions').fadeTo(100,1);									
	$newState
		.addClass('connectable sepa');
	if (ui.draggable.data("JSON").staticProperties != null){
		$newState
			.addClass('disabled');
	}
		
	if (ui.draggable.data("JSON").inputNodes < 2){ //1 InputNode
		
		jsPlumb.addEndpoint($newState, leftTargetPointOptions);	
	}else{ 
		jsPlumb.addEndpoint($newState,getNewTargetPoint(0, 0.25));
		
		jsPlumb.addEndpoint($newState,getNewTargetPoint(0, 0.75));
	}
	
	jsPlumb.addEndpoint($newState, sepaEndpointOptions);
	
	$newState.dblclick(function(){
		jsPlumb.addEndpoint($newState, sepaEndpointOptions);
	});
	
}

function handleDroppedAction($newState){
	
	jsPlumb.draggable($newState, {containment: 'parent'});
	
	$newState
		.addClass("connectable action");
		
	jsPlumb.addEndpoint($newState,leftTargetPointOptions);
	
}

function getNewTargetPoint(x, y){
	return {endpoint: "Rectangle",
			paintStyle: {fillStyle: "grey"},
			anchor: [x, y, -1, 0],
			isTarget: true};
	
}


function clearCurrentElement(){
	$currentElement = null;
}

/**
 * clears the Assembly of all elements 
 */
function clearAssembly() {
	$('#assembly').children().not('#clear, #submit').remove();
	jsPlumb.deleteEveryEndpoint();
	$('#sepas').children().hide();
	$('#sepas').fadeTo(300, 0);
	$('#actions').children().hide();
	$('#actions').fadeTo(300, 0);
	$('#sepaCollapse').attr("data-toggle", "");
	$('#sepaCollapse').addClass("disabled");
	$('#actionCollapse').attr("data-toggle", "");
	$('#actionCollapse').addClass("disabled");
	$('#collapseOne,#collapseTwo,#collapseThree').collapse('hide');
}

function createPartialPipeline(info){
	var pipelinePart = {};
	pipelinePart.streams = [];
	pipelinePart.sepas = [];
	pipelinePart.action ={};
	console.log(info);
	var finished = false;
	var element = info.target;
	var elementId = "#" + element;
	
	addElementToPartialPipeline(element, pipelinePart);
	
	// while (!finished){
		// addToPipeline(elementId, pipelinePart);
		// for (var i= 0; i < jsPlumb.getConnections({target : element}); i++){
			// if (jsPlumb.getConnections({target : element})[i].sourceId != null && jsPlumb.getConnections({target : element}).sourceId != undefined){
				// element = jsPlumb.getConnections({target : element}).sourceId;
				// elementId = "#" + element;
			// }else{
				// finished = true;
			// }
		// }
	// }
	
	// $('#assembly>span.connectable').each(function(i, element) {
		// if (isConnected(element)){
			// addToPipeline(element, pipelinePart);
		// }
	// });
	console.log(pipelinePart);
	currentPipeline = pipelinePart;
	
}

function addElementToPartialPipeline(element, pipelinePart){
	var elementId = "#" + element;
	addToPipeline(element, pipelinePart);
	if (jsPlumb.getConnections({target : element}) != null && jsPlumb.getConnections({target : element}) != undefined){
		for (var i = 0, con; con = jsPlumb.getConnections({target : element})[i]; i++){
			addElementToPartialPipeline(con.source, pipelinePart);
		}
	}
}




/**
 * Sends the pipeline to the server 
 */
function submit() {
	var error = false;
	var pipeline = {};
	var streams = [];
	var sepas = [];
	var action = {};
	var streamPresent = false;
	var sepaPresent = false;
	var actionPresent = false;
	
	pipeline.streams = streams;
	pipeline.sepas = sepas;
	pipeline.action = action;

	$('#assembly>span.connectable').each(function(i, element) {
		if (!isConnected(element)){
			toastTop("error", "All elements must be connected", "Submit Error");
		}
		
		if ($(element).hasClass('sepa')) {
			sepaPresent = true;
			if ($(element).data("options")) {
				addToPipeline(element, pipeline);

			} else if ($(element).data("JSON").staticProperties != null) {
				toastTop("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Submit Error");	;
				error = true;

			}
		} else if ($(element).hasClass('stream')) {
			streamPresent = true;
			addToPipeline(element, pipeline);

		} else if ($(element).hasClass('action')) {
			if (actionPresent){
				error = true;
				toastTop("error", "More than one action element present in pipeline", "Submit Error");
			}else{
				actionPresent = true;
				addToPipeline(element, pipeline);
			}
		}
	});
	if (!streamPresent){
		toastTop("error", "No stream element present in pipeline", "Submit Error");
		error = true;
	}
	if (!sepaPresent){
		toastTop("error", "No sepa element present in pipeline", "Submit Error");
		error = true;
	}
	if (!actionPresent){
		toastTop("error", "No action element present in pipeline", "Submit Error");
		error = true;
	}
	if (!error ) {
		
		currentPipeline = pipeline;
		openPipelineNameModal();
		
		
	}
}

function openPipelineNameModal(){
	
	$('#pipelineNameModal').modal('show');
}

function savePipelineName(){
	
	var pipelineName = $('#pipelineNameForm').serializeArray();
	if(pipelineName.length < 2){
		toastRightTop("error","Please enter all parameters");
		return false;
	}
	currentPipeline.name = pipelineName[0].value;
	currentPipeline.description = pipelineName[1].value;
	console.log(currentPipeline);
	sendPipeline(true);
}

function sendPipeline(fullPipeline, info){
	
	if(fullPipeline){
	
		// console.log(currentPipeline);
 		
 		$.ajax({
 			url : "http://localhost:8080/semantic-epa-backend/api/pipelines",
 			data : JSON.stringify(currentPipeline),
 			processData : false,
 			type : 'POST',
 			success : function(data){
 				if (data.success){			//TODO Objekt im Backend ändern
 					// toastTop("success", "Pipeline sent to server");
 					displaySuccess(data);
 				}else{
 					displayErrors(data);
 				}
 			},
 			error: function(data){
 				console.log(data);
 				toastTop("error", "Could not fulfill request", "Connection Error");
 			}
 		});
 		
 		
	 	
 	
 	}else{
 		
 		$.ajax({
 			url : "http://localhost:8080/semantic-epa-backend/api/pipelines/update",
 			data : JSON.stringify(currentPipeline),
 			processData : false,
 			type : 'POST',
 			success : function(data){
 				if (data.success){			//TODO Objekt im Backend ändern
 					modifyPipeline(data.pipelineModifications);
 					for (var i= 0, sepa; sepa = currentPipeline.sepas[i]; i++){
 						var id = "#" + sepa.DOM;
 						if ($(id).data("options") != true){
 							if (!isFullyConnected(id)) {return;}
 							$('#customize-content').html(prepareCustomizeModal($(id)));
 							var string = "Customize " + sepa.name;
 							$('#customizeTitle').text(string);
							$('#customizeModal').modal('show');
							
 						}
 					}
 				}else{
 					jsPlumb.detach(info.connection);
 					displayErrors(data);
 				}
 					
 			},
 			error: function(data){
 				console.log(data);
 				toastTop("error", "Could not fulfill request", "Connection Error");
 			} 
 		});
 	}
}




function modifyPipeline(pipelineModifications){
	var id;
	console.log(pipelineModifications);
	
	
	for (var i = 0, modification; modification = pipelineModifications[i]; i++){
		// console.log(modification);
		id = "#" + modification.domId;
		$currentElement = $(id);
		$currentElement.data("JSON").staticProperties = modification.staticProperties;
		// $currentElement.data("options", null);
		// $currentElement.data("modal", null);
		clearCurrentElement();
	}
	
}


/**
 * saves the parameters in the current element's data with key "options" 
 */
function save() {

	
	
	var options = $('#modalForm').serializeArray();
	console.log(options);
	if (options.length < $currentElement.data("JSON").staticProperties.length){
		toastRightTop("error","Please enter all parameters");
			return false;
	}
	for (var i = 0; i < options.length; i++){
		if (options[i].value == ""){
			toastRightTop("error","Please enter all parameters");
			return false;
		}
	}
	
	$currentElement.data("options", true);
	

	saveInStaticProperties(options);
	
	$currentElement.css("opacity", 1);
	

}

function saveInStaticProperties(options){
	for (var i = 0; i < options.length; i++){
		switch ($currentElement.data("JSON").staticProperties[i].input.properties.elementType){
			
			case "RADIO_INPUT" :
			case "SELECT_INPUT" :
				for (var j = 0; j < $currentElement.data("JSON").staticProperties[i].input.properties.options.length; j++){
					if ($currentElement.data("JSON").staticProperties[i].input.properties.options[j].humanDescription == options[i].value){
						$currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = true;
					}else{
						$currentElement.data("JSON").staticProperties[i].input.properties.options[j].selected = false;
					}
				}
				continue;
				
			case "TEXT_INPUT":
				
				$currentElement.data("JSON").staticProperties[i].input.properties.value = options[i].value;
				continue;
				
		}
	}
	toastRightTop("success", "Parameters saved");
}

function prepareCustomizeModal(element) {
	$currentElement = element;
	var string = "";
	// $('#savedOptions').children().not('strong').remove();
	// if (element.data("modal") == null) {

		if (element.data("JSON").staticProperties != null && element.data("JSON").staticProperties != []) {
			var staticPropertiesArray = element.data("JSON").staticProperties;

			var textInputCount = 0;
			var radioInputCount = 0;
			var selectInputCount = 0;

			for (var i = 0; i < staticPropertiesArray.length; i++) {
				switch (staticPropertiesArray[i].input.properties.elementType) {
				case "TEXT_INPUT":
					string += getTextInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].name, textInputCount, staticPropertiesArray[i].input.properties.value);
					textInputCount++;
					continue;
				case "RADIO_INPUT":
					string += getRadioInputForm(staticPropertiesArray[i].description, staticPropertiesArray[i].input.properties.options, radioInputCount);
					radioInputCount++;
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

function isConnected(element){
	
	if (jsPlumb.getConnections({source : element}).length < 1 && jsPlumb.getConnections({target : element}).length < 1){
		return false;
	}
	return true;
}

function isFullyConnected(element){
	return jsPlumb.getConnections({target : $(element)}).length == $(element).data("JSON").inputNodes ;
}

