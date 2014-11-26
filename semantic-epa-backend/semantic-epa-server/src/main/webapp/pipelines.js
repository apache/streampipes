var pipelines;

function getPipelines(){
	clearAssembly();
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
	$("<td>").text(i).appendTo($row);
	$("<td>").text($row.data("JSON").name).appendTo($row);
	// $("<td>").text("TESTNAME").appendTo($row); //DEBUG
	$("<td>").text("OK").append(getGlyphIconButton("glyphicon glyphicon-refresh", function(){verifyPipeline(pipelineId);})).appendTo($row); //VALIDATION STATUS
	// $("<td>").text("OK").append(getGlyphIconButton("glyphicon glyphicon-refresh", validatePipeline("TESTID"))).appendTo($row); //DEBUG
	// $("<td>").text($row.data("JSON").pipelineStatus).appendTo($row);
	$("<td>").text("TESTSTATUS").appendTo($row);
	$("<td>").append($("<div class='btn-group'>")
		.append(getGlyphIconButton("glyphicon glyphicon-play", function(){startPipeline(pipelineId);})).append(getGlyphIconButton("glyphicon glyphicon-stop", function(){stopPipeline($row.data("JSON").pipelineId);}))).appendTo($row);
	// $("<td>").append($("<div class='btn-group'>")
		// .append(getGlyphIconButton("glyphicon glyphicon-play", startPipeline("TESTID"))).append(getGlyphIconButton("glyphicon glyphicon-stop", stopPipeline("TESTID")))).appendTo($row);
	$("<td>").append(getGlyphIconButton("glyphicon glyphicon-remove", function(){console.log($(this).parent().parent().data("JSON")); deletePipeline($(this).parent().parent().data("JSON")._id, $(this).parent().parent());})).appendTo($row);
	
}

function verifyPipeline(pipelineId){
	var url = standardUrl + "pipelines/" + pipelineId + "/verify";
	$.ajax({
		url : url,
		success : function(data){
			alert(data);
			changePipelineStatus(data, pipelineId);
		},
		type : 'GET',
		processData: false
	});
}

function changePipelineStatus(data, pipelineId){}

function startPipeline(pipelineId){
	var url = standardUrl + "pipelines/" + pipelineId + "/start";
	$.ajax({
		url : url,
		success : function(data){
			alert(data);
			changePipelineStatus(data, pipelineId);
		},
		type : 'GET',
		processData: false
	});
}


function deletePipeline(pipelineId, $row){
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
		type : 'DELETE',
		processData: false
	});
}

function stopPipeline(pipelineId){}

function displayPipeline(json){
	for (var i = 0, stream; stream = json.streams[i]; i++){		
		createPreviewElement("stream", stream, i, json);
	}
	for (var i = 0, sepa; sepa = json.sepas[i]; i++){
		
		createPreviewElement("sepa", sepa, i, json);
	}
	createPreviewElement("action", json.action);
	connectElements(json);
}

function createPreviewElement(type, element, i, json){
	var $state = $("<span>")
		.addClass("connectable")
		.attr("id", element.DOM)
		.data("JSON", $.extend(true, {}, element));
	if (element.iconUrl == null){ //Kein icon in JSON angegeben
		var md5 = element.elementId.replace("-", "");
		var $ident = $('<p>')
			.text(md5)
			.appendTo($state);
		$ident.identicon5({size:150});
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
			
			.data("JSON", element);
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

function connectElements(json){
	var source, target;
	var options = {
	endpoint: ["Dot", {radius: 5}],
	paintStyle: {fillStyle: "grey"},
	connectorStyle : {strokeStyle: "grey", lineWidth: 4},
	connector: "Straight",
	anchor: ["Perimeter", {shape: "Circle"}],
	connectorOverlays: [ 
	    [ "Arrow", { width:25, length:20, location:.5, id:"arrow" } ],   
  	]
};
	
	//Action --> Sepas----------------------//
		target = json.action.DOM;
	
		for (var i = 0, connection; connection = json.action.connectedTo[i]; i++){
			source = connection;
			jsPlumb.connect({source: source, target: target, detachable:false});
		}
	//Sepas --> Streams---------------------//
		for (var i = 0, sepa; sepa = json.sepas[i]; i++){
			for (var j = 0, connection; connection = sepa.connectedTo[j]; j++){
				source = connection;
				target = sepa.DOM;
				jsPlumb.connect({source: source, target: target, detachable:false});
			}
		}
	
	
	
}

function getYPosition(count , i, $canvas, $element){
	return ($canvas.height() / count)/2 - (1/2) * $element.height() + i * ($canvas.height() / count);
}
function getXPosition($canvas, $element){
	return ($canvas.width() / 2) - (1/2) * $element.width();
}

function makeActive(){
	$(this).addClass("info");
}
