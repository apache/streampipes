var pipelines;

function refreshPipelines(){

	$("#pipelineTableBody").children().remove();
	var url = standardUrl + "pipelines";
	pipelines = $.getJSON(url, listPipelines);
	
}

function listPipelines(data){
	$.each(data, function(i, json){
		pipelineTableRowFactory(i, json);
	});
}

function pipelineTableRowFactory(i, json){
	var id = "pipeline" + i;
	var $row = $("<tr>")
		.attr("id", id)
		.data("JSON", json);
	createPipelineTableRowData(i, $row);
	$row.appendTo("#pipelineTableBody");
}

function createPipelineTableRowData(i, $row){
	var status = "TESTSTATUS";
	$("<td>").text(i).appendTo($row); // #
	$("<td>").text($row.data("JSON").name).appendTo($row); // Name
	$("<td>").text("OK").append(getGlyphIconButton("glyphicon glyphicon-refresh", function(e){e.stopPropagation(); verifyPipeline(getParentWithJSONData($(this)).data("JSON")._id);})).appendTo($row); //VALIDATION STATUS
	
	if ($row.data("JSON").running) status = "Running";
	else status = "Idle";
	$("<td>").text(status).appendTo($row); 
	
	if ($row.data("JSON").running)
		{
			$("<td>").append($("<div class='btn-group'>") //Options
				.append(getGlyphIconButton("glyphicon glyphicon-play", false, function(e){e.stopPropagation(); startPipeline(getParentWithJSONData($(this)).data("JSON")._id);})).append(getGlyphIconButton("glyphicon glyphicon-stop", true, function(e){e.stopPropagation(); stopPipeline(getParentWithJSONData($(this)).data("JSON")._id);}))).appendTo($row);
		}
	else
		{
		$("<td>").append($("<div class='btn-group'>") //Options
				.append(getGlyphIconButton("glyphicon glyphicon-play", true, function(e){e.stopPropagation(); startPipeline(getParentWithJSONData($(this)).data("JSON")._id);})).append(getGlyphIconButton("glyphicon glyphicon-stop", false, function(e){e.stopPropagation(); stopPipeline(getParentWithJSONData($(this)).data("JSON")._id);}))).appendTo($row);

		}
		$("<td>").append(getGlyphIconButton("glyphicon glyphicon-remove", true, function(e){e.stopPropagation(); deletePipeline(getParentWithJSONData($(this)).data("JSON")._id, getParentWithJSONData($(this)));})).appendTo($row); //Delete
	$("<td>").append(getGlyphIconButton("glyphicon glyphicon-wrench", true, function(e){e.stopPropagation(); adjustPipeline(getParentWithJSONData($(this)).data("JSON"));})).appendTo($row); //Modify
	
	
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

function changePipelineStatus(data, pipelineId){
	refreshPipelines();		//Cheap Way
	//$("#pipelineTableBody").children().each(function(i){
	//	if ($(this).data("JSON")._id == pipelineId){
	//		if (data.notifications[0].title == "Started"){
	//			//Running
	//			//Disable Play Button, enable stop button
	//		}else if (data.notifications[0].title == "Stopped"){
	//			//Idle
	//			//Disable Stop Button, enable play button
	//		}
	//	}
    //
	//});
}

function startPipeline(pipelineId){
	console.log("Starting Pipeline");
	var url = standardUrl + "pipelines/" + pipelineId + "/start";
	$.ajax({
		url : url,
		success : function(data){
			console.log(data);
			if (data.success){
				displaySuccess(data);
				changePipelineStatus(data, pipelineId);
			}else{
				displayErrors(data);
			}
		},
		error : function(data){
			console.log(data);
		},
		type : 'GET',
		processData: false
	});
}


function deletePipeline(pipelineId, $row){
	console.log("Deleting Pipeline");
	var url = standardUrl + "pipelines/" + pipelineId;
	$.ajax({
		url : url,
		success : function(data){
			if (data.success){
				displaySuccess(data);
				$row.remove();
			}else{
				displayErrors(data);
			}
		},
		error : function(data){
			console.log(data);
		},
		type : 'DELETE',
		processData: false
	});
}

function stopPipeline(pipelineId){
	console.log("Stopping Pipeline");
	var url = standardUrl + "pipelines/" + pipelineId + "/stop";
	$.ajax({
		url : url,
		success : function(data){
			console.log("success");
			console.log(data);
			if (data.success){
				displaySuccess(data);
				changePipelineStatus(data, pipelineId);
			}else{
				displayErrors(data);
			}
		},
		error : function(data){
			console.log(data);
		},
		type : 'GET',
		processData: false
	});
}

function displayPipeline(json){
	console.log("displayPipeline()");
	console.log(jsPlumb.getContainer());
	for (var i = 0, stream; stream = json.streams[i]; i++){		
		createPreviewElement("stream", stream, i, json);
	}
	for (var i = 0, sepa; sepa = json.sepas[i]; i++){
		
		createPreviewElement("sepa", sepa, i, json);
	}
	createPreviewElement("action", json.action);
	connectPipelineElements(json, false);
	jsPlumb.repaintEverything(true);
}

function createPreviewElement(type, element, i, json){
	
	var $state = $("<span>")
		.addClass("connectable a")
		.attr("id", element.DOM)
		.data("JSON", $.extend(true, {}, element));
	if (element.iconUrl == null){ //Kein icon in JSON angegeben
		var md5 = CryptoJS.MD5(element.elementId);
		var $ident = $('<p>')
			.text(md5)
			.appendTo($state);
		$ident.identicon5({size:79});
		$ident.children().addClass("connectable-img tt")
		.attr(
			{"data-toggle": "tooltip",
			"data-placement": "top",
			"data-delay": '{"show": 1000, "hide": 100}',
			title: element.name
			})
		.data("JSON", $.extend(true, {},element));
	}else{
		$('<img>')
			.addClass('connectable-img tt')
			.attr(
				{
				src : element.iconUrl,
				"data-toggle": "tooltip",
				"data-placement": "top",
				"data-delay": '{"show": 1000, "hide": 100}',
				title: element.name
				})
			.appendTo($state)
			
			.data("JSON", $.extend(true, {},element));
	}
	
	var topLeftY, topLeftX;
	
	switch (type){
		
		case "stream":
			$state.appendTo("body");
			$state.addClass("stream");
			topLeftY = getYPosition(json.streams.length , i, $("#streamDisplay"), $state);
			topLeftX = getXPosition($("#streamDisplay"), $state);
			$state.appendTo("#streamDisplay");
			break;
		
			// jsPlumb.addEndpoint($icon,streamEndpointOptions);
		case "sepa":
			$state.appendTo("body");
			$state.addClass("sepa");
			topLeftY = getYPosition(json.sepas.length , i, $("#sepaDisplay"), $state);
			topLeftX = getXPosition($("#sepaDisplay"), $state);
			$state.appendTo("#sepaDisplay");
			break;
		
		case "action":
			$state.appendTo("body");
			$state.addClass("action");
			topLeftY = $("#actionDisplay").height() / 2 - (1/2) * $state.outerHeight();
			topLeftX = $("#actionDisplay").width() / 2 - (1/2) * $state.outerWidth();
			$state.appendTo("#actionDisplay");
			break;
	}
	$state.css(
		{
			"position" : "absolute",
			"top": topLeftY,
			"left": topLeftX
		}
	);
}

function connectPipelineElements(json, detachable){
	console.log("connectPipelineElements()");
	console.log(jsPlumb.getContainer());
	var source, target;
	var sourceOptions = {
		endpoint: ["Dot", {radius: 5}],
		paintStyle: {fillStyle: "grey"},
		connectorStyle : {strokeStyle: "grey", lineWidth: 4},
		connector: "Straight",
		anchor: "Right",
		connectorOverlays: [ 
		    [ "Arrow", { width:25, length:20, location:.5, id:"arrow" } ],   
	  	]
	};
	var targetOptions = {
		endpoint: "Rectangle",
		paintStyle: {fillStyle: "grey"},
		anchor: "Left",
		isTarget: true
	};
	jsPlumb.setSuspendDrawing(true);
	
	//Action --> Sepas----------------------//
		target = json.action.DOM;
	
		for (var i = 0, connection; connection = json.action.connectedTo[i]; i++){
			source = connection;

			var sourceEndpoint = jsPlumb.addEndpoint(source, sepaEndpointOptions);
			var targetEndpoint = jsPlumb.addEndpoint(target, leftTargetPointOptions);
			jsPlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable:detachable});
		}
	//Sepas --> Streams---------------------//
		for (var i = 0, sepa; sepa = json.sepas[i]; i++){
			for (var j = 0, connection; connection = sepa.connectedTo[j]; j++){
				
				source = connection;
				target = sepa.DOM;
				// console.log(source);
				// console.log(target);
				
				var sourceEndpoint = jsPlumb.addEndpoint(source, streamEndpointOptions);
				var targetEndpoint = jsPlumb.addEndpoint(target, leftTargetPointOptions);
				jsPlumb.connect({source: sourceEndpoint, target: targetEndpoint, detachable:detachable});
			}
		}
		jsPlumb.setSuspendDrawing(false ,true);
	
	
	
}

