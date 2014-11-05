var standardDraggableOptions = {
	revert : 'invalid',
	helper : 'clone',
	stack : '.draggable-icon',
	start : function(e, ui) {
		$('.alpha').fadeTo(300, .2);
		$('#assembly').css('border', '3px dashed grey');
		ui.helper.appendTo('body');
		ui.helper.children().addClass("draggable-img-dragging");
	},
	stop : function(e, ui) {
		$('.alpha').fadeTo(300, 1);
		$('#assembly').css('border', '');
		ui.helper.children().removeClass("draggable-img-dragging");
	}
};



/**
 * Initiates the webapp for the given use-case
 */
function init(type) {
	var debug = false;
	savedSepas= false;
	savedActions = false;
	if (debug){standardUrl = testUrl;}

	// Get and inititate sources------------------------
	if (type == "Proa") {
		domain = "DOMAIN_PROASENSE";
	} else if (type == "PA") {
		domain = "DOMAIN_PERSONAL_ASSISTANT";
	}
	var url = standardUrl + "sources?domain=" + domain;
	savedSources = $.getJSON(url, initSources);

	//Inititate accordion functionality-----------------
	//Initiate assembly and jsPlumb functionality-------
	jsPlumb.ready(function(e){
		initAssembly();
 		jsPlumb.Defaults.Container = "assembly";
		$('#accordion').on('show.bs.collapse', function() {
			$('#accordion .in').collapse('hide');
		});
		$('#collapseOne,#collapseTwo,#collapseThree').collapse({toggle: false});
		
		$(document).click(function() {
			$('#assemblyContextMenu').hide();
			$('#staticContextMenu').hide();
		});
		$('#sources').on('change', function(){
			$(this)
			.css("background-color", "#044")
			.animate("200")
			.css("background-color", "")
			.animate("200");
		});
		$("#pipelineTableBody").on("click","tr", function(){
			$(this).addClass("info");
			$("#pipelineTableBody").children().not(this).removeClass("info");
		});
		
	});

	


	//Bind click handler--------------------------------
	
	
};

/**
 * Converts the JSON into clickable Event Producers
 */
function initSources(data) {

	$.each(data, function(i, json) {
		var idString = "source" + i;

		var $newSource = $('<span>').attr({
			id : idString,
			class : "clickable  tt",
			"data-toggle" : "tooltip",
			"data-placement" : "top",
			title : json.name
		})
		.data("JSON", json)
		.click(displayStreams)
		.on("contextmenu", staticContextMenu)
		.appendTo('#sources');

		if (json.iconUrl == null) {//No Icon Path found in JSON
			var md5 = json.elementId.replace("-", "");
			var $ident = $('<p>').text(md5).appendTo($newSource);
			$ident.identicon5({
				size : 150
			});
			$ident.children().addClass("clickable-img").data("JSON", json);
		} else {//Icon Path
			$('<img>').attr("src", json.iconUrl).addClass('clickable-img').data("JSON", json).appendTo($newSource);
		}

	});
	initTooltips();
}
/**
 * Gets and displays streams for clicked source 
 */
function displayStreams(e) {
	$('#streamCollapse').attr("data-toggle", "collapse");
	$('#streamCollapse').removeClass("disabled");
	$('#streams').children().remove();
	$(this).fadeTo(0, 1);
	$('.clickable').not(this).fadeTo(200, .2);
	var $src = $(this);
	// console.log(typeof $(this).data("streams"));
	if (typeof $(this).data("streams") != "undefined"){
		createStreams($(this).data("streams"));
	}else{
		var url = standardUrl + "sources/" + encodeURIComponent($(this).data("JSON").elementId) + "/events";
		var promise = $.getJSON(url).then(function(data){
			savedStreams = data;
			$src.data("streams", savedStreams);
			createStreams(data);
		}); 
		
		
		
	}
	
	$('#streams').fadeTo(300, 1);
	e.stopPropagation();

	$('#collapseOne').collapse('show');

}

