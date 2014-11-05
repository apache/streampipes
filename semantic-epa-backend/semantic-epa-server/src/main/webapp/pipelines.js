var pipelines;

function getPipelines(){
	var url = standardUrl + "pipelines";
	pipelines = $.getJSON(url, listPipelines);
	
}

function listPipelines(data){
	$.each(data, pipelineTableRowFactory(i, json));
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
	$("<td>").text("OK").append(getGlyphIconButton("glyphicon glyphicon-refresh", validatePipeline($row.data("JSON").id))).appendTo($row); //VALIDATION STATUS
	$("<td>").text($row.data("JSON").pipelineStatus).append($("<button>").addClass("btn btn-default").text("Val.")).appendTo($row);
	$("<td>").append($("<div class='btn-group'>").append($("<button class='btn btn-default'>").text("Start")))
	
	
}

function validatePipeline(pipelineId){
	$.ajax
}

function displayPipeline(){
	
}



function makeActive(){
	$(this).addClass("info");
}
