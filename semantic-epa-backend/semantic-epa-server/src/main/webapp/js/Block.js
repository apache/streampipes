function Block(name, description, pipeline){
    this.name = name;
    this.description = description;
    this.pipeline = pipeline;

}

function createBlock(){
    var blockData = $('#blockNameForm').serializeArray();
    console.log(blockData);

    var blockPipeline = {};
    blockPipeline.streams = [];
    blockPipeline.sepas = [];
    blockPipeline.action = {};
    $('.ui-selected').each(function(){
        addToPipeline(this, blockPipeline)
    });
    //console.log(blockPipeline);
    var block = new Block(blockData[0].value, blockData[1].value, blockPipeline);
    console.log(block);

}
