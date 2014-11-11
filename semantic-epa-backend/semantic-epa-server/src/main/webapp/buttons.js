function getDefaultButton(text, onclick){
	return $("<button type='button' class='btn btn-default'>").text(text).click(onclick);
}

function getGlyphIconButton(glyphicon, onclick){
	return $("<button type='button' class='btn btn-default' style='margin-left:5px'>").append($("<span>").addClass(glyphicon)).click(onclick);
}
