function recElement(json){
    this.json = json;
    this.name = json.name;
    this.getjQueryElement = function(){
        return $('<a>')
            .data("recObject", this)
            .append($('<img>').attr("src", this.json.iconUrl).addClass("recommended-item-img"));

    };
}

function State(){
    this.adjustingPipelineState = false;
    this.plumbReady = false;
    this.sources = {};
    this.sepas = {};
    this.actions = {};
    this.currentElement = {};
    this.overWriteOldPipeline = false;
    this.currentPipeline;
}

