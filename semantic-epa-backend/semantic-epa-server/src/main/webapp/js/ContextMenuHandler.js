/**
 * Created by Cuddl3s on 03.06.2015.
 */

/**
 * Handles clicks in the contextmenu
 * @param {Object} type
 */
function ContextMenuClickHandler(type) {

    if (type === "assembly") {
        $('#assemblyContextMenu').off('click').on('click', function (e) {
            $(this).hide();

            var $invokedOn = $(this).data("invokedOn");
            var $selected = $(e.target);
            while ($invokedOn.parent().get(0) != $('#assembly').get(0)) {
                $invokedOn = $invokedOn.parent();

            }
            if ($selected.get(0) === $('#blockButton').get(0)){
                if ($invokedOn.hasClass("block")){
                    console.log({json: JSON.stringify($invokedOn.data("block").pipeline)});
                    displayPipelineInAssembly($.extend({}, $invokedOn.data("block").pipeline));
                    handleDeleteOption($invokedOn);
                    //$invokedOn.remove();
                }else{
                    $('#blockNameModal').modal('show');
                }
            }
            else if ($selected.get(0) === $('#delete').get(0)) {

                handleDeleteOption($invokedOn);

            } else if ($selected.get(0) === $('#customize').get(0)) {//Customize clicked

                $('#customize-content').html(prepareCustomizeModal($invokedOn));
                $('#customizeModal').modal('show');

            } else {
                handleJsonLDOption($invokedOn)
            }
        });
    } else if (type === "static") {
        $('#staticContextMenu').off('click').on('click', function (e) {
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

function handleDeleteOption($element){
    jsPlumb.removeAllEndpoints($element);

    $element.remove();

    if (!$('#assembly').children().hasClass('stream')) {
        $('#sepas').children().hide().fadeTo(300, 0);
        //$('#sepas').fadeTo(300, 0);
        $('#sepaCollapse').attr("data-toggle", "").addClass("disabled");
        //$('#sepaCollapse').addClass("disabled");
        $('#actionCollapse').attr("data-toggle", "").addClass("disabled");
        //$('#actionCollapse').addClass("disabled");
        $('#collapseOne').collapse('show');

    } else if (!$('#assembly').children().hasClass('sepa')) {
        $('#actions').children().hide().fadeTo(300, 0);
        //$('#actions').fadeTo(300, 0);
        $('#actionCollapse').attr("data-toggle", "").addClass("disabled");
        //$('#actionCollapse').addClass("disabled");
        $('#collapseTwo').collapse('show');
    } else if (!$('#assembly').children().hasClass('action')) {
        $('#collapseThree').collapse('show');
    }
}

function handleJsonLDOption($element) {
    var json = $element.data("JSON");
    $('#description-title').text(json.name);
    if (json.description) {$('#modal-description').text(json.description);}
    else {$('#modal-description').text("No description available");}
    $('#descrModal').modal('show');
    state.currentElement = $element;
    prepareJsonLDModal(json)
}