function getDefaultButton(text, onclick){
	return $("<button type='button' class='btn btn-default'>").text(text).on('click', onclick);
}

function getGlyphIconButton(glyphicon, onclick){
	return $("<button type='button' class='btn btn-default' style='margin-left:5px'>").append($("<span>").addClass(glyphicon)).on('click', onclick);
}
