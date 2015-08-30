var standardDraggableOptions = {
    revert: 'invalid',
    helper: 'clone',
    stack: '.draggable-icon',
    start: function (e, ui) {
        $('.alpha').fadeTo(300, .2);
        $('#assembly').css('border', '3px dashed grey');
        ui.helper.appendTo('#content');
        // ui.helper.children().addClass("draggable-img-dragging");
    },
    stop: function (e, ui) {
        $('.alpha').fadeTo(300, 1);
        $('#assembly').css('border', '');
        // ui.helper.children().removeClass("draggable-img-dragging");
    }
};
//var y = 0;


/**
 * Initiates the webapp for the given use-case
 */
function init(type) {
    //var debug = false;
    state.sources = false;
    state.sepas = false;
    state.actions = false;
    state.adjustingPipelineState = false;
    state.adjustingPipeline = {};

    //if (state.plumbReady) {
    //    clearPipelineDisplay();
    //}
    //
    //// Get and inititate sources------------------------
    //if (type == "Proa") {
    //    domain = "DOMAIN_PROASENSE";
    //} else if (type == "PA") {
    //    domain = "DOMAIN_PERSONAL_ASSISTANT";
    //}
    //var url = standardUrl + "sources?domain=" + domain;
    //$.ajax({
    //    dataType: "json",
    //    url: url,
    //    success: function (data) {
    //        state.sources = data;
    //        initSources(data);
    //    }
    //});


}
//Initiate assembly and jsPlumb functionality-------
//jsPlumb.ready(function (e) {
//    console.log("jsPlumb Ready");
//    state.plumbReady = true;
//    jsPlumb.bind("connection", function (info, originalEvent) {
//        var $target = $(info.target);
//        if (!$target.hasClass('a')){ //class 'a' = do not show customize modal //TODO class a zuweisen
//            createPartialPipeline(info);
//            $.when(
//                state.currentPipeline.update(info)
//            ).then(function(data){
//                if (data.success) {
//                    if ($target.hasClass('sepa')) {
//                        initRecs(state.currentPipeline, $target);
//                    }
//                }
//            });
//
//        }
//    });
//
//    if (debug){
//        $('#debugTestTab').show();
//    }
//
//
//    window.onresize = function (event) {
//        jsPlumb.repaintEverything(true);
//    };
//
//    initAssembly();
//    $("#assembly")
//        .selectable({
//            selected: function (event, ui) {
//            },
//            filter: ".connectable.stream,.connectable.sepa:not('.disabled')",
//            delay: 150
//
//        });
//    jsPlumb.Defaults.Container = "assembly";
//    //Inititate accordion functionality-----------------
//    $('#accordion').on('show.bs.collapse', function () {
//        $('#accordion').find('.in').collapse('hide');
//    });
//    $('#collapseOne,#collapseTwo,#collapseThree').collapse({toggle: false});
//
//    $(document).click(function () {
//        $('#assemblyContextMenu').hide();
//        $('#staticContextMenu').hide();
//        $('.circleMenu-open').circleMenu('close');
//    });
//    $('#sources').on('change', function () {
//        $(this)
//            .css("background-color", "#044")
//            .animate("200")
//            .css("background-color", "")
//            .animate("200");
//    });
//
//
//
//    $('a[data-toggle="tab"]')
//        .on('hide.bs.tab', function (e) {
//            clearTab(e);
//        })
//        .on('show.bs.tab', function (e) {
//            toTab(e);
//        });
//
//    $('#assembly').on('click',".recommended-item", function (e) {
//        console.log(e);
//        e.stopPropagation();
//        createAndConnect(this);
//    })
//
//});

function clearTab(e){

    var fromId = $(e.target).attr("href");

    if ( fromId == "#editor") {                 //Verl�sst Editor Tab

        disableOptions();

        if ($(".connectable").length > 0) {      //Elemente in Assembly

            if (!confirm("Do you wish to discard the current assembly?")) {
                e.preventDefault();
            } else {
                clearAssembly();
            }
        }
    }else if (fromId == "#pipelines"){          //Verl�sst Pipeline Tab
        clearPipelineDisplay();
    }
}