function createStreams(data){
		
	$.each(data, function(i, json) {
		var idString = "stream" + i;
		var $newStream = $('<span>')//<img>
		.attr({
			id : idString,
			class : "draggable-icon stream tt",
			"data-toggle" : "tooltip",
			"data-placement" : "top",
			title : json.name
		}).data("JSON", json)
		.on("contextmenu", staticContextMenu)
		.appendTo('#streams').show();
		if (json.iconUrl == null) {
			var md5 = json.elementId.replace("-", "");
			var $ident = $('<p>').text(md5).on("contextmenu", staticContextMenu)
			// .data("JSON", json)
			.appendTo($newStream);
			$ident.identicon5({
				size : 150
			});
			$ident.children().addClass("draggable-img").data("JSON", json);
		} else {
			$('<img>').attr("src", json.iconUrl).addClass("draggable-img").on("contextmenu", staticContextMenu)
			// .data("JSON", json)
			.appendTo($newStream);
		}
	});
	makeDraggable("stream");
	initTooltips();
}




/**
 * Gets and displays Sepas 
 */
function displaySepas(e) {
	$('#sepaCollapse').attr("data-toggle", "collapse");
	$('#sepaCollapse').removeClass("disabled");
	$('#sepas').children().remove();
	var url = standardUrl + "sepas?domains=" + domain;
	if (!savedSepas){
		$.getJSON(url).then(function(data){
			createSepas(data);
			savedSepas = data;
		});
	}else{
		createSepas(savedSepas);
	}

}

function createSepas(data){
	$.each(data, function(i, json) {
		if (($.inArray(domain, json.domains) != -1) || ($.inArray("DOMAIN_INDEPENDENT", json.domains) != -1)) {
			var idString = "sepa" + i;
			var $newSepa = $('<span>').attr({
				id : idString,
				class : "draggable-icon sepa tt",
				"data-toggle" : "tooltip",
				"data-placement" : "top",
				title : json.name
			}).data("JSON", json).on("contextmenu", staticContextMenu).appendTo('#sepas').show();
			if (json.iconUrl == null) {
				var md5 = json.elementId.replace("-", "");
				var $ident = $('<p>').text(md5).on("contextmenu", staticContextMenu).data("JSON", json).appendTo($newSepa);
				$ident.identicon5({
					size : 180
				});
				$ident.children().addClass("draggable-img").data("JSON", json);
			} else {
				$('<img>').attr("src", json.iconUrl).addClass("draggable-img").on("contextmenu", staticContextMenu).data("JSON", json).appendTo($newSepa);
			}
		}
	});
	makeDraggable("sepa");
	initTooltips();
}



/**
 * Displays Actions 
 */
function displayActions(e) {
	$('#actionCollapse').attr("data-toggle", "collapse");
	$('#actionCollapse').removeClass("disabled");
	var url = standardUrl + "actions";
	
	
	if (savedActions){
		createActions(savedActions);
	}else{
		$.getJSON(url).then(function(data){
			createActions(data);
			savedActions = data;
		});
	}
}
	
function createActions(data){
	$.each(data, function(i, json) {
		var idString = "action" + i;
		var $newAction = $('<span>').attr({
			id : idString,
			class : "draggable-icon action tt",
			"data-toggle" : "tooltip",
			"data-placement" : "top",
			title : json.name
		}).data("JSON", json)
		.on("contextmenu", staticContextMenu)
		.appendTo('#actions')
		.show();
		if (json.iconUrl == null) {
			var md5 = json.elementId.replace("-", "");
			var $ident = $('<p>').text(md5).on("contextmenu", staticContextMenu).data("JSON", json).appendTo($newAction);
			$ident.identicon5({
				size : 180
			});
			$ident.children().addClass("draggable-img").data("JSON", json);
		} else {
			$('<img>')
				.attr("src", json.iconUrl)
				.addClass("draggable-img")
				.on("contextmenu", staticContextMenu)
				.data("JSON", json)
				.appendTo($newAction);
		}
	});
	makeDraggable("action");
	initTooltips();
}	
	

