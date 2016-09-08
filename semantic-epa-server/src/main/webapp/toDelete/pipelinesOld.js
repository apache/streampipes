var pipelines;

function refreshPipelines(){

	$("#pipelineTableBody").children().remove();
	var url = standardUrl + "pipelines";
	//var url = standardUrl + "user/streams";
	pipelines = $.getJSON(url, listPipelines);
	console.log(pipelines);
}


function verifyPipeline(pipelineId){
	var url = standardUrl + "pipelines/" + pipelineId + "/verify";
	$.ajax({
		url : url,
		success : function(data){
			console.log(data);
			changePipelineStatus(data, pipelineId);
		},
		type : 'GET',
		processData: false
	});
}

function changePipelineStatus(data, pipelineId) {
	//refreshPipelines();		//Cheap Way
}

function adjustSavedPipeline(json){
	console.log(json);
	state.adjustingPipelineState = true;
	clearPipelineDisplay();
	$("#tabs").find("a[href='#editor']").tab('show');
	showAdjustingPipelineState(json.name);
	state.adjustingPipeline = json;
	displayPipelineInAssembly(json);
}


function displayPipelineInAssembly(json){

	jsPlumb.setContainer($("#assembly"));

	var currentx = 50;
	var currenty = 50;
	for (var i = 0, stream; stream = json.streams[i]; i++){
		handleDroppedStream(createNewAssemblyElement(stream, {'x':currentx, 'y':currenty}));
		currenty += 200;
	}
	currenty = 50;
	for (var i = 0, sepa; sepa = json.sepas[i]; i++){
		currentx += 200;
		var $sepa = handleDroppedSepa(createNewAssemblyElement(sepa, {'x':currentx, 'y':currenty})
		.data("options", true));
		if (jsPlumb.getConnections({source :sepa.DOM}).length == 0){ //Output Element
			jsPlumb.addEndpoint($sepa, sepaEndpointOptions);
		}
	}
	currentx += 200;
	if (!$.isEmptyObject(json.action)) {
		handleDroppedAction(createNewAssemblyElement(json.action, {'x': currentx, 'y': currenty})
			.data("options", true));

	}

	
	connectPipelineElements(json, true);
	console.log(json);
	jsPlumb.repaintEverything();
	
}

function getYPosition(count , i, $canvas, $element){
	return ($canvas.height() / count)/2 - (1/2) * $element.height() + i * ($canvas.height() / count);
}
function getXPosition($canvas, $element){
	return ($canvas.width() / 2) - (1/2) * $element.width();
}
