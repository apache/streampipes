//TODO copy

function getAccordionPart(id, parent, count, json){
	
	var html = "<div class='panel panel-default'>";
	html += "<div class='panel-heading'>";
	html += "<h4 class='panel-title'>";
	html += "<a id='" + id + "' data-toggle='collapse' data-parent='#" + parent + "' href='#" + count + "'>";
	html += json.name;
	html += "</a>";
	html += "</h4>";
	html += "</div>";
	html += "<div id='" + count + "' class='panel-collapse collapse'>";
	html += "<div class='panel-body'>";
	html += "<div id='body" + count + "' align='center'>";
	console.log("vor Body 1");
	html += getAccordionBody(json);
	console.log("nach Body 4");
	html += "</div></div></div></div>";
	
	return html;
} 

function getAccordionBody(json){
	console.log("in body 2");
	var html = "<ul class='list-group'>";
	var finished = false;
	 var url = standardUrl + "sources/" + encodeURIComponent(json.elementId) + "/events";
	 $.getJSON(url, function(data) {
		$.each(data, function(i, json) {
			html += "<li class='list-group-item'>";
			html += json.name;
			html += "</li>";
		});
			
	}).done(function(){
		console.log("FERTIG... ANSCHEINEND... 3");
		html += "</ul>";
		return html;
	});
	
	
}




// <div class="panel panel-default">
    // <div class="panel-heading">
      // <h4 class="panel-title">
        // <a id="streamCollapse" class="disabled" data-parent="#accordion" href="#collapseOne">
          // Streams
        // </a>
        // <span class="btngroup pull-right">
	        // <button type="button" class="btn btn-default btn-xs" onclick=addTo("stream")>
	        	// <span class="glyphicon glyphicon-plus"></span>
        	// </button>
        	// <button type="button" class="btn btn-default btn-xs" onclick=manage("stream")>
	        	// <span class="glyphicon glyphicon-cog"></span>
        	// </button>
    	// </span>
      // </h4>
    // </div>
    // <div id="collapseOne" class="panel-collapse collapse">
      // <div class="panel-body">
        // <div id="streams" align="center" class="icon-stands alpha">
// 
		// </div>
      // </div>
    // </div>
  // </div>
  
// var $listElement = $('<li>')
				// .addClass("list-group-item")
				// .text(json.name)
				// .data("JSON", json)
				// .appendTo('#elementList');
			// $('<span>')
				// .addClass("glyphicon glyphicon-remove pull-right hoverable")
				// .on('click' , function(e){
					// var elementId = $(e.target).parent().data("JSON").elementId;
					// var uri = standardUrl + "streams/" + elementId;
// 
					// $.ajax({
					    // url: uri,
					    // type: 'DELETE',
					    // success: function(result) {
					        // alert(result);
					    // }
					// });
				// })
				// .appendTo($listElement);