function adjustPipeline(json){
	var type = "Proa"; //TODO ändern auf jeweiligen Use-Case
	console.log(json);
	state.adjustingPipelineState = true;
	clearPipelineDisplay();
	//jsPlumb.setContainer($("#assembly"));
	$("#tabs").find("a[href='#editor']").tab('show');
	showAdjustingPipelineState(json.name);
	
	// refresh(type);
	
	var currentx = 50;
	var currenty = 50;
	for (var i = 0, stream; stream = json.streams[i]; i++){
		handleDroppedStream(createNewAssemblyElement(stream, {'x':currentx, 'y':currenty}));
		currenty += 200;
	}
	currenty = 50;
	for (var i = 0, sepa; sepa = json.sepas[i]; i++){
		currentx += 200;
		handleDroppedSepa(createNewAssemblyElement(sepa, {'x':currentx, 'y':currenty})
		.data("options", true));
			
	}
	currentx += 200;
	handleDroppedAction(createNewAssemblyElement(json.action, {'x':currentx, 'y':currenty})
		.data("options", true));
	$("#logo-home").data("pipeline", json);
	
	connectPipelineElements(json, true);
	jsPlumb.repaintEverything(true);
	
}

function getYPosition(count , i, $canvas, $element){
	return ($canvas.height() / count)/2 - (1/2) * $element.height() + i * ($canvas.height() / count);
}
function getXPosition($canvas, $element){
	return ($canvas.width() / 2) - (1/2) * $element.width();
}
function clearPipelineDisplay(){
	jsPlumb.deleteEveryEndpoint();
	$("#pipelineDisplay").children().each(function(){		
		$(this).children().remove();
	});
	
}
