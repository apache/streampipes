

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


/**
 * Handles everything that has to do with the assembly area, and elements in it
 */
function initAssembly(){
	
	$('#clear').click(clearAssembly); 

	$('#assembly').droppable({
			tolerance: "fit",
			drop: function(stream, ui) {
			
				if (ui.draggable.hasClass('draggable-icon') && !ui.draggable.hasClass('action')) {
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
				}
			}else if(ui.draggable.hasClass('draggable-icon') && ui.draggable.hasClass('action')){
				handleDroppedAction(ui, $newState);
			}
			initTooltips();
		}
		
	}); //End #assembly.droppable(
}

function createNewAssemblyElement(ui){
	var $newState = $('<span>')									
		.data("JSON", ui.draggable.data("JSON"))
		.appendTo('#assembly');
	jsPlumb.draggable($newState, {containment: 'parent'});
	var newPos = ui.helper.position();
	var newTop = getDropPosition(ui.helper);
	$newState
		.css({'position': 'absolute', 'top': newTop , 'left': newPos.left})
		.on("contextmenu", function (e) {
			if ($(this).hasClass('stream')){
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
			title: ui.draggable.data("JSON").name})
		.data("JSON", ui.draggable.data("JSON"));
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
		
	makeDraggable("sepa");
	jsPlumb.addEndpoint($newState,streamEndpointOptions);
	
	$newState.dblclick(function(){
		jsPlumb.addEndpoint($newState,streamEndpointOptions);
	});
	// $(function() { $newState.contextMenu('#cmenu'); });
	
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

function handleDroppedAction(ui, $newState){
	
	var $newState = $('<span>')									
		.appendTo('#assembly');
	jsPlumb.draggable($newState, {containment: 'parent'});
	var newPos = ui.helper.position();
	var newTop = getDropPosition(ui.helper);
	$newState
		.css({'position': 'absolute', 'top': newTop , 'left': newPos.left})
		.addClass("connectable action")
		.on("contextmenu", function (e) {
			$('#customize, #division ').hide();
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
		});;
	jsPlumb.addEndpoint($newState,leftTargetPointOptions);
	var $inner = $('<span>')
		.addClass("connectable-img")
		.appendTo($newState)
		.one('contextmenu', function(event) {
			
		});;
	if (ui.draggable.children().hasClass("glyphicon-hand-up")){
		$inner.addClass("glyphicon glyphicon-hand-up");
	}else{
		$inner.addClass("glyphicon glyphicon-list-alt");
	}
	
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

/**
 * Sends the pipeline to the server 
 */
function submit() {
	var error = false;
	var pipeline = {};
	var streams = [];
	var sepas = [];
	var streamPresent = false;
	var sepaPresent = false;
	var actionPresent = false;
	
	pipeline.streams = streams;
	pipeline.sepas = sepas;
	pipeline.action = "";

	$('#assembly>span.connectable').each(function(i, element) {
			if (!isConnected(element)){
				toastTop("error", "Nicht alle Elemente verbunden", "Submit Error");
			}
			
			if ($(element).hasClass('sepa')) {
				sepaPresent = true;
				if ($(element).data("options") != null || $(element).data("JSON").staticProperties == null) {
					var el = {};
					el.DOM = $(element).attr("id");
					el.JSON = $(element).data("JSON");
					el.connectedTo = [];
					for (var i = 0; i < jsPlumb.getConnections({
						target : element
					}).length; i++) {
						el.connectedTo.push($(jsPlumb.getConnections({target: element})[i].source).attr("id"));
					}
					if ($(element).data("options") != null){
						el.options = $(element).data("options");
					}
					pipeline.sepas.push(el);
	
				} else if ($(element).data("JSON").staticProperties != null) {
					toastTop("error", "Bitte für alle Elemente Parameter festlegen (Rechtsklick auf das Element -> Anpassen)", "Submit Error");	;
					error = true;
	
				}
			} else if ($(element).hasClass('stream')) {
				streamPresent = true;
				var el = {};
				
				el.DOM = $(element).attr("id");
				el.JSON = $(element).data("JSON");
				el.connectedTo = [];
				for (var i = 0; i < jsPlumb.getConnections({
					target : element
				}).length; i++) {
					el.connectedTo.push($(jsPlumb.getConnections({target: element})[i].source).attr("id"));
				}
				pipeline.streams.push(el);
	
			} else if ($(element).hasClass('action')) {
				if (actionPresent){
					error = true;
					toastTop("error", "Mehr als 1 Action Element in Pipeline", "Submit Error");
				}
				actionPresent = true;
				var el = {};
				el.DOM = $(element).attr("id");
				el.JSON = $(element).data("JSON");
				el.connectedTo = [];
				for (var i = 0; i < jsPlumb.getConnections({
					target : element
				}).length; i++) {
					el.connectedTo.push($(jsPlumb.getConnections({target: element})[i].source).attr("id"));
				}
				pipeline.action = el;
			}
	});
	if (!streamPresent){
		toastTop("error", "Kein Stream Element in Pipeline", "Submit Error");
		error = true;
	}
	if (!sepaPresent){
		toastTop("error", "Kein Sepa Element in Pipeline", "Submit Error");
		error = true;
	}
	if (!actionPresent){
		toastTop("error", "Kein Action Element in Pipeline", "Submit Error");
		error = true;
	}
	if (!error ) {
		
		console.log(pipeline);
		toastTop("success", "Pipeline wurde gesendet");
		$.post("http://anemone06.fzi.de/semantic-epa-backend/api/pipelines", JSON.stringify(pipeline));
	}
}
/**
 * saves the parameters in the current element's data with key "options" 
 */
function save() {

	console.log($('#modalForm').serializeArray());
	var options = $('#modalForm').serializeArray();
	if (options.length < $currentElement.data("JSON").staticProperties.length){
		toastr.error("Bitte alle Parameter angeben");
			return false;
	}
	for (var i = 0; i < options.length; i++){
		if (options[i].value == ""){
			// toastTop("error", "Bitte alle Parameter angeben");
			toastr.error("Bitte alle Parameter angeben");
			return false;
		}
	}
	
	$currentElement.data("options", options);
	$('#savedOptions').children().not('strong').remove();
	for (var i = 0; i < $currentElement.data("options").length; i++) {
		$('<div>').text($currentElement.data("options")[i].name + ": " + $currentElement.data("options")[i].value).appendTo('#savedOptions');
	}

	if ($currentElement.data("options") != null) {
		toastr.success("Parameter gespeichert!");
		$currentElement.css("opacity", 1);
		$('#customizeModal').modal('hide');
	} else {
		toastTop("warning","Oooops, irgendetwas ist schief gelaufen...");
	}

}

function prepareCustomizeModal(element) {
	$currentElement = element;
	var string = "";
	$('#savedOptions').children().not('strong').remove();
	if (element.data("modal") == null) {

		if (element.data("JSON").staticProperties != null) {
			var staticPropertiesArray = element.data("JSON").staticProperties;

			var textInputCount = 0;
			var radioInputCount = 0;
			var selectInputCount = 0;

			for (var i = 0; i < staticPropertiesArray.length; i++) {
				switch (staticPropertiesArray[i].input.elementType) {
				case "TEXT_INPUT":
					string += getTextInputForm(staticPropertiesArray[i].name, staticPropertiesArray[i].name, textInputCount);
					textInputCount++;
					continue;
				case "RADIO_INPUT":
					string += getRadioInputForm(staticPropertiesArray[i].name, staticPropertiesArray[i].input.options, radioInputCount);
					radioInputCount++;
					continue;
				case "SELECT_INPUT":
					string += getSelectInputForm(staticPropertiesArray[i].name, staticPropertiesArray[i].input.options, selectInputCount);
					selectInputCount++;
					continue;

				}
			}
		}
		element.data("modal", string);

	} else {
		string = element.data("modal");
		if (element.data("options") != null) {

			for (var i = 0; i < element.data("options").length; i++) {
				$('<div>').text(element.data("options")[i].name + ": " + element.data("options")[i].value).appendTo('#savedOptions');
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