/**
 * initiates tooltip functionality 
 */
function initTooltips() {
	$('.tt').tooltip();
}

/**
 * initiates drag and drop functionality for given elements
 * @param {Object} type stream, sepa or action elements
 */
function makeDraggable(type) {

	if (type === "stream") {
		$('.draggable-icon.stream').draggable({
			revert : 'invalid',
			helper : 'clone',
			stack : '.draggable-icon',
			start : function(stream, ui) {
				if ($('#sepas').css("opacity") === "0") {
					$('.alpha').not('#sepas').fadeTo(300, .2);
					ui.helper.appendTo('body');
					ui.helper.children().addClass("draggable-img-dragging");

				} else {
					ui.helper.appendTo('body');
					ui.helper.children().addClass("draggable-img-dragging");
				}

				$('#assembly').css('border', '3px dashed grey');
			},
			stop : function(stream, ui) {
				if ($('#sepas').css("opacity") == "0") {
					$('.alpha').not('#sepas').fadeTo(300, 1);
				} else {
					$('.alpha').fadeTo(300, 1);
				}
				$('#assembly').css('border', '');
				ui.helper.children().removeClass("draggable-img-dragging");
			}
		});
	} else if (type === "sepa") {
		$('.draggable-icon.sepa').draggable(standardDraggableOptions);
	} else {
		$('.draggable-icon.action').draggable(standardDraggableOptions);
	}
}

/**
 * Shows the contextmenu for given element 
 * @param {Object} e
 */
function staticContextMenu(e) {
	$('#staticContextMenu').data("invokedOn", $(e.target)).show().css({
		position : "absolute",
		left : getLeftLocation(e, "static"),
		top : getTopLocation(e, "static")
	});
	ContextMenuClickHandler("static");
	return false;

}



/**
 * Gets the position of the dropped element insidy the assembly 
 * @param {Object} helper
 */
function getDropPosition(helper) {
	var helperPos = helper.position();
	var divPos = $('#assembly').position();
	var newTop = helperPos.top - divPos.top;
	return newTop;
}

/**
 *  
 * @param {Object} e
 * @param {Object} type
 */
function getLeftLocation(e, type) {
	if (type === "static") {
		var menuWidth = $('#staticContextMenu').width();
	} else {
		var menuWidth = $('#assemblyContextMenu').width();
	}
	var mouseWidth = e.pageX;
	var pageWidth = $(window).width();

	// opening menu would pass the side of the page
	if (mouseWidth + menuWidth > pageWidth && menuWidth < mouseWidth) {
		return mouseWidth - menuWidth;
	}
	return mouseWidth;
}

function getTopLocation(e, type) {
	if (type === "static") {
		var menuHeight = $('#staticContextMenu').height();
	} else {
		var menuHeight = $('#assemblyContextMenu').height();
	}

	var mouseHeight = e.pageY;
	var pageHeight = $(window).height();

	// opening menu would pass the bottom of the page
	if (mouseHeight + menuHeight > pageHeight && menuHeight < mouseHeight) {
		return mouseHeight - menuHeight;
	}
	return mouseHeight;
}

/**
 * Handles clicks in the contextmenu 
 * @param {Object} type
 */
