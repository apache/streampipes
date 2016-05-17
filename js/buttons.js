function getDefaultButton(text, onclick){
	return $("<button type='button' class='btn btn-default'>").text(text).on('click', onclick);
}

function getGlyphIconButton(glyphicon, active, onclick){
	if (active) return $("<button type='button' class='btn btn-default' style='margin-left:5px'>").append($("<span>").addClass(glyphicon)).on('click', onclick);
	else return $("<button type='button' class='btn btn-default' style='margin-left:5px' disabled>").append($("<span>").addClass(glyphicon));
}
