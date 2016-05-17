//function Block(name, description, pipeline){
//
//    var oi;
//    for (var i in pipeline.sepas){
//        if (jsPlumb.getConnections({source :pipeline.sepas[i].DOM}).length == 0){
//            oi = i;
//            break;
//        }
//    }
//    this.name = name;
//    this.description = description;
//    this.outputIndex = oi;
//    this.pipeline = pipeline;
//    this.getjQueryElement = function(){
//        return $('<div>')
//            .data("block", $.extend({},this))
//            .addClass("block")
//            .append($('<div>').addClass("block-name tt").text(this.name)
//                .attr({
//                    "data-toggle": "tooltip",
//                    "data-placement": "top",
//                    "data-delay": '{"show": 100, "hide": 100}',
//                    title: this.description
//                })
//            )
//            .append($('<div>').addClass("block-img-container")
//                .append($('<img>').addClass('block-img').attr("src", this.pipeline.streams[0].iconUrl)));
//    }
//    this.breakBlock = function(){
//        displayPipelineInAssembly($.extend({}, this.pipeline));
//    }
//}

//function blockElements(){
//    var block = createBlock();
//    if (block == false){
//        toastRightTop("error", "Please enter parameters for transparent elements (Right click -> Customize)", "Block Creation Error");
//        return;
//    }
//    //sendToServer();
//    var $selectedElements = $('.ui-selected');
//    var blockCoords = getMidpointOfAssemblyElements($selectedElements);
//    var $block = block.getjQueryElement()
//        .appendTo("#assembly")
//        .css({"top" : blockCoords.y, "left" : blockCoords.x, "position" : "absolute"});
//    initTooltips();
//    $('.block-name').flowtype({
//        minFont: 12,
//        maxFont: 25,
//        fontRatio: 10
//    });
//    jsPlumb.draggable($block, {containment: 'parent'});
//
//    $block.on("contextmenu", function(e){
//        $('#customize, #division ').hide();
//        $('#blockButton, #division1 ').show();
//        $('#blockButton').text("Revert to Pipeline");
//        $('#assemblyContextMenu')
//            .data("invokedOn", $(e.target))
//            .show()
//            .css({
//                position: "absolute",
//                left: getLeftLocation(e, "assembly"),
//                top: getTopLocation(e, "assembly")
//            });
//        ContextMenuClickHandler("assembly");
//        return false;
//    });
//
//    //CLEANUP
//    $selectedElements.each(function(i, element){
//        jsPlumb.remove(element);
//    });
//    //jsPlumb.remove($selectedElements);
//    //$selectedElements.remove();
//    //jsPlumb.deleteEveryEndpoint();
//    jsPlumb.addEndpoint($block, sepaEndpointOptions);
//
//
//}
//
//function createBlock(){
//    var blockData = $('#blockNameForm').serializeArray(); //TODO SAVE
//    //console.log(blockData);
//
//    var blockPipeline = new Pipeline();
//    $('.ui-selected').each(function(){
//        var $el = $(this)
//        if ($el.hasClass("sepa") && $el.data("JSON").staticProperties != null && $el.data("options")) {
//            blockPipeline.addElement(this);
//        }else if ($el.hasClass("stream")){
//            blockPipeline.addElement(this);
//        }else{
//            return false;
//        }
//    });
//    //console.log(blockPipeline);
//    var block = new Block(blockData[0].value, blockData[1].value, blockPipeline);
//    //console.log(block);
//
//    return block;
//
//}
//
//function getMidpointOfAssemblyElements($elements){
//    var maxLeft, minLeft, maxTop, minTop;
//
//    $elements.each(function(i, element){
//         var offsetObject = $(element).position();
//         if (i == 0){
//             maxLeft = offsetObject.left;
//             minLeft = offsetObject.left;
//             maxTop = offsetObject.top;
//             minTop = offsetObject.top;
//         }
//        else {
//             minLeft = Math.min(minLeft, offsetObject.left);
//             maxLeft = Math.max(maxLeft, offsetObject.left);
//             minTop = Math.min(minTop, offsetObject.top);
//             maxTop = Math.max(maxTop, offsetObject.top);
//         }
//    });
//    var midLeft = (minLeft + maxLeft) / 2;
//    var midTop = (minTop + maxTop) / 2;
//    return {x: midLeft, y: midTop};
//}