function ContextMenuClickHandler(type) {

	if (type === "assembly") {
		$('#assemblyContextMenu').off('click').on('click', function(e) {
			$(this).hide();

			var $invokedOn = $(this).data("invokedOn");
			var $selected = $(e.target);
			while ($invokedOn.parent().get(0) != $('#assembly').get(0)) {
				$invokedOn = $invokedOn.parent();

			}
			if ($selected.get(0) === $('#delete').get(0)) {

				jsPlumb.removeAllEndpoints($invokedOn);

				$invokedOn.remove();

				if (!$('#assembly').children().hasClass('stream')) {
					$('#sepas').children().hide();
					$('#sepas').fadeTo(300, 0);
					$('#sepaCollapse').attr("data-toggle", "");
					$('#sepaCollapse').addClass("disabled");
					$('#actionCollapse').attr("data-toggle", "");
					$('#actionCollapse').addClass("disabled");
					$('#collapseOne').collapse('show');
					
				} else if (!$('#assembly').children().hasClass('sepa')) {
					$('#actions').children().hide();
					$('#actions').fadeTo(300, 0);
					$('#actionCollapse').attr("data-toggle", "");
					$('#actionCollapse').addClass("disabled");
					$('#collapseTwo').collapse('show');
				} else if (!$('#assembly').children().hasClass('action')){
					$('#collapseThree').collapse('show');
				}
			} else {//Customize clicked

				$('#customize-content').html(prepareCustomizeModal($invokedOn));
				$('#customizeModal').modal('show');
			}
		});
	} else if (type === "static") {
		$('#staticContextMenu').off('click').on('click', function(e) {
			$(this).hide();
			var $invokedOn = $(this).data("invokedOn");
			while ($invokedOn.parent().get(0) != $('#sources').get(0) && $invokedOn.parent().get(0) != $('#streams').get(0) && $invokedOn.parent().get(0) != $('#sepas').get(0)) {
				$invokedOn = $invokedOn.parent();
			}
			var json = $invokedOn.data("JSON");
			$('#description-title').text(json.name);
			$('#modal-description').text(json.description);
			$('#descrModal').modal('show');
		});
	}
}



function refresh(type) {

	if (type == "Proa") {
		$('#typeChange').html("Proasense <b class='caret'></b>");
	} else if (type == "PA") {
		$('#typeChange').html("Personal Assistant <b class='caret'></b>");
	}
	enableOptions();

	$('#sources').children().remove();
	$('#streams').children().remove();
	$('#streams').fadeTo(300, 0);
	$('#sepas').fadeTo(300, 0);
	clearAssembly();
	$('#streamCollapse').attr("data-toggle", "");
	$('#streamCollapse').addClass("disabled");

	init(type);
}



function showAdd() {
	$('#addModal').modal('show');
}

