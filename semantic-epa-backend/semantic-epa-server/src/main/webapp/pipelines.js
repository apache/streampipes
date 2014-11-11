var pipelines;

function getPipelines(){
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
	$("<td>").text("OK").append(getGlyphIconButton("glyphicon glyphicon-refresh", validatePipeline($row.data("JSON").id))).appendTo($row); //VALIDATION STATUS
	// $("<td>").text("OK").append(getGlyphIconButton("glyphicon glyphicon-refresh", validatePipeline("TESTID"))).appendTo($row); //DEBUG
	$("<td>").text($row.data("JSON").pipelineStatus).appendTo($row);
	$("<td>").text("TESTSTATUS").appendTo($row);
	// $("<td>").append($("<div class='btn-group'>")
		// .append(getGlyphIconButton("glyphicon glyphicon-play", startPipeline($row.data("JSON").id))).append(getGlyphIconButton("glyphicon glyphicon-stop", stopPipeline($row.data("JSON").id))));
	$("<td>").append($("<div class='btn-group'>")
		.append(getGlyphIconButton("glyphicon glyphicon-play", startPipeline("TESTID"))).append(getGlyphIconButton("glyphicon glyphicon-stop", stopPipeline("TESTID")))).appendTo($row);
	
	
}

function validatePipeline(pipelineId){
	
}

function startPipeline(pipelineId){}

function stopPipeline(pipelineId){}

function displayPipeline(){
	
}



function makeActive(){
	$(this).addClass("info");
}
