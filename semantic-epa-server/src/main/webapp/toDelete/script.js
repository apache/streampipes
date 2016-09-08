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


//(function($) {

/**
 * initiates tooltip functionality
 */



function debugCircleMenuConsole(){
    var $el = $('.recommended-button>ul>li');
    console.log($el);
    console.log($el.data('plugin_circleMenu-pos-x'));
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




function showTutorial() {
    $("#tutorialModal").modal('show');
}