function showManage(){
	var list = {};
	var sources = [];
	var streams = [];
	list.sources = sources;
	list.streams = streams;
	
	$('#elementList').empty();
	
	//  ===================== 
	//  = Sources + Streams = 
	//  ===================== 
	
	
	$('<ul>')
		.addClass("list-group")
		.attr("id", "sourceList")
		.appendTo("#elementList");
	$('<h4>')
		.addClass("list-group-item-heading")
		.text("Sources/Streams")
		.appendTo("#sourceList");
	$('.clickable').each(function(i){
		var $src = $('<li>')
			.attr("id", "srcList" + i)
			.addClass("list-group-item")
			.html("<strong>" + $(this).data("JSON").name + "</strong>")
			.data("JSON" , $(this).data("JSON"))
			.appendTo("#sourceList");
		
		addButtons($src, "sources");	
		
		 if ($(this).data("streams") != undefined){
			var data = $(this).data("streams");
			console.log(data);
			$.each(data, function(i, json){
					$('<li>')
						.data("JSON", json)
						.addClass("list-group-item")
						.text(json.name)
						.appendTo($src);
			});
		}else {
			var url = standardUrl + "sources/" + encodeURIComponent($(this).data("JSON").elementId) + "/events";
			var $origSrc = $(this);
			console.log($origSrc);
			var promise = $.getJSON(url).then(function(data){
				savedStreams = data;
				$origSrc.data("streams", savedStreams);
				$.each(data, function(i, json){
					$('<li>')
						.data("JSON", json)
						.addClass("list-group-item")
						.text(json.name)
						.appendTo($src);
				});
			});
		}
		
			
	});
	
	//  ========== 
	//  = Sepas = 
	//  ========== 
	
	$('<ul>')
		.addClass("list-group")
		.attr("id", "sepaList")
		.appendTo("#elementList");
	var $header = $('<h4>')
		.addClass("list-group-item-heading")
		.text("Sepas")
		.appendTo("#sepaList");
	if (!savedSepas){
		var url = standardUrl + "sepas?domains=" + domain;
		$.getJSON(url).then(function(data){
			savedSepas = data;
			$.each(data, function(i, json){
				var $el = $('<li>')
					.data("JSON", json)
					.addClass("list-group-item")
					.text(json.name)
					.appendTo("#sepaList");
				addButtons($el, "sepas");
				
			});
		});
	}else{
		$.each(savedSepas, function(i, json){
				var $el = $('<li>')
					.data("JSON", json)
					.addClass("list-group-item")
					.text(json.name)
					.appendTo("#sepaList");
				addButtons($el, "sepas");
			});
	}
	

		$('#manageModal').modal('show');
	
	//  =========== 
	//  = Actions = 
	//  ===========
	
	$('<ul>')
		.addClass("list-group")
		.attr("id", "actionList")
		.appendTo("#elementList");
	$('<h4>')
		.addClass("list-group-item-heading")
		.text("Actions")
		.appendTo("#actionList");
	var $el;
	if (!savedActions){
		var url = standardUrl + "actions";
		$.getJSON(url).then(function(data){
			savedActions = data;
			$.each(data, function(i, json){
				var $el = $('<li>')
					.data("JSON", json)
					.addClass("list-group-item")
					.text(json.name)
					.appendTo("#actionList");
				addButtons($el, "actions");
				
					
			});
		});
	}else{
		$.each(savedActions, function(i, json){
				var $el = $('<li>')
					.data("JSON", json)
					.addClass("list-group-item")
					.text(json.name)
					.appendTo("#actionList");
				addButtons($el, "actions");
			});
	}
	
	

}


function addButtons($element, type){
	var $wrapper = $('<div>')
		.addClass("btn-group-xs pull-right");
	
	var $button = $('<button>')
		.addClass("btn btn-default")
		.attr("type", "button")
		.on('click' , function(e){
			var elementId = $element.data("JSON").elementId;
			var uri = encodeURIComponent(elementId);
			if (type == "sources"){
				url = standardUrl + "sources";
			}else if(type == "sepas"){
				url = standardUrl + "sepas";
			}else if (type == "actions"){
				url = standardUrl + "actions";
			} 
			
			$.ajax({
			  	url: url,
			  	data: "uri=" + uri,
			  	processData: false,
			  	type: 'POST',
			  	success: function(data){
			  		toastRightTop("success", "Element successfully updated");
			  		refresh("Proa");
			    
			    	switch (type){
					case "1":
						break;
					case "2":
						refreshSepas();
						break;
					case "3":
						refreshActions();
					}
			  	}
			});
		})
		.appendTo($wrapper);
	
	$('<span>')
		.addClass("glyphicon glyphicon-refresh")
		.appendTo($button);	
	
	$button = $('<button>')
		.addClass("btn btn-default")
		.attr("type", "button")
		.on('click' , function(e){
			var elementId = $element.data("JSON").elementId;
			var uri = standardUrl + type + "/" + encodeURIComponent(elementId);

			$.ajax({
			    url: uri,
			    type: 'DELETE',
			    success: function(result){
			        $element.remove();
			        toastRightTop("success", "Element successfully deleted");
			        refresh("Proa");
			        
			          
			    }
			});
			if (type == "sepas"){
				refreshSepas();
			}else if (type == "actions"){
				refreshActions();
			}
		})
		.appendTo($wrapper);
		
	$('<span>')
		.addClass("glyphicon glyphicon-remove ")
		.appendTo($button);
		
	$wrapper.appendTo($element);
			
	
		
		
}