function toTab(e){

    var toId = $(e.target).attr("href");

    if (toId == "#editor"){
        enableOptions();
        jsPlumb.setContainer($("#assembly"));
    }else if (toId == "#pipelines"){
        jsPlumb.setContainer($("#pipelineDisplay"));
    }
}

/**
 * Converts the JSON into clickable Event Producers
 */
function initSources(data) {

    $.each(data, function (i, json) {
        var idString = "source" + i;

        var $newSource = $('<span>').attr({
            id: idString,
            class: "clickable  tt",
            "data-toggle": "tooltip",
            "data-placement": "top",
            title: json.name
        })
            .data("JSON", json)
            .click(displayStreams)
            .on("contextmenu", staticContextMenu)
            .appendTo('#sources');
       
        if (json.iconUrl == null) {//No Icon Path found in JSON
            addTextIconToElement($newSource, $newSource.data("JSON").name);
        } else {//Icon Path
            $('<img>').attr("src", json.iconUrl).addClass('clickable-img').data("JSON", json).appendTo($newSource)
                .error(function(){
                    addTextIconToElement($(this).parent(), $(this).parent().data("JSON").name );
                    $(this).remove();
                });
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

    if (typeof $(this).data("streams") != "undefined") {
        createStreams($(this).data("streams"));
    } else {
        var url = standardUrl + "sources/" + encodeURIComponent($(this).data("JSON").elementId) + "/events";
        $.getJSON(url).then(function (data) {
            var savedStreams = data;
            $src.data("streams", savedStreams);
            createStreams(data);
        });


    }

    $('#streams').fadeTo(300, 1);
    e.stopPropagation();

    $('#collapseOne').collapse('show');

}

//function createStreams(data) {
//
//    $.each(data, function (i, json) {
//        var idString = "stream" + i;
//        var $newStream = $('<span>')//<img>
//            .attr({
//                id: idString,
//                class: "draggable-icon stream tt",
//                "data-toggle": "tooltip",
//                "data-placement": "top",
//                title: json.name
//            }).data("JSON", json)
//            .on("contextmenu", staticContextMenu)
//            .appendTo('#editor-icon-stand');
//        if (json.iconUrl == null) {
//            addTextIconToElement($newStream, $newStream.data("JSON").name);
//        } else {
//            $('<img>').attr("src", json.iconUrl).addClass('draggable-img').on("contextmenu", staticContextMenu)
//                .data("JSON", json)
//                .appendTo($newStream)
//                .error(function(){
//                    addTextIconToElement($(this).parent(), $(this).parent().data("JSON").name );
//                    $(this).remove();
//                });
//
//            //$('<img>').attr("src", json.iconUrl).addClass("draggable-img").on("contextmenu", staticContextMenu)
//            //    // .data("JSON", json)
//            //    .appendTo($newStream);
//        }
//    });
//    makeDraggable("stream");
//    initTooltips();
//}


/**
 * Gets and displays Sepas
 */
//function displaySepas(e) {
//    $('#sepaCollapse').attr("data-toggle", "collapse");
//    $('#sepaCollapse').removeClass("disabled");
//    $('#sepas').children().remove();
//    var url = standardUrl + "sepas?domains=" + domain;
//    if (!state.sepas) {
//        $.getJSON(url).then(function (data) {
//            createSepas(data);
//            state.sepas = data;
//        });
//    } else {
//        createSepas(state.sepas);
//    }
//
//}

//function createSepas(data) {
//    $.each(data, function (i, json) {
//        if (($.inArray(domain, json.domains) != -1) || ($.inArray("DOMAIN_INDEPENDENT", json.domains) != -1)) {
//            var idString = "sepa" + i;
//            var $newSepa = $('<span>').attr({
//                id: idString,
//                class: "draggable-icon sepa tt",
//                "data-toggle": "tooltip",
//                "data-placement": "top",
//                title: json.name
//            }).data("JSON", json).on("contextmenu", staticContextMenu).appendTo('#sepas').show();
//            if (json.iconUrl == null) {
//                addTextIconToElement($newSepa, $newSepa.data("JSON").name);
//            } else {
//                $('<img>').attr("src", json.iconUrl).addClass("draggable-img").on("contextmenu", staticContextMenu).data("JSON", json)
//                    .error(function(){
//                        addTextIconToElement($(this).parent(), $(this).parent().data("JSON").name );
//                        $(this).remove();
//                    })
//                    .appendTo($newSepa);
//            }
//        }
//    });
//    makeDraggable("sepa");
//    initTooltips();
//}


/**
 * Displays Actions
 */
//function displayActions(e) {
//    $('#actionCollapse').attr("data-toggle", "collapse");
//    $('#actionCollapse').removeClass("disabled");
//    var url = standardUrl + "actions";
//
//
//    if (state.actions) {
//        createActions(state.actions);
//    } else {
//        $.getJSON(url).then(function (data) {
//            createActions(data);
//            state.actions = data;
//        });
//    }
//}

//function createActions(data) {
//    $.each(data, function (i, json) {
//        var idString = "action" + i;
//        var $newAction = $('<span>').attr({
//            id: idString,
//            class: "draggable-icon action tt",
//            "data-toggle": "tooltip",
//            "data-placement": "top",
//            title: json.name
//        }).data("JSON", json)
//            .on("contextmenu", staticContextMenu)
//            .appendTo('#actions')
//            .show();
//        if (json.iconUrl == null) {
//            addTextIconToElement($newAction, $newAction.data("JSON").name);
//        } else {
//            $('<img>')
//                .attr("src", json.iconUrl)
//                .addClass("draggable-img")
//                .on("contextmenu", staticContextMenu)
//                .data("JSON", json)
//                .error(function(){
//                    addTextIconToElement($(this).parent(), $(this).parent().data("JSON").name );
//                    $(this).remove();
//                })
//                .appendTo($newAction);
//        }
//    });
//    makeDraggable("action");
//    initTooltips();
//}


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
//function makeDraggable(type) {
//
//    if (type === "stream") {
//        $('.draggable-icon.stream').draggable({
//            revert: 'invalid',
//            helper: 'clone',
//            stack: '.draggable-icon',
//            start: function (stream, ui) {
//                //ui.helper.children().addClass("draggable-img-dragging");
//                //if ($('#sepas').css("opacity") === "0") {
//                //    $('.alpha').not('#sepas').fadeTo(300, .2);
//                    ui.helper.appendTo('#content');
//                //} else {
//                //    ui.helper.appendTo('#content');
//                //
//                //}
//
//                $('#assembly').css('border-color', 'red');
//            },
//            stop: function (stream, ui) {
//                //if ($('#sepas').css("opacity") == "0") {
//                //    $('.alpha').not('#sepas').fadeTo(300, 1);
//                //} else {
//                //    $('.alpha').fadeTo(300, 1);
//                //}
//                $('#assembly').css('border-color', '#666666');
//                //ui.helper.children().removeClass("draggable-img-dragging");
//            }
//        });
//    } else if (type === "sepa") {
//        $('.draggable-icon.sepa').draggable(standardDraggableOptions);
//    } else {
//        $('.draggable-icon.action').draggable(standardDraggableOptions);
//    }
//}

/**
 * Shows the contextmenu for given element
 * @param {Object} e
 */
function staticContextMenu(e) {
    $('#staticContextMenu').data("invokedOn", $(e.target)).show().css({
        position: "absolute",
        left: getLeftLocation(e, "static"),
        top: getTopLocation(e, "static")
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




function refresh(type, tabReset) {

    if (type == "Proa") {
        $('#typeChange').html("Proasense <b class='caret'></b>");
    } else if (type == "PA") {
        $('#typeChange').html("Personal Assistant <b class='caret'></b>");
    }
    //enableOptions();


    $('#sources').children().remove();
    $('#streams').children().remove();
    $('#streams').fadeTo(300, 0);
    $('#sepas').fadeTo(300, 0);
    clearAssembly();
    $('#streamCollapse').attr("data-toggle", "");
    $('#streamCollapse').addClass("disabled");
    hideAdjustingPipelineState();
    init(type);

    if (tabReset) {
        $("#tabs").find("a[href='#editor']").tab('show');
    }
}


function showAdd() {
    $("#sses").children().remove();
    $("#addText").val("");
    $('#addModal').modal('show');
}

function showManage() {
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
    $('.clickable').each(function (i) {
        var $src = $('<li>')
            .attr("id", "srcList" + i)
            .addClass("list-group-item")
            .html("<strong>" + $(this).data("JSON").name + "</strong>")
            .data("JSON", $(this).data("JSON"))
            .appendTo("#sourceList");

        addButtons($src, "sources");

        if ($(this).data("streams") != undefined) {
            var data = $(this).data("streams");

            $.each(data, function (i, json) {
                $('<li>')
                    .data("JSON", json)
                    .addClass("list-group-item")
                    .text(json.name)
                    .appendTo($src);
            });
        } else {
            var url = standardUrl + "sources/" + encodeURIComponent($(this).data("JSON").elementId) + "/events";
            // var url = standardUrl + "sources/user";
            var $origSrc = $(this);

            $.getJSON(url).then(function (data) {
                var savedStreams = data;
                $origSrc.data("streams", savedStreams);
                $.each(data, function (i, json) {
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
    if (!state.sepas) {
        var url = standardUrl + "sepas?domains=" + domain;
        //var url = standardUrl + "sepas/user"; //for user-specific elements

        $.getJSON(url).then(function (data) {
            state.sepas = data;
            $.each(data, function (i, json) {
                var $el = $('<li>')
                    .data("JSON", json)
                    .addClass("list-group-item")
                    .text(json.name)
                    .appendTo("#sepaList");
                addButtons($el, "sepas");
            });
        });
    } else {
        $.each(state.sepas, function (i, json) {
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
    if (!state.actions) {
        var url = standardUrl + "actions";
        // var url = standardUrl + "actions/user"; for user-sepcific elements
        $.getJSON(url).then(function (data) {
            state.actions = data;
            $.each(data, function (i, json) {
                var $el = $('<li>')
                    .data("JSON", json)
                    .addClass("list-group-item")
                    .text(json.name)
                    .appendTo("#actionList");
                addButtons($el, "actions");


            });
        });
    } else {
        $.each(state.actions, function (i, json) {
            var $el = $('<li>')
                .data("JSON", json)
                .addClass("list-group-item")
                .text(json.name)
                .appendTo("#actionList");
            addButtons($el, "actions");
        });
    }


}


function addButtons($element, type) {
    var $wrapper = $('<div>')
        .addClass("btn-group-xs pull-right");

    var $button = $('<button>')
        .addClass("btn btn-default")
        .attr("type", "button")
        .on('click', function (e) {
            var elementId = $element.data("JSON").elementId;
            var uri = encodeURIComponent(elementId);
            if (type == "sources") {
                url = standardUrl + "sources";
            } else if (type == "sepas") {
                url = standardUrl + "sepas";
            } else if (type == "actions") {
                url = standardUrl + "actions";
            }

            $.ajax({
                url: url,
                data: "uri=" + uri,
                processData: false,
                type: 'POST',
                success: function (data) {

                    if (data.success) {
                        toastRightTop("success", "Element successfully updated");
                        refresh("Proa");

                        switch (type) {
                            case "1":
                                break;
                            case "2":
                                refreshSepas();
                                break;
                            case "3":
                                refreshActions();
                        }
                    } else {
                        displayErrors(data);
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
        .on('click', function (e) {
            var elementId = $element.data("JSON").elementId;
            var uri = standardUrl + type + "/" + encodeURIComponent(elementId);

            $.ajax({
                url: uri,
                type: 'DELETE',
                success: function (result) {
                    if (result.success) {
                        $element.remove();
                        toastRightTop("success", "Element successfully deleted");
                        refresh("Proa");
                    } else {
                        displayErrors(result);
                    }


                }
            });
            if (type == "sepas") {
                refreshSepas();
            } else if (type == "actions") {
                refreshActions();
            }
        })
        .appendTo($wrapper);

    $('<span>')
        .addClass("glyphicon glyphicon-remove ")
        .appendTo($button);

    $wrapper.appendTo($element);


}

function refreshSepas() {
    var url = standardUrl + "sepas?domains=" + domain;
    $.getJSON(url).then(function (data) {
        state.sepas = data;
    });
}

function refreshActions() {
    var url = standardUrl + "actions";
    $.getJSON(url).then(function (data) {
        state.actions = data;
    });
}

function debugCircleMenuConsole(){
    var $el = $('.recommended-button>ul>li');
    console.log($el);
    console.log($el.data('plugin_circleMenu-pos-x'));
}






function manage(type) {

    $('#elementList').empty();

    switch (type) {

        case "stream":


            $('#descr').text($('#typeChange').text() + " - Streams");

            $('.clickable').each(function (e) {
                var url = standardUrl + "sources/" + encodeURIComponent($(this).data("JSON").elementId) + "/events";
                var streams = $.getJSON(url, function (data) {
                    $.each(data, function (i, json) {
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
                                "data-toggle": "popover",
                                "title": "JSON",
                                "data-content": JSON.stringify($(this).parent().data("JSON")),
                                "data-placement": "auto"
                            })
                            .popover();

                        $('<span>')
                            .addClass("glyphicon glyphicon-remove pull-right hoverable")
                            .on('click', function (e) {
                                var elementId = $(e.target).parent().data("JSON").elementId;
                                var uri = standardUrl + "streams/" + elementId;

                                $.ajax({
                                    url: uri,
                                    type: 'DELETE',
                                    success: function (result) {
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

function addTextIconToElement($element, name, small){
    var $span = $("<span>")
        .text(getElementIconText(name) || "NA")
        .attr(
        {"data-toggle": "tooltip",
            "data-placement": "top",
            "data-delay": '{"show": 1000, "hide": 100}',
            title: name
        })
        .appendTo($element);

    if (small){
        $span.addClass("element-text-icon-small")
    }else{
        $span.addClass("element-text-icon")
    }
}

function getElementIconText(string){
    var result ="";
    if (string.length <= 4){
        result = string;
    }else {
        var words = string.split(" ");
        words.forEach(function(word, i){
            result += word.charAt(0);
        });
    }
    return result.toUpperCase();
}


function toastTop(type, message, title, timeout) {

    if (!timeout) {
        toastr.options = {
            "newestOnTop": false,
            "positionClass": "toast-top-full-width"
        };
    } else {
        toastr.options = {
            "newestOnTop": false,
            "positionClass": "toast-top-full-width",
            "timeOut": 50000,
            "closeButton": true
        };
    }

    switch (type) {
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


function toastRightTop(type, message, title) {
    toastr.options = {
        "newestOnTop": false,
        "positionClass": "toast-top-right"
    };

    switch (type) {
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

function displayErrors(data) {
    for (var i = 0, notification; notification = data.notifications[i]; i++) {
        toastRightTop("error", notification.description, notification.title);
    }
}

function displaySuccess(data) {
    for (var i = 0, notification; notification = data.notifications[i]; i++) {
        toastRightTop("success", notification.description, notification.title);
    }
}


function disableOptions() {
    $('#options')
        .addClass("disabled")
        .children()
        .attr("data-toggle", "");

}
function enableOptions() {
    $('#options')
        .removeClass("disabled")
        .children()
        .attr("data-toggle", "dropdown");
}

function getParentWithJSONData($element) { //TODO umschreiben auf $.parents
    while ($element.data("JSON") == undefined) {
        $element = $element.parent();
    }
    return $element;
}

function prepareJsonLDModal(json){

    var path ="";
    if (state.currentElement.hasClass("stream")){
        path = "streams/";
    }else if (state.currentElement.hasClass("sepa")){
        path = "sepas/";
    }else{
        path = "actions/";
    }
    var url = standardUrl + path +  encodeURIComponent(json.elementId) +"/jsonld";
    //var url = "http://localhost:8080/semantic-epa-backend/api/actions/http%3A%2F%2Flocalhost%3A8091%2Ftable/jsonld";

    $.ajax({url : url, type : "GET", success : function(data){
        $("#modal-jsonld").text(data);
    }, error : function(data){
        $("#modal-jsonld").text("");
    }});

}
function openJsonLDModal(){
    $("#jsonldModal").modal('show');
}



function showTutorial() {
    $("#tutorialModal").modal('show');
}


