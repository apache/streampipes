function recElement(json){
    this.json = json;
    this.name = json.name;
    this.getjQueryElement = function(){
        return $('<a>')
            .data("recObject", this)
            .append($('<img>').attr("src", this.json.iconUrl).addClass("recommended-item-img"));

    };
}

