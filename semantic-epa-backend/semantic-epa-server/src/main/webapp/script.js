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

	// Get and inititate sources------------------------
	if (type == "Proa") {
		domain = "DOMAIN_PROASENSE";
	} else if (type == "PA") {
		domain = "DOMAIN_PERSONAL_ASSISTANT";
	}
	var url = standardUrl + "sources?domain=" + domain;
	$.getJSON(url, initSources);

	//Inititate accordion functionality-----------------
	//Initiate assembly and jsPlumb functionality-------
	jsPlumb.ready(function(e){
		initAssembly();
 		jsPlumb.Defaults.Container = "assembly";
		$('#accordion').on('show.bs.collapse', function() {
			$('#accordion .in').collapse('hide');
		});
		$('#collapseOne,#collapseTwo,#collapseThree').collapse({toggle: false});
	});

	//Bind click handler--------------------------------
	$(document).click(function() {
		$('#assemblyContextMenu').hide();
		$('#staticContextMenu').hide();
	});
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
		}).data("JSON", json).click(displayStreams).on("contextmenu", staticContextMenu).appendTo('#sources');

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
	var url = standardUrl + "sources/" + encodeURIComponent($(this).data("JSON").elementId) + "/events";
	var streams = $.getJSON(url, function(data) {
		$.each(data, function(i, json) {
			var idString = "stream" + i;
			var $newStream = $('<span>')//<img>
			.attr({
				id : idString,
				class : "draggable-icon stream tt",
				"data-toggle" : "tooltip",
				"data-placement" : "top",
				title : json.name
			}).data("JSON", json).on("contextmenu", staticContextMenu).appendTo('#streams').show();
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
	});
	$('#streams').fadeTo(300, 1);
	e.stopPropagation();

	$('#collapseOne').collapse('show');

}
/**
 * Gets and displays Sepas 
 */
function displaySepas(e) {
	$('#sepaCollapse').attr("data-toggle", "collapse");
	$('#sepaCollapse').removeClass("disabled");
	$('#sepas').children().remove();
	var url = standardUrl + "sepas?domains=" + domain;
	var sepas = $.getJSON(url, function(data) {
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
	});

}
/**
 * Displays Actions 
 */
function displayActions(e) {
	$('#actionCollapse').attr("data-toggle", "collapse");
	$('#actionCollapse').removeClass("disabled");

	/*-----------------TEST TEST TEST TEST TEST-----------------*/

	var $newAction = $('<span>').addClass("draggable-icon action tt").appendTo('#actions').attr({
		"data-toggle" : "tooltip",
		"data-placement" : "top",
		title : "DO SOMETHING"
	});
	$('<span>').addClass("glyphicon glyphicon-hand-up draggable-img").appendTo($newAction);

	var $newAction = $('<span>').addClass("draggable-icon action tt").appendTo('#actions').attr({
		"data-toggle" : "tooltip",
		"data-placement" : "top",
		title : "MONITOR"
	});
	$('<span>').addClass("glyphicon glyphicon-list-alt draggable-img").appendTo($newAction);

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
	$('#elementList').empty();
	$('#descr').text("Streams");
	var url = standardUrl + "streams";
	
	$.getJSON(url, function(data){
		$.each(data, function(i, json){
			var $listElement = $('<li>')
				.addClass("list-group-item")
				.text(json.name)
				.data("JSON", json)
				.appendTo('#elementList');
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
	
	
	$('#manageModal').modal('show');
	
	
	
}

function add() {			
	var uri = $('#addText').val();
	if (uri != ''){
	// var xmlhttp = new XMLHttpRequest();
	// xmlhttp.open("POST", "", true);
	// //URL DES SERVERS ANGEBEN
	// xmlhttp.send(url);
	
		var url = standardUrl + "streams/";
		$.post(url, uri);
			
		if (!!window.EventSource) {
			var source = new EventSource('');
		} else {
			// Result to xhr polling :(
		}
		source.addEventListener('message', function(e) {
			console.log(e.data);
			$('<p>').text(e.data).appendTo('#sses').slideDown('slow');
		}, false);
			
		
	}else{
		toastr.error("Bitte uri eingeben");
	}

}

function addTo(type){
	
	showAdd();
	
	switch (type){
		
		case "Streams":
			
		case "Sepas":
		
		case "Actions":
		
		default:

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
	
	// if ($(selector).length){
		
	// switch (type){
// 		
		// case "Streams":
			// $(selector).each(function(e){
				// var $listElement = $('<li>')
					// .addClass("list-group-item")
					// .text($(this).data("JSON").name)
					// .data("JSON", $(this).data("JSON"))
					// .appendTo('#elementList');
				// $('<span>')
					// .addClass("glyphicon glyphicon-remove pull-right hoverable")
					// .on('click' , function(e){
						// var elementId = $(e.target).parent().data("JSON").elementId;
						// // var uri = standardUrl + "streams/" + elementId;
// // 
						// // $.ajax({
						    // // url: uri,
						    // // type: 'DELETE',
						    // // success: function(result) {
						        // // console.log(result);
						    // // }
						// // });
					// })
					// .appendTo($listElement);
			// });
				
					
			$('#manageModal').modal('show');
		// case "Sepas":
// 		
		// case "Actions":
// 		
		// default:
// 		
// 
	// }
	// }
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