function refreshSepas(){
	var url = standardUrl + "sepas?domains=" + domain;
	$.getJSON(url).then(function(data){
		savedSepas = data;
	});
}

function refreshActions(){
	var url = standardUrl + "actions";
	$.getJSON(url).then(function(data){
		savedActions = data;
	});
}



function add() {			
	var uri = $('#addText').val();
	uri = encodeURIComponent(uri);
	if (uri != ''){

		var type = $('input[name="type-radios"]:checked').val();
		switch (type){
			case "1":
				var url = standardUrl + "sources";
				break;
			case "2":
				var url = standardUrl + "sepas";
				break;
			case "3":
				var url = standardUrl + "actions";
		}
		
		var fd = new FormData();    
		fd.append( 'uri', uri );
		
		$.ajax({
		  url: url,
		  data: "uri=" + uri,
		  processData: false,
		  type: 'POST',
		  success: function(data){
		  	toastRightTop("success", "Element successfully added");
		  	refresh("Proa");
		  	$('#sses').text("Finished!");
		    
		    switch (type){
			case "1":
				
				break;
			case "2":
				refreshSepas();
				break;
			case "3":
				refreshActions();
			}
		  }
		});
		$('#sses').text("Working...");
				
	}else{
		toastRightTop("error", "Please enter a URI");
	}
}

function manage(type){
		
	$('#elementList').empty();
	
	switch(type){
		
		case "stream":
	
			
			$('#descr').text($('#typeChange').text() + " - Streams");
			
			$('.clickable').each(function(e){
				var url = standardUrl + "sources/" + encodeURIComponent($(this).data("JSON").elementId) + "/events";
				var streams = $.getJSON(url, function(data) {
					$.each(data, function(i, json) {
						var $listElement = $('<li>')
							.addClass("list-group-item")
							.text(json.name)
							.data("JSON", json)
							.appendTo('#elementList');
						$('<span><strong>')
							.text("?")
							.addClass("hoverable")
							.css("margin-left", "5px")
							.appendTo($listElement)
							.attr({
								"data-toggle" : "popover",
								"title" : "JSON",
								"data-content" : JSON.stringify($(this).parent().data("JSON")),
								"data-placement" : "auto" 
							})
							.popover();
							console.log(JSON.stringify($(this).parent().data("JSON")));
						$('<span>')
							.addClass("glyphicon glyphicon-remove pull-right hoverable")
							.on('click' , function(e){
								var elementId = $(e.target).parent().data("JSON").elementId;
								var uri = standardUrl + "streams/" + elementId;
		
								$.ajax({
								    url: uri,
								    type: 'DELETE',
								    success: function(result) {
								        alert(result);
								    }
								});
							})
							.appendTo($listElement);	
					});
				});
				
			});
			
		case "sepa":
	
	}
				
	$('#manageModal').modal('show');
}

function toastTop(type, message, title){
	toastr.options = {
		"newestOnTop":false,
		"positionClass": "toast-top-full-width"
	};
	
	switch (type){
		case "error":	
			toastr.error(message, title);
			return;
		case "warning":
			toastr.warning(message, title);
			return;
		case "success":
			toastr.success(message, title);
			return;
		case "info":
			toastr.info(message, title);
			return;
	}
}

function toastRightTop(type, message, title){
	toastr.options = {
		"newestOnTop":false,
		"positionClass": "toast-top-right"
	};
	
	switch (type){
		case "error":	
			toastr.error(message, title);
			return;
		case "warning":
			toastr.warning(message, title);
			return;
		case "success":
			toastr.success(message, title);
			return;
		case "info":
			toastr.info(message, title);
			return;
	}
}

function disableOptions(){
	$('#options')
		.addClass("disabled")
		.children()
		.attr("data-toggle", "");
	
}
function enableOptions(){
	$('#options')
		.removeClass("disabled")
		.children()
		.attr("data-toggle", "dropdown");
}
